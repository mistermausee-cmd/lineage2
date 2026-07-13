package quests;

import java.util.StringTokenizer;

import l2s.gameserver.listener.actor.player.OnClassChangeListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

import instances.FortressOfTheDeadQ10752;

/**
 * @author Bonux
**/
public class _10752_WindsOfFateAPromise extends Quest
{
	public class ClassChangeListener implements OnClassChangeListener
	{
		public void onClassChange(Player player, ClassId oldClass, ClassId newClass)
		{
			QuestState qs = player.getQuestState(getId());
			if(qs != null)
			{
				if(!newClass.isOfLevel(ClassLevel.FIRST))
					qs.abortQuest();
			}
		}
	}

	// NPC's
	private static final int MAGISTER_AYANTHE = 33942;	// Арис - Магистр
	private static final int MASTER_KATALIN = 33943;	// Катрина - Мастер
	private static final int KARLA = 33933;	// Калли Серебан
	private static final int GRAND_MASTER_SIEGMUND = 31321;	// Зигмунд - Великий Мастер
	private static final int HEAD_BLACKSMITH_LOMBERT = 31317;	// Ломберт - Главный Кузнец
	private static final int MYSTERIOUS_WIZARD = 31522;	// Загадочный Маг
	private static final int TOMBSTONE = 31523;	// Надгробие
	private static final int GHOST_OF_VON_HELLMANN = 31524;	// Призрак фон Хельмана
	private static final int BROKEN_BOOKSHELF = 31526;	// Книжный Шкаф
	private static final int KAIN_VAN_HALTER = 33979;	// Кайн Ван Холтер
	private static final int MYSTERIOUS_WIZARD_2 = 33980;	// Загадочный Маг

	// Item's
	private static final int NAVARIS_MARK = 39536;	// Знак Серении
	private static final int PROPHECY_MACHINE_FRAGMENT = 39537;	// Обломок Устройства Предсказаний
	private static final int KAINS_PROPHECY_MACHINE_FRAGMENT = 39538;	// Обломок Устройства Предсказаний Кайна
	private static final int RED_SOUL_CRYSTAL_STAGE_14 = 9570;	// Красный Кристалл Души - Уровень 15
	private static final int BLUE_SOUL_CRYSTAL_STAGE_14 = 9571;	// Синий Кристалл Души - Уровень 15
	private static final int GREEN_SOUL_CRYSTAL_STAGE_14 = 9572;	// Зеленый Кристалл Души - Уровень 15
	private static final int MYSTERIOUS_SOULSHOT_LARGE_PACK_S_GRADE = 22576;	// Упаковка: Чудесные Заряды Души S (10 000)
	private static final int MYSTERIOUS_BLESSED_SPIRITSHOT_LARGE_PACK_S_GRADE = 22577;	// Упаковка: Чудесные Благ. Заряды Духа S (10 000)

	// Other
	private static final int GHOST_DESPAWN_DELAY = 35000;	// 35 sec.
	private static final int WIZARD_DESPAWN_DELAY = 30000;	// 30 sec.
	private static final int INSTANCE_ZONE_ID = 254; // Крепость Неупокоенных

	// Var's
	private static final String GHOST_OBJECT_ID_VAR = "ghost_object_id";
	private static final String WIZARD2_OBJECT_ID_VAR = "wizard2_object_id";
	private static final String WIZARD_MSG_TIMER_ID_VAR = "wizard_msg_timer_id";
	private static final String END_INSTANCE_TIMER_ID_VAR = "end_instance_timer_id";

	// Rewards
	private static final long EXP_REWARD = 42000000;	// Награда EXP.
	private static final int SP_REWARD = 0;	// Награда SP.

	private final OnClassChangeListener _classChangeListener = new ClassChangeListener();

	public _10752_WindsOfFateAPromise()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(MAGISTER_AYANTHE, MASTER_KATALIN);
		addTalkId(MAGISTER_AYANTHE, MASTER_KATALIN, KARLA, GRAND_MASTER_SIEGMUND, HEAD_BLACKSMITH_LOMBERT, MYSTERIOUS_WIZARD, TOMBSTONE, GHOST_OF_VON_HELLMANN, BROKEN_BOOKSHELF);
		addTalkId(KAIN_VAN_HALTER, MYSTERIOUS_WIZARD_2);
		addFirstTalkId(MYSTERIOUS_WIZARD_2);

