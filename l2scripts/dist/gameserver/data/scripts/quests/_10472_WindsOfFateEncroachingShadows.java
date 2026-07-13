package quests;

import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.skills.SkillEntry;

/**
 * @author Bonux
**/
/*
 * TODO:
 * На 3й статидии выделять у ГК тп в Глудио.
 * На 5й статидии выделять у ГК тп в Глудио.
 * На 19й статидии выделять у ГК тп в Деревню Говорящего Острова.
**/
public class _10472_WindsOfFateEncroachingShadows extends Quest
{
	// NPC's
	private static final int QUEEN_NAVARI = 33931;	// Королева Серения
	private static final int ALCHEMIST_ZEPHYRA = 33978;	// Алхимик Зефира
	private static final int BLACKSMITH_MOMET = 33998;	// Кузнец Ивни
	private static final int BLACK_MARKETEER_OF_MAMMON = 31092;	// Контрабандист Маммона
	private static final int BLACKSMITH_OF_MAMMON = 31126;	// Кузнец Маммона
	private static final int AGENT_OF_CHAOS_HARDIN = 33870;	// Хардин Агент Хаоса
	private static final int POWERFUL_DEFENSIVE_TANKS = 33397;	// Рыцарь Сигеля - Мастер Защиты Сила перерождения Рыцаря
	private static final int POWERFUL_MELEE_DAMAGE_DEALER = 33398;	// Воин Тира - Мастер Оружия/Силы Сила перерождения Воина
	private static final int POWERFUL_DAGGER_MELEE_DAMAGE_DEALER = 33399;	// Разбойник Одала - Мастер Кинжалов Сила перерождения Разбойника
	private static final int POWERFUL_BOW_CROSSBOW_SHARPSHOOTERS = 33400;	// Лучник Эура - Мастер Лука/Арбалета Сила перерождения Лучника
	private static final int POWERFUL_MAGIC_DAMAGE_DEALER = 33401;	// Волшебник Фео - Мастер Магии Сила перерождения Волшебника
	private static final int POWERFUL_MAGIC_BUFFER = 33402;	// Заклинатель Иса - Мастер Чар Сила перерождения Заклинателя
	private static final int POWERFUL_MAGIC_SUMMONER = 33403;	// Призыватель Веньо - Мастер Призыва Сила перерождения Призывателя
	private static final int POWERFUL_MAGIC_HEALER = 33404;	// Целитель Альгиза - Мастер Лечения Сила перерождения Целителя
	private static final int KARLA = 33933;	// Калли Серебан 
	private static final int SUB_DUAL_CLASS_MASTER_RAINA = 33491;	// Мастер Подклассов/Двойных Классов Реан

	// Monster's
	private static final int ARBITOR_OF_DARKNESS = 23174;	// Палач Тьмы
	private static final int ALTAR_OF_EVIL_SPIRIT_OFFERING_BOX = 23175;	// Демоническая Жертвенница Алтаря
	private static final int MUTATED_CERBEROS = 23176;	// Мутировавший Цербер
	private static final int DARTANION = 23177;	// Дартанион
	private static final int INSANE_PHION = 23178;	// Буйный Пайон
	private static final int DIMENSIONAL_RIFTER = 23179;	// Пространственный Бунтарь
	private static final int HELLGATE_FIGHTING_DOG  = 23180;	// Цербер

	// Item's
	private static final int DARK_FRAGMENT = 40060;	// Наполненный Силой Тьмы Осколок
	private static final int COUNTERFEIT_ATELIA = 40059;	// Поддельный Астатин
	private static final int RED_SOUL_CRYSTAL_STAGE_15 = 10480;	// Красный Кристалл Души - Уровень 15
	private static final int BLUE_SOUL_CRYSTAL_STAGE_15 = 10481;	// Синий Кристалл Души - Уровень 15
	private static final int GREEN_SOUL_CRYSTAL_STAGE_15 = 10482;	// Зеленый Кристалл Души - Уровень 15
	private static final int FIRE_STONE = 9546;	// Руда Огня
	private static final int WATER_STONE = 9547;	// Руда Воды
	private static final int EARTH_STONE = 9548;	// Руда Земли
	private static final int WIND_STONE = 9549;	// Руда Ветра
	private static final int DARK_STONE = 9550;	// Руда Тьмы
	private static final int HOLY_STONE = 9551;	// Руда Святости
	private static final int RECIPE_TWILIGHT_NECKLACE_60 = 36791;	// Рецепт: Ожерелье Ада (60%)
	private static final int CRYSTAL_R_GRADE = 17371;	// Кристалл: Ранг R

