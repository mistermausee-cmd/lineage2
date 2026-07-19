/*
 * Copyright (c) 2013 L2jMobius
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package handlers.bypass.communityboard;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.config.RatesConfig;
import org.l2jmobius.gameserver.config.custom.CommunityBoardConfig;
import org.l2jmobius.gameserver.config.custom.PremiumSystemConfig;
import org.l2jmobius.gameserver.handler.CommunityBoardHandler;
import org.l2jmobius.gameserver.handler.IParseBoardHandler;
import org.l2jmobius.gameserver.managers.PremiumManager;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanAccess;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ShowBoard;
import org.l2jmobius.gameserver.network.serverpackets.WareHouseDepositList;
import org.l2jmobius.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * Вкладки Community Board: «Информация», «Премиум», «Склад».
 * @author Kiro
 */
public class ServiceBoard implements IParseBoardHandler
{
	private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/navigation.html";
	private static final int ADENA = 57;
	private static final SimpleDateFormat DATE = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	// Пакеты премиума: {дней, цена}. Цены — заглушки (адена).
	private static final long[][] PREMIUM_PACKAGES =
	{
		{
			3,
			50_000_000
		},
		{
			7,
			100_000_000
		},
		{
			30,
			350_000_000
		}
	};

	private static final String[] COMMANDS =
	{
		"_bbsinfo",
		"_bbspremium",
		"_bbswhpd",
		"_bbswhpw",
		"_bbswhcd",
		"_bbswhcw",
		"_bbswh"
	};

	@Override
	public String[] getCommandList()
	{
		return COMMANDS;
	}

	@Override
	public boolean onCommand(String command, Player player)
	{
		String html = null;

		if (command.equals("_bbsinfo"))
		{
			html = infoPage(player);
		}
		else if (command.startsWith("_bbspremium"))
		{
			if (command.contains(";"))
			{
				buyPremium(player, command);
			}
			html = premiumPage(player);
		}
		else if (command.startsWith("_bbswhpd"))
		{
			openPrivate(player, true);
			return false;
		}
		else if (command.startsWith("_bbswhpw"))
		{
			openPrivate(player, false);
			return false;
		}
		else if (command.startsWith("_bbswhcd"))
		{
			openClan(player, true);
			return false;
		}
		else if (command.startsWith("_bbswhcw"))
		{
			openClan(player, false);
			return false;
		}
		else if (command.startsWith("_bbswh"))
		{
			html = warehousePage(player);
		}

		if (html != null)
		{
			if (CommunityBoardConfig.CUSTOM_CB_ENABLED)
			{
				html = html.replace("%navigation%", navigation(player));
			}
			CommunityBoardHandler.separateAndSend(html, player);
		}
		return false;
	}

	private static String navigation(Player player)
	{
		return org.l2jmobius.gameserver.cache.HtmCache.getInstance().getHtm(player, NAVIGATION_PATH);
	}

