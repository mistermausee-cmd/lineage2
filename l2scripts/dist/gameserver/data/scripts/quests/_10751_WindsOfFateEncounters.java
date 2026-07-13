package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.player.OnClassChangeListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.GameObjectTasks;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class _10751_WindsOfFateEncounters extends Quest
{
	public class ClassChangeListener implements OnClassChangeListener
	{
		public void onClassChange(Player player, ClassId oldClass, ClassId newClass)
		{
			QuestState qs = player.getQuestState(getId());
			if(qs != null)
			{
				if(!newClass.isOfLevel(ClassLevel.NONE))
					qs.abortQuest();
			}
		}
	}

	// NPC's
	private static final int HIGH_PRIEST_RAYMOND = 30289;	// Раймонд - Верховный Жрец
	private static final int QUEEN_NAVARI = 33931;	// Серения - Королева
	private static final int MAGISTER_AYANTHE = 33942;	// Арис - Магистр
	private static final int MASTER_KATALIN = 33943;	// Катрина - Мастер
	private static final int WIZARD_MYSTERIOUS = 33980;	// Загадочный Маг
	private static final int TELESHA = 33981;	// Тересия

	// Monster's
	private static final int SKELETON_WARRIOR = 27528;	// Скелетон Воин - Квестовый Монстр
	private static final int SKELETON_ARCHER = 27529;	// Скелетон Лучник - Квестовый Монстр

	// Item's
	private static final int WIND_SPIRIT_REALMS_RELIC = 39535;	// Часть Энергии Ветра
	private static final int NAVARIS_SUPPORT_BOX_WARRIOR = 40266;	// Ящик Поддержки Серении - Боец Сайхи
	private static final int NAVARIS_SUPPORT_BOX_MAGIC = 40267;	// Ящик Поддержки Серении - Последователь Сайхи

	// Rewards
	private static final long REWARD_ADENA = 110000;	// Награда аден.
	private static final long REWARD_EXP = 2700000;	// Награда EXP.
	private static final int REWARD_SP = 648;	// Награда SP.

	// Other
	private static final double TELESHA_SPAWN_CHANCE = 15.;	// 15%
	private static final int TELESHA_DESPAWN_DELAY = 30000;	// 30 sec.
	private static final int WIZARD_DESPAWN_DELAY = 30000;	// 30 sec.

	// Var's
	private static final String TELESHA_FINDED_VAR = "telesha_finded";
	private static final String TELESHA_OBJECT_ID_VAR = "telesha_object_id";
	private static final String WIZARD_OBJECT_ID_VAR = "wizard_object_id";
	private static final String WIZARD_MSG_TIMER_ID_VAR = "wizard_msg_timer_id";

	private final OnClassChangeListener _classChangeListener = new ClassChangeListener();

	public _10751_WindsOfFateEncounters()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(QUEEN_NAVARI);
		addTalkId(HIGH_PRIEST_RAYMOND, QUEEN_NAVARI, MAGISTER_AYANTHE, MASTER_KATALIN, WIZARD_MYSTERIOUS, TELESHA);
		addFirstTalkId(TELESHA, WIZARD_MYSTERIOUS);

		addKillId(SKELETON_WARRIOR, SKELETON_ARCHER);

		addQuestItem(WIND_SPIRIT_REALMS_RELIC);

		addLevelCheck("queen_navari_q10751_00.htm", 38);
		addClassLevelCheck("queen_navari_q10751_00.htm", true, ClassLevel.NONE);
		addRaceCheck("queen_navari_q10751_00.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final int cond = st.getCond();

		String htmltext = event;

		if(htmltext.equalsIgnoreCase("queen_navari_q10751_02.htm"))
		{
			if(cond == 0)
			{
				if(st.getPlayer().isMageClass())
				{
					htmltext = "queen_navari_q10751_02a.htm";
					st.setCond(3);
				}
				else
					st.setCond(2);
			}
		}
		else if(htmltext.equalsIgnoreCase("master_katalin_q10751_02.htm"))
		{
			if(cond == 2)
			{
				st.setCond(4);
			}
		}
		else if(htmltext.equalsIgnoreCase("magister_ayanthe_q10751_02.htm"))
		{
			if(cond == 3)
			{
				st.setCond(5);
			}
		}
		else if(htmltext.equalsIgnoreCase("high_priest_raymond_q10751_03.htm"))
		{
			if(cond == 4 || cond == 5)
			{
				st.giveItems(WIND_SPIRIT_REALMS_RELIC, 1);
				st.setCond(6);
			}
		}
		else if(htmltext.equalsIgnoreCase("inspect_the_corpse"))
		{
			if(cond == 6)
			{
				final Player player = st.getPlayer();

				npc.startDeleteTask(5000L);

				NpcInstance wizard = NpcUtils.spawnSingle(WIZARD_MYSTERIOUS, Location.findPointToStay(npc, 150), player.getReflection(), WIZARD_DESPAWN_DELAY, player.getName());

				st.set(WIZARD_OBJECT_ID_VAR, wizard.getObjectId(), false);

				st.startQuestTimer(WIZARD_MSG_TIMER_ID_VAR, 0);
			}
			return null;
		}
		else if(htmltext.equalsIgnoreCase(WIZARD_MSG_TIMER_ID_VAR))
		{
			if(cond == 6)
			{
				if(GameObjectsStorage.getNpc(st.getInt(WIZARD_OBJECT_ID_VAR)) != null)
				{
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_MYSTERIOUS_WIZARD, 5000, ScreenMessageAlign.TOP_CENTER));
					st.startQuestTimer(WIZARD_MSG_TIMER_ID_VAR, 7000);
				}
			}
			return null;
		}
		else if(htmltext.equalsIgnoreCase("wizard_mysterious_q10751_02.htm"))
		{
			if(cond == 6)
			{
				st.giveItems(WIND_SPIRIT_REALMS_RELIC, 1);
				st.setCond(7);

				final Player player = st.getPlayer();

				player.sendPacket(new ExShowScreenMessage(NpcString.RETURN_TO_RAYMOND_OF_THE_TOWN_OF_GLUDIO, 3000, ScreenMessageAlign.TOP_CENTER));

				npc.startDeleteTask(5000L);
			}
		}
		else if(htmltext.equalsIgnoreCase("high_priest_raymond_q10751_07.htm"))
		{
			if(cond == 7)
			{
				if(st.getPlayer().isMageClass())
				{
					htmltext = "high_priest_raymond_q10751_07a.htm";
					st.setCond(9);
				}
				else
					st.setCond(8);
			}
		}
		else if(htmltext.equalsIgnoreCase("master_katalin_q10751_11.htm"))
		{
			if(cond == 8)
			{
				final Player player = st.getPlayer();
				if(player.getClassId() == ClassId.ERTHEIA_FIGHTER)
				{
					player.setClassId(ClassId.MARAUDER.ordinal(), false);
					player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.REAWAKENING));
				}

				st.giveItems(ItemTemplate.ITEM_ID_ADENA, REWARD_ADENA);
				st.takeItems(WIND_SPIRIT_REALMS_RELIC, -1);
				st.giveItems(NAVARIS_SUPPORT_BOX_WARRIOR, 1);
				st.addExpAndSp(REWARD_EXP, REWARD_SP);
				st.finishQuest(SOUND_FANFARE2);
			}
		}
		else if(htmltext.equalsIgnoreCase("magister_ayanthe_q10751_11.htm"))
		{
			if(cond == 9)
			{
				final Player player = st.getPlayer();
				if(player.getClassId() == ClassId.ERTHEIA_MAGE)
				{
					player.setClassId(ClassId.SAIHA_MAGE.ordinal(), false);
					player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.REAWAKENING));
				}

				st.giveItems(ItemTemplate.ITEM_ID_ADENA, REWARD_ADENA);
				st.takeItems(WIND_SPIRIT_REALMS_RELIC, -1);
				st.giveItems(NAVARIS_SUPPORT_BOX_MAGIC, 1);
				st.addExpAndSp(REWARD_EXP, REWARD_SP);
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
				htmltext = "queen_navari_q10751_01.htm";
			else if(cond == 2)
				htmltext = "queen_navari_q10751_02.htm";
			else if(cond == 3)
				htmltext = "queen_navari_q10751_02a.htm";
		}
		else if(npcId == MASTER_KATALIN)
		{
			if(cond == 2)
				htmltext = "master_katalin_q10751_01.htm";
			else if(cond == 4)
				htmltext = "master_katalin_q10751_03.htm";
			else if(cond == 8)
				htmltext = "master_katalin_q10751_04.htm";
		}
		else if(npcId == MAGISTER_AYANTHE)
		{
			if(cond == 3)
				htmltext = "magister_ayanthe_q10751_01.htm";
			else if(cond == 5)
				htmltext = "magister_ayanthe_q10751_03.htm";
			else if(cond == 9)
				htmltext = "magister_ayanthe_q10751_04.htm";
		}
		else if(npcId == HIGH_PRIEST_RAYMOND)
		{
			if(cond == 4 || cond == 5)
				htmltext = "high_priest_raymond_q10751_01.htm";
			else if(cond == 6)
				htmltext = "high_priest_raymond_q10751_04.htm";
			else if(cond == 7)
				htmltext = "high_priest_raymond_q10751_05.htm";
			else if(cond == 8)
				htmltext = "high_priest_raymond_q10751_08.htm";
			else if(cond == 9)
				htmltext = "high_priest_raymond_q10751_08a.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		final QuestState st = player.getQuestState(getId());
		final int npcId = npc.getNpcId();
		if(npcId == TELESHA)
		{
			if(st == null)
				return null;

			if(st.getInt(TELESHA_OBJECT_ID_VAR) != npc.getObjectId())
				return null; // Offlike

			final int cond = st.getCond();
			if(cond == 6)
			{
				if(GameObjectsStorage.getNpc(st.getInt(WIZARD_OBJECT_ID_VAR)) == null)
					return "telesha_q10751_01.htm";
			}
		}
		else if(npcId == WIZARD_MYSTERIOUS)
		{
			if(st == null)
				return null;

			if(st.getInt(WIZARD_OBJECT_ID_VAR) != npc.getObjectId())
				return null; // TODO

			final int cond = st.getCond();
			if(cond == 6)
				return "wizard_mysterious_q10751_01.htm";
			else if(cond == 7)
				return "wizard_mysterious_q10751_03.htm";
		}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();
		if(cond == 6)
		{
			if(npcId == SKELETON_WARRIOR || npcId == SKELETON_ARCHER)
			{
				NpcInstance tempNpc = GameObjectsStorage.getNpc(st.getInt(TELESHA_OBJECT_ID_VAR));
				if(tempNpc != null)
					return null;

				tempNpc = GameObjectsStorage.getNpc(st.getInt(WIZARD_OBJECT_ID_VAR));
				if(tempNpc != null)
					return null;

				if(st.getInt(TELESHA_FINDED_VAR) > 0 || Rnd.chance(TELESHA_SPAWN_CHANCE))
				{
					final Player player = st.getPlayer();

					player.sendPacket(new ExShowScreenMessage(NpcString.CHECK_ON_TELESHA, TELESHA_DESPAWN_DELAY, ScreenMessageAlign.TOP_CENTER));

					tempNpc = NpcUtils.spawnSingle(TELESHA, Location.findPointToStay(npc, 50), player.getReflection(), TELESHA_DESPAWN_DELAY, player.getName());

					st.set(TELESHA_FINDED_VAR, 1);
					st.set(TELESHA_OBJECT_ID_VAR, tempNpc.getObjectId(), false);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
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