	// Skills
	private static final SkillEntry ABSORB_WIND = SkillHolder.getInstance().getSkillEntry(16389, 1);	// Поглощение Силы Ветра
	private static final SkillEntry WYNN_SUMMONERS_POWER = SkillHolder.getInstance().getSkillEntry(16390, 1);	// Сила Призывателя Венью
	private static final SkillEntry FEOH_WIZARDS_POWER = SkillHolder.getInstance().getSkillEntry(16391, 1);	// Сила Волшебника Фео
	private static final SkillEntry TYRR_WARRIORS_POWER = SkillHolder.getInstance().getSkillEntry(16392, 1);	// Сила Воина Тира
	private static final SkillEntry OTHELL_ROGUES_POWER = SkillHolder.getInstance().getSkillEntry(16393, 1);	// Сила Разбойника Одала
	private static final SkillEntry ISS_ENCHANTERS_POWER = SkillHolder.getInstance().getSkillEntry(16394, 1);	// Сила Заклинателя Иса
	private static final SkillEntry YUL_ARCHERS_POWER = SkillHolder.getInstance().getSkillEntry(16395, 1);	// Сила Лучника Эура
	private static final SkillEntry SIGEL_KNIGHTS_POWER = SkillHolder.getInstance().getSkillEntry(16396, 1);	// Сила Рыцаря Сигеля
	private static final SkillEntry AEORE_HEALERS_POWER = SkillHolder.getInstance().getSkillEntry(16397, 1);	// Сила Целителя Альгиза
	private static final SkillEntry ATELIA_ENERGY = SkillHolder.getInstance().getSkillEntry(16398, 1);	// Энергия Астата
	private static final SkillEntry FERINS_CURE = SkillHolder.getInstance().getSkillEntry(16399, 1);	// Восстановление Эфира
	//private static final SkillEntry FERINS_RECHARGE = SkillHolder.getInstance().getSkillEntry(16400, 1);	// Перезарядка Эфира

	// Rewards
	private static final long EXP_REWARD = 175739575;	// Награда EXP.
	private static final int SP_REWARD = 42177;	// Награда SP.

	// Other
	private static final double STATE_4_DARK_FRAGMENT_DROP_CHANCE = 50.;	// Шанс дропа: Наполненный Силой Тьмы Осколок.
	private static final int STATE_4_DARK_FRAGMENT_NEED_ITEMS_COUNT = 50;	//	Количество необходимых: Наполненный Силой Тьмы Осколок.
	private static final int[] STATE_4_MONSTERS_KILL_LIST = { ARBITOR_OF_DARKNESS, ALTAR_OF_EVIL_SPIRIT_OFFERING_BOX, MUTATED_CERBEROS, DARTANION, INSANE_PHION, DIMENSIONAL_RIFTER, HELLGATE_FIGHTING_DOG };	// Монстры с который дропает: Наполненный Силой Тьмы Осколок, на N стадии.	public _10472_WindsOfFateEncroachingShadows()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(QUEEN_NAVARI);
		addTalkId(QUEEN_NAVARI, ALCHEMIST_ZEPHYRA, BLACKSMITH_MOMET, BLACK_MARKETEER_OF_MAMMON, BLACKSMITH_OF_MAMMON, AGENT_OF_CHAOS_HARDIN, KARLA, SUB_DUAL_CLASS_MASTER_RAINA);
		addFirstTalkId(POWERFUL_DEFENSIVE_TANKS, POWERFUL_MELEE_DAMAGE_DEALER, POWERFUL_DAGGER_MELEE_DAMAGE_DEALER, POWERFUL_BOW_CROSSBOW_SHARPSHOOTERS, POWERFUL_MAGIC_DAMAGE_DEALER, POWERFUL_MAGIC_BUFFER, POWERFUL_MAGIC_SUMMONER, POWERFUL_MAGIC_HEALER);
		addKillId(STATE_4_MONSTERS_KILL_LIST);

		addQuestItem(DARK_FRAGMENT, COUNTERFEIT_ATELIA);