	// ==================================================================
	// ИНФОРМАЦИЯ
	// ==================================================================
	private String infoPage(Player player)
	{
		final boolean prem = player.hasPremiumStatus();
		final Clan clan = player.getClan();
		final String clanName = (clan != null) ? clan.getName() + " (ур. " + clan.getLevel() + ")" : "нет";

		// Множители премиума (1, если премиум не активен).
		final double pXp = prem ? PremiumSystemConfig.PREMIUM_RATE_XP : 1;
		final double pSp = prem ? PremiumSystemConfig.PREMIUM_RATE_SP : 1;
		final double pDropAmt = prem ? PremiumSystemConfig.PREMIUM_RATE_DROP_AMOUNT : 1;
		final double pDropChance = prem ? PremiumSystemConfig.PREMIUM_RATE_DROP_CHANCE : 1;
		final double pSpoil = prem ? PremiumSystemConfig.PREMIUM_RATE_SPOIL_CHANCE : 1;
		final double pAdena = prem ? PremiumSystemConfig.PREMIUM_RATE_DROP_AMOUNT_BY_ID.getOrDefault(ADENA, 1f) : 1;

		// Персональные бонусы игрока (руны, предметы, вайталити) — из статов.
		final double bXp = player.getStat().getExpBonusMultiplier();
		final double bSp = player.getStat().getSpBonusMultiplier();
		final double bAdena = player.getStat().getValue(Stat.BONUS_DROP_ADENA, 1);
		final double bDropAmt = player.getStat().getValue(Stat.BONUS_DROP_AMOUNT, 1);
		final double bDropChance = player.getStat().getValue(Stat.BONUS_DROP_RATE, 1);
		final double bSpoil = player.getStat().getValue(Stat.BONUS_SPOIL_RATE, 1);

		final double baseAdena = RatesConfig.RATE_DROP_AMOUNT_BY_ID.getOrDefault(ADENA, 1f);

		final StringBuilder b = new StringBuilder();
		// --- Персонаж и сервер (по 2 пары в строке) ---
		b.append(sectionHeader("Персонаж"));
		b.append("<table width=505 border=0 cellpadding=2 cellspacing=0>");
		b.append(pairRow("Имя", player.getName(), "Класс", className(player)));
		b.append(pairRow("Уровень", Integer.toString(player.getLevel()), "Клан", clanName));
		b.append(pairRow("PvP / PK", player.getPvpKills() + " / " + player.getPkKills(), "Онлайн", onlineTime(player.getOnlineTimeMillis())));
		b.append(pairRow("Адена", fmt(player.getAdena()), "Премиум", premStatusShort(player)));
		b.append(pairRow("Хроники", "Grand Crusade", "Игроков", Integer.toString(World.getInstance().getPlayers().size())));
		b.append("</table>");
		b.append(divider());
		// --- Рейты (База × Премиум × Бонус = Итог) ---
		b.append(sectionHeader("Ваши рейты"));
		b.append("<table width=505 border=0 cellpadding=2 cellspacing=0>");
		b.append(rateHead());
		b.append(rateFull("Опыт (XP)", RatesConfig.RATE_XP, pXp, bXp));
		b.append(rateFull("Мастерство (SP)", RatesConfig.RATE_SP, pSp, bSp));
		b.append(rateFull("Адена", baseAdena, pAdena, bAdena));
		b.append(rateFull("Дроп (кол-во)", RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER, pDropAmt, bDropAmt));
		b.append(rateFull("Дроп (шанс)", RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER, pDropChance, bDropChance));
		b.append(rateFull("Спойл (шанс)", RatesConfig.RATE_SPOIL_DROP_CHANCE_MULTIPLIER, pSpoil, bSpoil));
		b.append("</table>");
		b.append("<br1><font color=\"696969\">Бонус — множитель от рун, предметов и вайталити. Итог = база \u00d7 премиум \u00d7 бонус.</font>");

		return wrap("ИНФОРМАЦИЯ", "Персонаж, сервер и ваши персональные рейты", b.toString(),
			"<font color=\"696969\">Lineage II \u2022 Grand Crusade</font>");
	}