		addQuestItem(NAVARIS_MARK, PROPHECY_MACHINE_FRAGMENT, KAINS_PROPHECY_MACHINE_FRAGMENT);

		addLevelCheck(MAGISTER_AYANTHE, "magister_ayanthe_q10752_00.htm", 76);
		addClassLevelCheck(MAGISTER_AYANTHE, "magister_ayanthe_q10752_00.htm", true, ClassLevel.FIRST);
		addRaceCheck(MAGISTER_AYANTHE, "magister_ayanthe_q10752_00.htm", Race.ERTHEIA);

		addLevelCheck(MASTER_KATALIN, "master_katalin_q10752_00.htm", 76);
		addClassLevelCheck(MASTER_KATALIN, "master_katalin_q10752_00.htm", true, ClassLevel.FIRST);
		addRaceCheck(MASTER_KATALIN, "master_katalin_q10752_00.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final int cond = st.getCond();

		StringTokenizer stkn = new StringTokenizer(event, " ");

		event = stkn.nextToken();

		if(event.equalsIgnoreCase("magister_ayanthe_q10752_05.htm") || event.equalsIgnoreCase("master_katalin_q10752_05.htm"))
		{
			if(cond == 0)
			{
				st.setCond(1);
			}
		}
		else if(event.equalsIgnoreCase("karla_q10752_04.htm"))
		{
			if(cond == 1)
			{
				st.giveItems(NAVARIS_MARK, 1);
				st.giveItems(PROPHECY_MACHINE_FRAGMENT, 1);
				st.setCond(2);
			}
		}
		else if(event.equalsIgnoreCase("grand_master_siegmund_q10752_03.htm"))
		{
			if(cond == 2)
			{
				st.setCond(3);
			}
		}
		else if(event.equalsIgnoreCase("head_blacksmith_lombert_q10752_05.htm"))
		{
			if(cond == 3)
			{
				st.setCond(4);
			}
		}
		else if(event.equalsIgnoreCase("mysterious_wizard_q10752_05.htm"))
		{
			if(cond == 4)
			{
				st.setCond(5);
			}
		}
		else if(event.equalsIgnoreCase("tombstone_q10752_03.htm"))
		{
			if(cond == 5)
			{
				st.setCond(6);
			}

			if(GameObjectsStorage.getNpc(st.getInt(GHOST_OBJECT_ID_VAR)) == null)
			{
				NpcInstance ghost = NpcUtils.spawnSingle(GHOST_OF_VON_HELLMANN, Location.findPointToStay(npc, 150), npc.getReflection(), GHOST_DESPAWN_DELAY, st.getPlayer().getName());

				st.set(GHOST_OBJECT_ID_VAR, ghost.getObjectId(), false);

				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_GHOST_OF_VON_HELLMANN, 10000, ScreenMessageAlign.TOP_CENTER));
			}
		}
		else if(event.equalsIgnoreCase("ghost_of_von_hellmann_q10752_03.htm"))
		{
			if(cond == 6)
			{
				st.setCond(7);

				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TIME_TO_MOVE_ONTO_THE_NEXT_PLACE, 15000, ScreenMessageAlign.TOP_CENTER));
			}
		}
		else if(event.equalsIgnoreCase("broken_bookshelf_q10752_03.htm"))
		{
			if(cond == 7)
			{
				st.setCond(8);
			}
		}
		else if(event.equalsIgnoreCase("open_letter"))
		{
			enterInstance(st, new FortressOfTheDeadQ10752(), INSTANCE_ZONE_ID);
			return null;
		}
		else if(event.equalsIgnoreCase("kain_van_halter_q10752_11.htm"))
		{
			if(GameObjectsStorage.getNpc(st.getInt(WIZARD2_OBJECT_ID_VAR)) != null)
				return null;

			NpcInstance wizard = NpcUtils.spawnSingle(MYSTERIOUS_WIZARD_2, Location.findPointToStay(npc, 150), npc.getReflection(), WIZARD_DESPAWN_DELAY, st.getPlayer().getName());

			st.set(WIZARD2_OBJECT_ID_VAR, wizard.getObjectId(), false);

			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_MYSTERIOUS_WIZARD, 10000, ScreenMessageAlign.TOP_CENTER));
			st.startQuestTimer(WIZARD_MSG_TIMER_ID_VAR, 12000);
		}
		else if(event.equalsIgnoreCase(WIZARD_MSG_TIMER_ID_VAR))
		{
			if(GameObjectsStorage.getNpc(st.getInt(WIZARD2_OBJECT_ID_VAR)) != null)
			{
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_MYSTERIOUS_WIZARD, 5000, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer(WIZARD_MSG_TIMER_ID_VAR, 7000);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("end_instance"))
		{
			Reflection reflection = npc.getReflection();
			if(reflection.getInstancedZoneId() == INSTANCE_ZONE_ID)
				reflection.despawnAll();

			SceneMovie scene = SceneMovie.ERTHEIA_QUEST_B;
			st.getPlayer().startScenePlayer(scene);

			st.startQuestTimer(END_INSTANCE_TIMER_ID_VAR, scene.getDuration());
			return null;
		}
		else if(event.equalsIgnoreCase(END_INSTANCE_TIMER_ID_VAR))
		{
			if(cond == 8)
			{
				st.giveItems(KAINS_PROPHECY_MACHINE_FRAGMENT, 1);
				st.setCond(9);
			}

			Reflection reflection = st.getPlayer().getActiveReflection();
			if(reflection!= null && reflection.getInstancedZoneId() == INSTANCE_ZONE_ID)
				reflection.collapse();
		}
		else if(event.equalsIgnoreCase("karla_q10752_09.htm"))
		{
			if(cond == 9)
			{
				if(st.getPlayer().isMageClass())
				{
					event = "karla_q10752_09a.htm";
					st.setCond(10);
				}
				else
					st.setCond(11);
			}
		}
		else if(event.equalsIgnoreCase("magister_ayanthe_q10752_07.htm") || event.equalsIgnoreCase("master_katalin_q10752_07.htm"))
		{
			if(stkn.hasMoreTokens())
				st.set("sa_type", stkn.nextToken(), false);
		}
		else if(event.equalsIgnoreCase("magister_ayanthe_q10752_11.htm") || event.equalsIgnoreCase("master_katalin_q10752_11.htm"))
		{
			if(cond == 10 || cond == 11)
			{
				final Player player = st.getPlayer();
				if(player.isMageClass())
					player.setClassId(ClassId.STORM_SAIHA_MAGE.ordinal(), false);
				else
					player.setClassId(ClassId.RANGER.ordinal(), false);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.REAWAKENING));

				st.takeItems(NAVARIS_MARK, -1);
				st.takeItems(PROPHECY_MACHINE_FRAGMENT, -1);
				st.takeItems(KAINS_PROPHECY_MACHINE_FRAGMENT, -1);

				final String saType = st.get("sa_type");
				if(saType != null)
				{
					if(saType.equalsIgnoreCase("red"))
						st.giveItems(RED_SOUL_CRYSTAL_STAGE_14, 1);
					else if(saType.equalsIgnoreCase("blue"))
						st.giveItems(BLUE_SOUL_CRYSTAL_STAGE_14, 1);
					else if(saType.equalsIgnoreCase("green"))
						st.giveItems(GREEN_SOUL_CRYSTAL_STAGE_14, 1);
				}
				st.giveItems(46852, 1, false);
				st.giveItems(57, 1599120);
				st.giveItems(1467, 8000);
				st.giveItems(3952, 8000);
				st.giveItems(33518, 3);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest(SOUND_FANFARE2);
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = NO_QUEST_DIALOG;

		if(npcId == MAGISTER_AYANTHE)
		{
			if(cond == 0)
				htmltext = "magister_ayanthe_q10752_01.htm";
			else if(cond == 1)
				htmltext = "magister_ayanthe_q10752_06.htm";
			else if(cond == 10)
				htmltext = "magister_ayanthe_q10752_07.htm";
		}
		else if(npcId == MASTER_KATALIN)
		{
			if(cond == 0)
				htmltext = "master_katalin_q10752_01.htm";
			else if(cond == 1)
				htmltext = "master_katalin_q10752_06.htm";
			else if(cond == 11)
				htmltext = "master_katalin_q10752_07.htm";
		}
		else if(npcId == KARLA)
		{
			if(cond == 1)
				htmltext = "karla_q10752_01.htm";
			else if(cond == 2)
				htmltext = "karla_q10752_05.htm";
			else if(cond == 9)
				htmltext = "karla_q10752_06.htm";
			else if(cond == 10)
				htmltext = "karla_q10752_10a.htm";
			else if(cond == 11)
				htmltext = "karla_q10752_10.htm";
		}
		else if(npcId == GRAND_MASTER_SIEGMUND)
		{
			if(cond == 2)
				htmltext = "grand_master_siegmund_q10752_01.htm";
			else if(cond == 3)
				htmltext = "grand_master_siegmund_q10752_04.htm";
		}
		else if(npcId == HEAD_BLACKSMITH_LOMBERT)
		{
			if(cond == 3)
				htmltext = "head_blacksmith_lombert_q10752_01.htm";
			else if(cond == 4)
				htmltext = "head_blacksmith_lombert_q10752_06.htm";
		}
		else if(npcId == MYSTERIOUS_WIZARD)
		{
			if(cond == 4)
				htmltext = "mysterious_wizard_q10752_01.htm";
			else if(cond == 5)
				htmltext = "mysterious_wizard_q10752_06.htm";
		}
		else if(npcId == TOMBSTONE)
		{
			if(cond == 5)
				htmltext = "tombstone_q10752_01.htm";
			else if(cond == 6)
			{
				if(GameObjectsStorage.getNpc(st.getInt(GHOST_OBJECT_ID_VAR)) == null)
					htmltext = "tombstone_q10752_01.htm";
				else
				{
					htmltext = null;
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_GHOST_OF_VON_HELLMANN, 5000, ScreenMessageAlign.TOP_CENTER));
				}
			}
		}
		else if(npcId == GHOST_OF_VON_HELLMANN)
		{
			if(cond == 6)
			{
				if(npc.getObjectId() == st.getInt(GHOST_OBJECT_ID_VAR))
					htmltext = "ghost_of_von_hellmann_q10752_01.htm";
			}
		}
		else if(npcId == BROKEN_BOOKSHELF)
		{
			if(cond == 7)
				htmltext = "broken_bookshelf_q10752_01.htm";
			else if(cond == 8)
				htmltext = "broken_bookshelf_q10752_04.htm";
			else if(cond == 9)
				htmltext = "broken_bookshelf_q10752_05.htm";
		}
		else if(npcId == KAIN_VAN_HALTER)
		{
			if(cond == 8)
				htmltext = "kain_van_halter_q10752_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		final QuestState st = player.getQuestState(getId());
		final int npcId = npc.getNpcId();
		if(npcId == MYSTERIOUS_WIZARD_2)
		{
			if(st == null)
				return null;

			if(st.getInt(WIZARD2_OBJECT_ID_VAR) != npc.getObjectId())
				return null; // Offlike

			final int cond = st.getCond();
			if(cond == 8)
				return "mysterious_wizard2_q10752_01.htm";
		}
		return null;
	}

	@Override
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			case MAGISTER_AYANTHE:
				if(player.isMageClass())
					return true;
				return false;
			case MASTER_KATALIN:
				if(!player.isMageClass())
					return true;
				return false;
		}
		return true;
	}

	@Override
	public void onRestore(QuestState qs)
	{
		if(qs.isStarted())
			qs.getPlayer().addListener(_classChangeListener);
	}

	@Override
	public void onAccept(QuestState qs)
	{
		qs.getPlayer().addListener(_classChangeListener);
	}

	@Override
	public void onExit(QuestState qs)
	{
		qs.getPlayer().removeListener(_classChangeListener);
	}
}