		addLevelCheck("queen_navari_q10472_00.htm", 85);
		addClassLevelCheck("queen_navari_q10472_00.htm", true, ClassLevel.THIRD);
		addRaceCheck("queen_navari_q10472_00.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final int cond = st.getCond();

		StringTokenizer stkn = new StringTokenizer(event, " ");

		String htmltext = stkn.nextToken();

		if(htmltext.equalsIgnoreCase("queen_navari_q10472_05.htm"))
		{
			if(cond == 0)
			{
				st.setCond(1);
			}
		}
		else if(htmltext.equalsIgnoreCase("alchemist_zephyra_q10472_02.htm"))
		{
			if(cond == 1)
			{
				st.setCond(2);
			}
		}
		else if(htmltext.equalsIgnoreCase("blacksmith_momet_q10472_03.htm"))
		{
			if(cond == 2)
			{
				st.setCond(3);
			}
		}
		else if(htmltext.equalsIgnoreCase("black_marketeer_of_mammon_q10472_02.htm"))
		{
			if(cond == 3)
			{
				st.setCond(4);
			}
		}
		else if(htmltext.equalsIgnoreCase("black_marketeer_of_mammon_q10472_05.htm"))
		{
			npc.doCast(ABSORB_WIND, st.getPlayer(), true);
		}
		else if(htmltext.equalsIgnoreCase("black_marketeer_of_mammon_q10472_07.htm"))
		{
			if(cond == 5)
			{
				st.takeItems(DARK_FRAGMENT, -1);
				st.setCond(6);
			}
		}
		else if(htmltext.equalsIgnoreCase("blacksmith_of_mammon_q10472_07.htm"))
		{
			if(cond == 6)
			{
				st.giveItems(COUNTERFEIT_ATELIA, 1);
				st.setCond(7);
			}
		}
		else if(htmltext.equalsIgnoreCase("agent_of_chaos_hardin_q10472_04.htm"))
		{
			if(cond == 7)
			{
				st.setCond(8);
			}
		}
		else if(htmltext.equalsIgnoreCase("agent_of_chaos_hardin_q10472_08.htm"))
		{
			if(cond == 16)
			{
				st.takeItems(COUNTERFEIT_ATELIA, -1);
				st.setCond(17);

				npc.doCast(ATELIA_ENERGY, st.getPlayer(), true);
			}
		}
		else if(htmltext.equalsIgnoreCase("alchemist_zephyra_q10472_06.htm"))
		{
			if(cond == 17)
			{
				st.setCond(18);

				npc.doCast(FERINS_CURE, st.getPlayer(), true);
				//npc.altUseSkill(FERINS_RECHARGE, st.getPlayer());
			}
		}
		else if(htmltext.equalsIgnoreCase("karla_q10472_02.htm"))
		{
			if(cond == 18)
			{
				st.setCond(19);
			}
		}
		else if(htmltext.equalsIgnoreCase("class_master_raina_q10472_04.htm"))
		{
			if(stkn.hasMoreTokens())
				st.set("sa_type", stkn.nextToken(), false);
		}
		else if(htmltext.equalsIgnoreCase("class_master_raina_q10472_05.htm"))
		{
			if(cond == 19)
			{
				if(stkn.hasMoreTokens())
				{
					final String runeType = stkn.nextToken();
					if(runeType.equalsIgnoreCase("fire"))
						st.giveItems(FIRE_STONE, 15);
					else if(runeType.equalsIgnoreCase("water"))
						st.giveItems(WATER_STONE, 15);
					else if(runeType.equalsIgnoreCase("earth"))
						st.giveItems(EARTH_STONE, 15);
					else if(runeType.equalsIgnoreCase("wind"))
						st.giveItems(WIND_STONE, 15);
					else if(runeType.equalsIgnoreCase("dark"))
						st.giveItems(DARK_STONE, 15);
					else if(runeType.equalsIgnoreCase("holy"))
						st.giveItems(HOLY_STONE, 15);
				}

				final String saType = st.get("sa_type");
				if(saType != null)
				{
					if(saType.equalsIgnoreCase("red"))
						st.giveItems(RED_SOUL_CRYSTAL_STAGE_15, 1);
					else if(saType.equalsIgnoreCase("blue"))
						st.giveItems(BLUE_SOUL_CRYSTAL_STAGE_15, 1);
					else if(saType.equalsIgnoreCase("green"))
						st.giveItems(GREEN_SOUL_CRYSTAL_STAGE_15, 1);
				}

				st.giveItems(CRYSTAL_R_GRADE, 5);
				st.giveItems(RECIPE_TWILIGHT_NECKLACE_60, 1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest(SOUND_FANFARE2);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = NO_QUEST_DIALOG;

		if(npcId == QUEEN_NAVARI)
		{
			if(cond == 0)
				htmltext = "queen_navari_q10472_01.htm";
			else if(cond == 1)
				htmltext = "queen_navari_q10472_06.htm";
		}
		else if(npcId == ALCHEMIST_ZEPHYRA)
		{
			if(cond == 1)
				htmltext = "alchemist_zephyra_q10472_00.htm";
			else if(cond == 2)
				htmltext = "alchemist_zephyra_q10472_03.htm";
			else if(cond == 17)
				htmltext = "alchemist_zephyra_q10472_04.htm";
			else if(cond == 18)
				htmltext = "alchemist_zephyra_q10472_07.htm";
		}
		else if(npcId == BLACKSMITH_MOMET)
		{
			if(cond == 2)
				htmltext = "blacksmith_momet_q10472_00.htm";
			else if(cond == 3)
				htmltext = "blacksmith_momet_q10472_04.htm";
		}
		else if(npcId == BLACK_MARKETEER_OF_MAMMON)
		{
			if(cond == 3)
				htmltext = "black_marketeer_of_mammon_q10472_00.htm";
			else if(cond == 4)
				htmltext = "black_marketeer_of_mammon_q10472_03.htm";
			else if(cond == 5)
				htmltext = "black_marketeer_of_mammon_q10472_04.htm";
			else if(cond == 6)
				htmltext = "black_marketeer_of_mammon_q10472_07.htm";
		}
		else if(npcId == BLACKSMITH_OF_MAMMON)
		{
			if(cond == 6)
				htmltext = "blacksmith_of_mammon_q10472_00.htm";
			else if(cond == 7)
				htmltext = "blacksmith_of_mammon_q10472_08.htm";
		}
		else if(npcId == AGENT_OF_CHAOS_HARDIN)
		{
			if(cond == 7)
				htmltext = "agent_of_chaos_hardin_q10472_00.htm";
			else if(cond == 8)
				htmltext = "agent_of_chaos_hardin_q10472_05.htm";
			else if(cond == 16)
				htmltext = "agent_of_chaos_hardin_q10472_06.htm";
			else if(cond == 17)
				htmltext = "agent_of_chaos_hardin_q10472_09.htm";
		}
		else if(npcId == KARLA)
		{
			if(cond == 18)
				htmltext = "karla_q10472_00.htm";
			else if(cond == 19)
				htmltext = "karla_q10472_03.htm";
		}
		else if(npcId == SUB_DUAL_CLASS_MASTER_RAINA)
		{
			if(cond == 19)
				htmltext = "class_master_raina_q10472_00.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		final QuestState st = player.getQuestState(getId());
		if(st == null)
			return "";

		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = "";

		if(npcId == POWERFUL_MAGIC_SUMMONER)
		{
			if(cond == 8)
			{
				htmltext = "powerful_magic_summoner_q10472_00.htm";
				st.setCond(9);
				npc.doCast(WYNN_SUMMONERS_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_MAGIC_DAMAGE_DEALER)
		{
			if(cond == 9)
			{
				htmltext = "powerful_magic_damage_dealer_q10472_00.htm";
				st.setCond(10);
				npc.doCast(FEOH_WIZARDS_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_MELEE_DAMAGE_DEALER)
		{
			if(cond == 10)
			{
				htmltext = "powerful_melee_damage_dealer_q10472_00.htm";
				st.setCond(11);
				npc.doCast(TYRR_WARRIORS_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_DAGGER_MELEE_DAMAGE_DEALER)
		{
			if(cond == 11)
			{
				htmltext = "powerful_dagger_melee_damage_dealer_q10472_00.htm";
				st.setCond(12);
				npc.doCast(OTHELL_ROGUES_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_MAGIC_BUFFER)
		{
			if(cond == 12)
			{
				htmltext = "powerful_magic_buffer_q10472_00.htm";
				st.setCond(13);
				npc.doCast(ISS_ENCHANTERS_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_BOW_CROSSBOW_SHARPSHOOTERS)
		{
			if(cond == 13)
			{
				htmltext = "powerful_bow_crossbow_sharpshooters_q10472_00.htm";
				st.setCond(14);
				npc.doCast(YUL_ARCHERS_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_DEFENSIVE_TANKS)
		{
			if(cond == 14)
			{
				htmltext = "powerful_defensive_tanks_q10472_00.htm";
				st.setCond(15);
				npc.doCast(SIGEL_KNIGHTS_POWER, player, true);
			}
		}
		else if(npcId == POWERFUL_MAGIC_HEALER)
		{
			if(cond == 15)
			{
				htmltext = "powerful_magic_healer_q10472_00.htm";
				st.setCond(16);
				npc.doCast(AEORE_HEALERS_POWER, player, true);
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		if(ArrayUtils.contains(STATE_4_MONSTERS_KILL_LIST, npcId))
		{
			if(cond == 4)
			{
				if(st.rollAndGive(DARK_FRAGMENT, 1, 1, STATE_4_DARK_FRAGMENT_NEED_ITEMS_COUNT, STATE_4_DARK_FRAGMENT_DROP_CHANCE))
					st.setCond(5);
			}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(!player.isBaseClassActive())
			return "queen_navari_q10472_00.htm";

		if(player.getDualClass() != null)
			return "queen_navari_q10472_00.htm";

		return super.checkStartCondition(npc, player);
	}
}