	// ==================================================================
	// ПРЕМИУМ
	// ==================================================================
	private String premiumPage(Player player)
	{
		final long end = PremiumManager.getInstance().getPremiumExpiration(player.getAccountName());
		final long now = System.currentTimeMillis();
		final boolean active = end > now;

		final StringBuilder b = new StringBuilder();
		// Статус
		b.append(sectionHeader("Ваш статус"));
		b.append("<table width=500 border=0 cellpadding=2 cellspacing=0>");
		if (active)
		{
			b.append(row("Аккаунт", "<font color=\"00A5FF\">ПРЕМИУМ</font>"));
			b.append(row("Действует до", "<font color=\"F0C070\">" + DATE.format(end) + "</font>"));
			b.append(row("Осталось", "<font color=\"70FFCA\">" + remaining(end - now) + "</font>"));
		}
		else
		{
			b.append(row("Аккаунт", "<font color=\"808A99\">Обычный</font>"));
			b.append(row("Премиум", "не активен"));
		}
		b.append("</table>");
		b.append(divider());
		// Бонусы
		b.append(sectionHeader("Что даёт премиум"));
		b.append("<table width=500 border=0 cellpadding=2 cellspacing=0>");
		b.append(bonusRow("Опыт (XP)", RatesConfig.RATE_XP, RatesConfig.RATE_XP * PremiumSystemConfig.PREMIUM_RATE_XP));
		b.append(bonusRow("Мастерство (SP)", RatesConfig.RATE_SP, RatesConfig.RATE_SP * PremiumSystemConfig.PREMIUM_RATE_SP));
		b.append(bonusRow("Адена", RatesConfig.RATE_DROP_AMOUNT_BY_ID.getOrDefault(ADENA, 1f), RatesConfig.RATE_DROP_AMOUNT_BY_ID.getOrDefault(ADENA, 1f) * PremiumSystemConfig.PREMIUM_RATE_DROP_AMOUNT_BY_ID.getOrDefault(ADENA, 1f)));
		b.append(bonusRow("Кол-во дропа", RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER, RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER * PremiumSystemConfig.PREMIUM_RATE_DROP_AMOUNT));
		b.append(bonusRow("Шанс дропа", RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER, RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER * PremiumSystemConfig.PREMIUM_RATE_DROP_CHANCE));
		b.append("</table>");
		b.append(divider());
		// Покупка
		b.append(sectionHeader("Купить премиум"));
		b.append("<table border=0 cellpadding=4 cellspacing=0><tr>");
		for (long[] pkg : PREMIUM_PACKAGES)
		{
			final long days = pkg[0];
			final long price = pkg[1];
			b.append("<td align=center width=170>");
			b.append("<button value=\"").append(days).append(" ").append(daysWord(days)).append("\" action=\"bypass _bbspremium;").append(days);
			b.append("\" width=150 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
			b.append("<br1><font color=\"9AA4B0\">").append(fmt(price)).append(" аден</font>");
			b.append("</td>");
		}
		b.append("</tr></table>");
		b.append("<br><font color=\"696969\">Премиум действует на все персонажи аккаунта и не передаётся.</font>");

		return wrap("ПРЕМИУМ АККАУНТ", "Ускорьте развитие и добычу", b.toString(),
			"<button value=\"Информация\" action=\"bypass _bbsinfo\" width=150 height=28 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
	}

	private void buyPremium(Player player, String command)
	{
		if (!PremiumSystemConfig.PREMIUM_SYSTEM_ENABLED)
		{
			player.sendMessage("Премиум-система отключена.");
			return;
		}
		int days = 0;
		try
		{
			days = Integer.parseInt(command.substring(command.indexOf(';') + 1).trim());
		}
		catch (Exception e)
		{
			return;
		}
		long price = -1;
		for (long[] pkg : PREMIUM_PACKAGES)
		{
			if (pkg[0] == days)
			{
				price = pkg[1];
				break;
			}
		}
		if (price < 0)
		{
			return;
		}
		final int currency = CommunityBoardConfig.COMMUNITY_PREMIUM_COIN_ID;
		if (player.getInventory().getInventoryItemCount(currency, -1) < price)
		{
			player.sendMessage("Недостаточно адены для покупки премиума.");
			return;
		}
		if (!player.destroyItemByItemId(ItemProcessType.FEE, currency, price, player, true))
		{
			player.sendMessage("Недостаточно адены для покупки премиума.");
			return;
		}
		PremiumManager.getInstance().addPremiumTime(player.getAccountName(), days, TimeUnit.DAYS);
		player.sendMessage("Премиум активирован на " + days + " " + daysWord(days) + "!");
	}

	// ==================================================================
	// СКЛАД
	// ==================================================================
	private String warehousePage(Player player)
	{
		final Clan clan = player.getClan();
		final StringBuilder b = new StringBuilder();

		// --- Личный склад ---
		b.append(whCard(
			"ЛИЧНЫЙ СКЛАД",
			"Личное хранилище. Предметов на складе: <font color=\"F0F0F0\">" + player.getWarehouse().getSize() + "</font>",
			whBtn("Положить предметы", "_bbswhpd") + whBtn("Забрать предметы", "_bbswhpw")));

		b.append("<br><center><img src=\"L2UI.SquareGray\" width=505 height=1></center><br>");

		// --- Клановый склад ---
		if (clan == null)
		{
			b.append(whCard("КЛАНОВЫЙ СКЛАД",
				"<font color=\"808A99\">Доступен только участникам клана.<br1>Вы не состоите в клане.</font>", ""));
		}
		else if (clan.getLevel() < 1)
		{
			b.append(whCard("КЛАНОВЫЙ СКЛАД",
				"<font color=\"808A99\">Клан «" + clan.getName() + "» должен быть 1 уровня или выше.</font>", ""));
		}
		else
		{
			final boolean canWithdraw = player.hasAccess(ClanAccess.ACCESS_WAREHOUSE);
			String buttons = whBtn("Положить предметы", "_bbswhcd");
			buttons += canWithdraw ? whBtn("Забрать предметы", "_bbswhcw") : whBtnDisabled("Нет доступа");
			String desc = "Клан: <font color=\"F0F0F0\">" + clan.getName() + "</font>. Предметов: <font color=\"F0F0F0\">" + clan.getWarehouse().getSize() + "</font>";
			if (!canWithdraw)
			{
				desc += "<br1><font color=\"696969\">Забирать предметы могут только участники с правом доступа к складу.</font>";
			}
			b.append(whCard("КЛАНОВЫЙ СКЛАД", desc, buttons));
		}

		return wrap("СКЛАД", "Быстрый доступ к личному и клановому складу", b.toString(),
			"<font color=\"696969\">Lineage II \u2022 Grand Crusade</font>");
	}

	/** Карточка склада: крупный заголовок + описание + ряд кнопок по центру. */
	private static String whCard(String title, String desc, String buttons)
	{
		final StringBuilder c = new StringBuilder();
		c.append("<br><font name=\"hs12\" color=\"CDB67F\">").append(title).append("</font><br>");
		c.append("<br1><font color=\"9AA4B0\">").append(desc).append("</font><br>");
		if ((buttons != null) && !buttons.isEmpty())
		{
			c.append("<br><table border=0 cellpadding=4 cellspacing=0><tr>").append(buttons).append("</tr></table>");
		}
		return c.toString();
	}

	private static String whBtnDisabled(String label)
	{
		return "<td align=center><button value=\"" + label + "\" action=\"bypass _bbswh\" width=210 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF_Down\"></td>";
	}

	private void openPrivate(Player player, boolean deposit)
	{
		if (!GeneralConfig.ALLOW_WAREHOUSE || player.hasItemRequest())
		{
			return;
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.sendPacket(new ShowBoard());
		player.setActiveWarehouse(player.getWarehouse());
		if (deposit)
		{
			player.setInventoryBlockingStatus(true);
			player.sendPacket(new WareHouseDepositList(player, WareHouseDepositList.PRIVATE));
		}
		else
		{
			if (player.getActiveWarehouse().getSize() == 0)
			{
				player.sendMessage("На складе нет предметов.");
				return;
			}
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.PRIVATE));
		}
	}

	private void openClan(Player player, boolean deposit)
	{
		if (!GeneralConfig.ALLOW_WAREHOUSE || player.hasItemRequest())
		{
			return;
		}
		final Clan clan = player.getClan();
		if ((clan == null) || (clan.getLevel() < 1))
		{
			player.sendMessage("Клановый склад недоступен.");
			return;
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.sendPacket(new ShowBoard());
		if (deposit)
		{
			player.setActiveWarehouse(clan.getWarehouse());
			player.setInventoryBlockingStatus(true);
			player.sendPacket(new WareHouseDepositList(player, WareHouseDepositList.CLAN));
		}
		else
		{
			if (!player.hasAccess(ClanAccess.ACCESS_WAREHOUSE))
			{
				player.sendMessage("У вас нет права забирать предметы со склада клана.");
				return;
			}
			player.setActiveWarehouse(clan.getWarehouse());
			if (player.getActiveWarehouse().getSize() == 0)
			{
				player.sendMessage("На складе клана нет предметов.");
				return;
			}
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.CLAN));
		}
	}

