package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Bonux
 */
public class NewbieGuideInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int MAX_SUPPORT_LEVEL = 90;

	private static final int[][] BUFF_SETS = new int[][]{
		{ 15642, 1 }, // Путешественник - Поэма Рога
		{ 15643, 1 }, // Путешественник - Поэма Барабана
		{ 15644, 1 }, // Путешественник - Поэма Органа
		{ 15645, 1 }, // Путешественник - Поэма Гитары
		{ 15646, 1 }, // Путешественник - Поэма Арфы
		{ 15647, 1 },  // Путешественник - Поэма Лютни
		{ 15651, 1 },  // Путешественник - Соната Битвы
		{ 15652, 1 },  // Путешественник - Соната Движения
		{ 15653, 1 }  // Путешественник - Соната Расслабления
	};

	private static final int[] KNIGHTS_HARMONY = { 15648, 1 }; // Путешественник - Гармония Стража
	private static final int[] WARRIORS_HARMONY = { 15649, 1 }; // Путешественник - Гармония Берсерка
	private static final int[] WIZARDS_HARMONY = { 15650, 1 }; // Путешественник - Гармония Мага

	private static final int[] BLESSING_OF_PROTECTION = { 5182, 1 }; // Благословение Защиты

	private static int TIPS = -1;

	private static final int ADVENTURER_SUPPORT_GOODS = 32241; // Вещи Поддержки Путешественника
	private static final String ADVENTURER_SUPPORT_VAR = "@received_advent_support";

	private static final SchedulingPattern RESTART_DATE_PATTERN = new SchedulingPattern("30 6 * * *");

	public NewbieGuideInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "newbie_guide/";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("buffs"))
		{
			if(player.getLevel() > MAX_SUPPORT_LEVEL || MAX_SUPPORT_LEVEL <= 85 && player.getClassId().isAwaked())
			{
				showChatWindow(player, "newbie_guide/blessing_list002.htm", false);
				return;
			}

			for(int[] skill : BUFF_SETS)
				getBuff(skill[0], skill[1], player);

			int setId = Integer.parseInt(st.nextToken());
			switch(setId)
			{
				case 1:
					getBuff(KNIGHTS_HARMONY[0], KNIGHTS_HARMONY[1], player);
					break;
				case 2:
					getBuff(WARRIORS_HARMONY[0], WARRIORS_HARMONY[1], player);
					break;
				case 3:
					getBuff(WIZARDS_HARMONY[0], WIZARDS_HARMONY[1], player);
					break;
			}

			if(!player.isPK() && player.getLevel() <= 39 && player.getClassLevel().ordinal() < ClassLevel.SECOND.ordinal())
				getBuff(BLESSING_OF_PROTECTION[0], BLESSING_OF_PROTECTION[1], player);
		}
		else if(cmd.equalsIgnoreCase("receivebless"))
		{
			if(player.isPK() || player.getLevel() > 39 || player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal())
			{
				showChatWindow(player, "newbie_guide/pk_protect002.htm", false);
				return;
			}

			getBuff(BLESSING_OF_PROTECTION[0], BLESSING_OF_PROTECTION[1], player);
		}
		else if(cmd.equalsIgnoreCase("easeshilien"))
		{
			if(player.getDeathPenalty().getLevel() < 3)
			{
				showChatWindow(player, "newbie_guide/ease_shilien001.htm", false);
				return;
			}

			player.getDeathPenalty().reduceLevel();
		}
		else if(cmd.equalsIgnoreCase("bid1"))
		{
			int[][] list = new int[][]{{36551, 1}, {36552, 1}, {36546, 1}, {36547, 1}, {36556, 1}, {36557, 1}, {36526, 1}, {36516, 1}, {36517, 1}, {36521, 1}, {36522, 1}, {36527, 1},
			{36531, 1}, {36532, 1}, {36536, 1}, {36537, 1}, {36541, 1}, {36542, 1}, {36546, 1}, {36547, 1},
			{36551, 1}, {36552, 1}, {36556, 1}, {36557, 1}, {36561, 1}, {36562, 1}};
			double[] chances = new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

			if(!ItemFunctions.deleteItem(player, 37045, 1, true))
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm", false);
				return;
			}
			RandomOneItem(player, list, chances);
		}
		else if(cmd.equalsIgnoreCase("bid10"))
		{
			int[][] list = new int[][]{{36551, 1}, {36552, 1}, {36546, 1}, {36547, 1}, {36556, 1}, {36557, 1}, {36526, 1}, {36516, 1}, {36517, 1}, {36521, 1}, {36522, 1}, {36527, 1},
			{36531, 1}, {36532, 1}, {36536, 1}, {36537, 1}, {36541, 1}, {36542, 1}, {36546, 1}, {36547, 1},
			{36551, 1}, {36552, 1}, {36556, 1}, {36557, 1}, {36561, 1}, {36562, 1},
			{9546, 1}, {9547, 1}, {9548, 1}, {9549, 1}, {9550, 1}, {9551, 1}};
			double[] chances = new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 8, 8, 8, 8, 8, 8};

			if(!ItemFunctions.haveItem(player, 37045, 10) || !ItemFunctions.haveItem(player, 57, 50000))
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm", false);
				return;
			}
			else
			{
				ItemFunctions.deleteItem(player, 37045, 10, true);
				ItemFunctions.deleteItem(player, 57, 50000, true);
				RandomOneItem(player, list, chances);
			}
		}
		else if(cmd.equalsIgnoreCase("bid50"))
		{
			int[][] list = new int[][]{{9546, 1}, {9547, 1}, {9548, 1}, {9549, 1}, {9550, 1}, {9551, 1}, {9552, 1}, {9553, 1}, {9554, 1}, {9555, 1}, {9556, 1}, {9557, 1}};
			double[] chances = new double[]{10, 10, 10, 10, 10, 10, 3, 3, 3, 3, 3, 3};
			if(!ItemFunctions.haveItem(player, 37045, 50) || !ItemFunctions.haveItem(player, 57, 500000))
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm", false);
				return;
			}
			else
			{
				ItemFunctions.deleteItem(player, 37045, 50, true);
				ItemFunctions.deleteItem(player, 57, 500000, true);
				RandomOneItem(player, list, chances);
			}
		}
		else if(cmd.equalsIgnoreCase("bid2000"))
		{
			if(player.getLevel() < 86)
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_level.htm", false);
				return;
			}
			if(!ItemFunctions.deleteItem(player, 37045, 2000, true))
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm", false);
				return;
			}
			else
			{
				ItemFunctions.addItem(player, 38600, 1, true);
				ItemFunctions.addItem(player, 38601, 2, true);
			}

		}
		else if(cmd.equalsIgnoreCase("tips"))
		{
			if(!player.getVarBoolean(ADVENTURER_SUPPORT_VAR))
			{
				long restartTime = RESTART_DATE_PATTERN.next(System.currentTimeMillis());
				if(restartTime < System.currentTimeMillis()) // Заглушка, крон не умеет работать с секундами.
					restartTime += 86400000L; // Добавляем сутки.

				player.setVar(ADVENTURER_SUPPORT_VAR, "true", restartTime);
				ItemFunctions.addItem(player, ADVENTURER_SUPPORT_GOODS, 1L, true);
			}

			if(TIPS < 0)
			{
				int i = 0;
				while(true)
				{
					i++;
					String html = HtmCache.getInstance().getIfExists("newbie_guide/tips/tip-" + i + ".htm", player);
					if(html == null)
					{
						TIPS = i - 1;
						break;
					}
				}
			}
			showChatWindow(player, "newbie_guide/tips/tip-" + Rnd.get(1, TIPS) + ".htm", false);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private static boolean RandomOneItem(Player player, int[][] items, double[] chances)
	{
		if(items.length != chances.length)
			return false;

		double extractChance = 0;
		for(double c : chances)
			extractChance += c;

			int[] successfulItems = new int[0];
			while(successfulItems.length == 0)
				for(int i = 0; i < items.length; i++)
					if(Rnd.chance(chances[i]))
						successfulItems = ArrayUtils.add(successfulItems, i);
			int[] item = items[successfulItems[Rnd.get(successfulItems.length)]];
			if(item.length < 2)
				return false;

			ItemFunctions.addItem(player, item[0], item[1]);
		return true;
	}


	private void getBuff(int skillId, int skillLevel, Player player)
	{
		Skill skill = SkillHolder.getInstance().getSkill(skillId, skillLevel);
		if(skill == null)
			return;

		int removed = player.getAbnormalList().stop(skill);
		if(removed > 0)
			player.sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skill.getId(), skill.getLevel()));

		forceUseSkill(skill, player);
	}
}