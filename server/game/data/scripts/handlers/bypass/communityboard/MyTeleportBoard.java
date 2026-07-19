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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.config.custom.CommunityBoardConfig;
import org.l2jmobius.gameserver.handler.CommunityBoardHandler;
import org.l2jmobius.gameserver.handler.IParseBoardHandler;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.network.serverpackets.ShowBoard;

/**
 * Личные метки телепортации ("Мой телепорт").
 * Игрок может создать метку в точке, где стоит, за 1.000.000 адены, дать ей имя,
 * телепортироваться к ней и удалить её (с подтверждением).
 * Телепорт к метке: бесплатно до 99 ур. включительно, далее 100.000 адены.
 * @author Kiro
 */
public class MyTeleportBoard implements IParseBoardHandler
{
	private static final Logger LOG = Logger.getLogger(MyTeleportBoard.class.getName());
	private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/navigation.html";
	private static final String BASE = "data/html/CommunityBoard/Custom/mytp/";

	// Стоимость создания метки и телепорта.
	private static final long MARK_PRICE = 1_000_000;
	private static final long TELEPORT_HIGH_PRICE = 100_000;
	private static final int MAX_MARKS = 10;
	private static final int NAME_MAX_LEN = 24;

	// SQL
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `bbs_teleport_marks` (" +
		"`playerId` INT UNSIGNED NOT NULL DEFAULT 0," +
		"`markId` INT UNSIGNED NOT NULL DEFAULT 0," +
		"`name` VARCHAR(64) NOT NULL DEFAULT ''," +
		"`x` INT NOT NULL DEFAULT 0," +
		"`y` INT NOT NULL DEFAULT 0," +
		"`z` INT NOT NULL DEFAULT 0," +
		"PRIMARY KEY (`playerId`,`markId`))";
	private static final String SELECT_MARKS = "SELECT `markId`,`name`,`x`,`y`,`z` FROM `bbs_teleport_marks` WHERE `playerId`=? ORDER BY `markId`";
	private static final String SELECT_MARK = "SELECT `name`,`x`,`y`,`z` FROM `bbs_teleport_marks` WHERE `playerId`=? AND `markId`=?";
	private static final String COUNT_MARKS = "SELECT COUNT(*) AS cnt, COALESCE(MAX(`markId`),0) AS maxId FROM `bbs_teleport_marks` WHERE `playerId`=?";
	private static final String INSERT_MARK = "INSERT INTO `bbs_teleport_marks` (`playerId`,`markId`,`name`,`x`,`y`,`z`) VALUES (?,?,?,?,?,?)";
	private static final String DELETE_MARK = "DELETE FROM `bbs_teleport_marks` WHERE `playerId`=? AND `markId`=?";

	private static final String[] COMMANDS =
	{
		"_bbsmytp",
		"_bbstpadd",
		"_bbstpgo",
		"_bbstpdelok",
		"_bbstpdel"
	};

	public MyTeleportBoard()
	{
		// Гарантируем наличие таблицы при загрузке хендлера.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(CREATE_TABLE))
		{
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Could not create bbs_teleport_marks table: " + e.getMessage());
		}
	}

	@Override
	public String[] getCommandList()
	{
		return COMMANDS;
	}

	@Override
	public boolean onCommand(String command, Player player)
	{
		String returnHtml = null;

		if (command.equals("_bbsmytp"))
		{
			returnHtml = listPage(player, null);
		}
		else if (command.startsWith("_bbstpadd"))
		{
			returnHtml = addMark(player, command);
		}
		else if (command.startsWith("_bbstpgo"))
		{
			teleportToMark(player, parseId(command));
			return false;
		}
		else if (command.startsWith("_bbstpdelok"))
		{
			deleteMark(player, parseId(command));
			returnHtml = listPage(player, "Метка удалена.");
		}
		else if (command.startsWith("_bbstpdel"))
		{
			returnHtml = confirmPage(player, parseId(command));
		}

		if (returnHtml != null)
		{
			if (CommunityBoardConfig.CUSTOM_CB_ENABLED)
			{
				final String navigation = HtmCache.getInstance().getHtm(player, NAVIGATION_PATH);
				returnHtml = returnHtml.replace("%navigation%", navigation);
			}
			CommunityBoardHandler.separateAndSend(returnHtml, player);
		}
		return false;
	}

	// ---------------------------------------------------------------------
	// Страницы
	// ---------------------------------------------------------------------
	private String listPage(Player player, String message)
	{
		String html = HtmCache.getInstance().getHtm(player, BASE + "main.html");
		final List<int[]> ids = new ArrayList<>();
		final List<String> names = new ArrayList<>();
		int count = 0;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MARKS))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					ids.add(new int[]
					{
						rs.getInt("markId")
					});
					names.add(rs.getString("name"));
					count++;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": listPage: " + e.getMessage());
		}

		final StringBuilder sb = new StringBuilder();
		if (count == 0)
		{
			sb.append("<br><table width=470 border=0><tr><td align=center><font color=\"808A99\">У вас пока нет меток.<br1>Встаньте в нужном месте, введите название<br1>и нажмите кнопку «Создать метку».</font></td></tr></table><br>");
		}
		else
		{
			sb.append("<table border=0 cellpadding=1 cellspacing=2 width=515>");
			for (int i = 0; i < ids.size(); i++)
			{
				final int markId = ids.get(i)[0];
				final String name = names.get(i);
				sb.append("<tr>");
				sb.append("<td align=center><button value=\"").append(name).append("\" action=\"bypass _bbstpgo;").append(markId);
				sb.append("\" width=390 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				sb.append("<td align=center><button value=\"Удалить\" action=\"bypass _bbstpdel;").append(markId);
				sb.append("\" width=105 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
		}

		final String note = (message != null) ? ("<font color=\"CDB67F\">" + message + "</font>") : ("<font color=\"696969\">Телепорт к метке: до 99 ур. бесплатно, далее 100.000 адены.</font>");
		html = html.replace("%marks%", sb.toString());
		html = html.replace("%count%", Integer.toString(count));
		html = html.replace("%max%", Integer.toString(MAX_MARKS));
		html = html.replace("%note%", note);
		return html;
	}

	private String confirmPage(Player player, int markId)
	{
		String name = null;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MARK))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, markId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					name = rs.getString("name");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": confirmPage: " + e.getMessage());
		}

		if (name == null)
		{
			return listPage(player, "Метка не найдена.");
		}

		String html = HtmCache.getInstance().getHtm(player, BASE + "confirm.html");
		html = html.replace("%name%", name);
		html = html.replace("%id%", Integer.toString(markId));
		return html;
	}

	// ---------------------------------------------------------------------
	// Действия
	// ---------------------------------------------------------------------
	private String addMark(Player player, String command)
	{
		String name = "";
		if (command.length() > "_bbstpadd".length())
		{
			name = command.substring("_bbstpadd".length()).trim();
		}
		name = sanitize(name);
		if (name.isEmpty() || name.equalsIgnoreCase("$tpname"))
		{
			return listPage(player, "Введите название метки.");
		}

		// Проверка лимита.
		int count = 0;
		int nextId = 1;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(COUNT_MARKS))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					count = rs.getInt("cnt");
					nextId = rs.getInt("maxId") + 1;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": addMark count: " + e.getMessage());
		}

		if (count >= MAX_MARKS)
		{
			return listPage(player, "Достигнут лимит меток (" + MAX_MARKS + "). Удалите ненужные.");
		}

		if (player.getInventory().getInventoryItemCount(CommunityBoardConfig.COMMUNITYBOARD_CURRENCY, -1) < MARK_PRICE)
		{
			return listPage(player, "Недостаточно адены (нужно 1.000.000).");
		}

		if (!player.destroyItemByItemId(ItemProcessType.FEE, CommunityBoardConfig.COMMUNITYBOARD_CURRENCY, MARK_PRICE, player, true))
		{
			return listPage(player, "Недостаточно адены (нужно 1.000.000).");
		}

		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_MARK))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, nextId);
			ps.setString(3, name);
			ps.setInt(4, player.getX());
			ps.setInt(5, player.getY());
			ps.setInt(6, player.getZ());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": addMark insert: " + e.getMessage());
			return listPage(player, "Ошибка при создании метки.");
		}

		return listPage(player, "Метка «" + name + "» создана.");
	}

	private void deleteMark(Player player, int markId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_MARK))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, markId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": deleteMark: " + e.getMessage());
		}
	}

	private void teleportToMark(Player player, int markId)
	{
		int x = 0;
		int y = 0;
		int z = 0;
		boolean found = false;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MARK))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, markId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					x = rs.getInt("x");
					y = rs.getInt("y");
					z = rs.getInt("z");
					found = true;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": teleportToMark: " + e.getMessage());
		}

		if (!found)
		{
			player.sendMessage("Метка не найдена.");
			return;
		}

		final long teleportPrice = (player.getLevel() > 99) ? TELEPORT_HIGH_PRICE : 0;
		if (player.getInventory().getInventoryItemCount(CommunityBoardConfig.COMMUNITYBOARD_CURRENCY, -1) < teleportPrice)
		{
			player.sendMessage("Not enough currency!");
			return;
		}

		player.disableAllSkills();
		player.sendPacket(new ShowBoard());
		if (teleportPrice > 0)
		{
			player.destroyItemByItemId(ItemProcessType.FEE, CommunityBoardConfig.COMMUNITYBOARD_CURRENCY, teleportPrice, player, true);
		}
		player.setInstanceById(0);
		player.teleToLocation(new Location(x, y, z), 0);
		ThreadPool.schedule(player::enableAllSkills, 3000);
	}

	// ---------------------------------------------------------------------
	// Утилиты
	// ---------------------------------------------------------------------
	private static int parseId(String command)
	{
		try
		{
			final int idx = command.indexOf(';');
			if (idx < 0)
			{
				return -1;
			}
			return Integer.parseInt(command.substring(idx + 1).trim());
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	private static String sanitize(String name)
	{
		if (name == null)
		{
			return "";
		}
		// Убираем символы, опасные для HTML/bypass.
		name = name.replaceAll("[<>\"'$;&#\\\\/%]", "").trim();
		if (name.length() > NAME_MAX_LEN)
		{
			name = name.substring(0, NAME_MAX_LEN);
		}
		return name;
	}
}