	// ==================================================================
	// HTML-помощники (единый стиль магазина)
	// ==================================================================
	private static String wrap(String title, String subtitle, String body, String footer)
	{
		return "<html noscrollbar><body>"
			+ "<table width=700><tr><td height=6></td></tr></table>"
			+ "<table width=20><tr><td>%navigation%</td><td><center>"
			+ "<table border=0 cellpadding=0 cellspacing=0 width=565 height=474 background=\"L2UI_CT1.Windows_DF_TooltipBG\">"
			+ "<tr><td height=14></td></tr>"
			+ "<tr><td align=center><font name=\"hs12\" color=\"CDB67F\">" + title + "</font></td></tr>"
			+ "<tr><td align=center><font color=\"808A99\">" + subtitle + "</font></td></tr>"
			+ "<tr><td height=6></td></tr>"
			+ "<tr><td><center><img src=\"L2UI.SquareGray\" width=515 height=1></center></td></tr>"
			+ "<tr><td height=8></td></tr>"
			+ "<tr><td align=center>" + body + "</td></tr>"
			+ "<tr><td height=8></td></tr>"
			+ "<tr><td><center><img src=\"L2UI.SquareGray\" width=515 height=1></center></td></tr>"
			+ "<tr><td height=6></td></tr>"
			+ "<tr><td align=center>" + footer + "</td></tr>"
			+ "<tr><td height=4></td></tr>"
			+ "</table></center></td></tr></table>"
			+ "</body></html>";
	}

	private static String sectionHeader(String text)
	{
		return "<br><font name=\"hs10\" color=\"CDB67F\">" + text + "</font><br>";
	}

	private static String divider()
	{
		return "<br><img src=\"L2UI.SquareGray\" width=460 height=1><br>";
	}

	private static String row(String label, String value)
	{
		return "<tr><td width=220 align=right><font color=\"B0A070\">" + label + ":&nbsp;&nbsp;</font></td>"
			+ "<td width=280 align=left><font color=\"F0F0F0\">" + value + "</font></td></tr>";
	}

	private static String pairRow(String l1, String v1, String l2, String v2)
	{
		return "<tr>"
			+ "<td width=105 align=right><font color=\"B0A070\">" + l1 + ":&nbsp;</font></td>"
			+ "<td width=150 align=left><font color=\"F0F0F0\">" + v1 + "</font></td>"
			+ "<td width=100 align=right><font color=\"B0A070\">" + l2 + ":&nbsp;</font></td>"
			+ "<td width=150 align=left><font color=\"F0F0F0\">" + v2 + "</font></td>"
			+ "</tr>";
	}

	private static String rateHead()
	{
		return "<tr>"
			+ "<td width=145><font color=\"808A99\">Показатель</font></td>"
			+ "<td width=90 align=center><font color=\"808A99\">База</font></td>"
			+ "<td width=90 align=center><font color=\"808A99\">Премиум</font></td>"
			+ "<td width=90 align=center><font color=\"808A99\">Бонус</font></td>"
			+ "<td width=90 align=center><font color=\"808A99\">Итог</font></td>"
			+ "</tr>";
	}

	private static String rateFull(String label, double base, double prem, double bonus)
	{
		final double total = base * prem * bonus;
		final String premCell = (prem > 1.0001) ? "<font color=\"70FFCA\">x" + rateD(prem) + "</font>" : "<font color=\"696969\">\u2014</font>";
		final String bonusCell = (bonus > 1.0001) ? "<font color=\"70FFCA\">x" + rateD(bonus) + "</font>" : "<font color=\"696969\">\u2014</font>";
		return "<tr>"
			+ "<td width=145><font color=\"B0A070\">" + label + "</font></td>"
			+ "<td width=90 align=center><font color=\"9AA4B0\">x" + rateD(base) + "</font></td>"
			+ "<td width=90 align=center>" + premCell + "</td>"
			+ "<td width=90 align=center>" + bonusCell + "</td>"
			+ "<td width=90 align=center><font color=\"CDB67F\">x" + rateD(total) + "</font></td>"
			+ "</tr>";
	}

	private static String bonusRow(String label, float normal, float premium)
	{
		return "<tr><td width=220 align=right><font color=\"B0A070\">" + label + ":&nbsp;&nbsp;</font></td>"
			+ "<td width=280 align=left><font color=\"9AA4B0\">x" + rate(normal) + "</font> <font color=\"696969\">&gt;</font> <font color=\"70FFCA\">x" + rate(premium) + "</font></td></tr>";
	}

	private static String whBtn(String label, String bypass)
	{
		return "<td align=center><button value=\"" + label + "\" action=\"bypass " + bypass
			+ "\" width=210 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
	}

	private static String premStatusShort(Player player)
	{
		final long end = PremiumManager.getInstance().getPremiumExpiration(player.getAccountName());
		if (end > System.currentTimeMillis())
		{
			return "<font color=\"00A5FF\">Активен</font> (" + remaining(end - System.currentTimeMillis()) + ")";
		}
		return "<font color=\"808A99\">нет</font>";
	}

	// ==================================================================
	// Форматирование
	// ==================================================================
	private static String rate(float v)
	{
		if (v == Math.floor(v))
		{
			return Integer.toString((int) v);
		}
		return String.format(java.util.Locale.US, "%.1f", v);
	}

	private static String rateD(double v)
	{
		if (Math.abs(v - Math.rint(v)) < 0.001)
		{
			return Integer.toString((int) Math.rint(v));
		}
		String s = String.format(java.util.Locale.US, "%.2f", v);
		if (s.endsWith("0"))
		{
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	private static String fmt(long value)
	{
		return String.format(java.util.Locale.US, "%,d", value).replace(',', '.');
	}

	private static String remaining(long millis)
	{
		final long totalMinutes = millis / 60000;
		final long days = totalMinutes / 1440;
		final long hours = (totalMinutes % 1440) / 60;
		final long minutes = totalMinutes % 60;
		if (days > 0)
		{
			return days + " дн. " + hours + " ч.";
		}
		if (hours > 0)
		{
			return hours + " ч. " + minutes + " мин.";
		}
		return minutes + " мин.";
	}

	private static String onlineTime(long millis)
	{
		final long totalMinutes = millis / 60000;
		final long hours = totalMinutes / 60;
		final long minutes = totalMinutes % 60;
		return hours + " ч. " + minutes + " мин.";
	}

	private static String daysWord(long days)
	{
		final long d = days % 100;
		if ((d >= 11) && (d <= 14))
		{
			return "дней";
		}
		switch ((int) (days % 10))
		{
			case 1:
			{
				return "день";
			}
			case 2:
			case 3:
			case 4:
			{
				return "дня";
			}
			default:
			{
				return "дней";
			}
		}
	}

	private static String className(Player player)
	{
		String n = player.getPlayerClass().name().toLowerCase().replace('_', ' ');
		if (n.length() > 0)
		{
			n = Character.toUpperCase(n.charAt(0)) + n.substring(1);
		}
		return n;
	}
}
