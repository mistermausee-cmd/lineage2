package quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.gameserver.listener.actor.player.OnClassChangeListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import ai.incubatorOfEvil.NpcArcherAI;
import ai.incubatorOfEvil.NpcHealerAI;
import ai.incubatorOfEvil.NpcMageAI;
import ai.incubatorOfEvil.NpcWarriorAI;
import instances.IncubatorOfEvil;

// reworked by Evil_dnk
public abstract class SagasSuperclass extends Quest
{
	public class ClassChangeListener implements OnClassChangeListener
	{
		public void onClassChange(Player player, ClassId oldClass, ClassId newClass)
		{
			QuestState qs = player.getQuestState(getId());
			if(qs != null)
			{
				if(!newClass.isOfLevel(ClassLevel.SECOND))
					qs.abortQuest();
			}
		}
	}

	public SagasSuperclass()
	{
		super(PARTY_NONE, REPEATABLE);
	}

	protected int StartNPC = 0;

	// massives
	private List<NpcInstance> _npcWaves = new ArrayList<NpcInstance>();
	// private static final int Orven = 30857;
	private static final int Avanguard_aden = 33407;
	private static final int Avanguard_corpse1 = 33166;
	private static final int Avanguard_corpse2 = 33167;
	private static final int Avanguard_corpse3 = 33168;
	private static final int Avanguard_corpse4 = 33169;
	private static final int Avanguard_member = 33165;
	// instance npc:
	private static final int Avanguard_camptain = 33170;

	private static final int Avanguard_Ellis = 33171;
	private static final int Avanguard_Barton = 33172;
	private static final int Avanguard_Xaok = 33173;
	private static final int Avanguard_Ellia = 33174;
	// npc helpers
	private static final int Van_Archer = 33414;
	private static final int Van_Infantry = 33415;

	// monsters
	private static final int Shaman = 27430;
	private static final int Slayer = 27431;
	private static final int Pursuer = 27432;
	private static final int Priest_Darkness = 27433;
	private static final int Guard_Darkness = 27434;
	// boss
	private static final int Death_wound = 27425;

	// items
	private static final int DeadSoldierOrbs = 17748;
	private static final int Ring_Shout = 17484;
	// onKill won't work here because mobs also killing mobs
	
	private static final int EXP_REWARD = 42000000;
	private static final int SP_REWARD = 0; 
	
	private final OnClassChangeListener _classChangeListener = new ClassChangeListener();

	protected static Map<Integer, Class<?>> Quests = new HashMap<Integer, Class<?>>();
	static
	{
		Quests.put(10341, _10341_DayOfDestinyHumanFate.class);
		Quests.put(10342, _10342_DayOfDestinyElvenFate.class);
		Quests.put(10343, _10343_DayOfDestinyDarkElfsFate.class);
		Quests.put(10344, _10344_DayOfDestinyOrcsFate.class);
		Quests.put(10345, _10345_DayOfDestinyDwarfsFate.class);
		Quests.put(10346, _10346_DayOfDestinyKamaelsFate.class);
	}

	protected void init()
	{
		addStartNpc(StartNPC);
		addTalkId(StartNPC);
		addTalkId(Avanguard_aden);
		addTalkId(Avanguard_corpse1);
		addTalkId(Avanguard_corpse2);
		addTalkId(Avanguard_corpse3);
		addTalkId(Avanguard_corpse4);
		addTalkId(Avanguard_member);
		addTalkId(Avanguard_camptain);
		addTalkId(Avanguard_Ellis);
		addTalkId(Avanguard_Barton);
		addTalkId(Avanguard_Xaok);
		addTalkId(Avanguard_Ellia);
		addKillId(Shaman);
		addKillId(Slayer);
		addKillId(Pursuer);
		addKillId(Priest_Darkness);
		addKillId(Guard_Darkness);
		addKillId(Death_wound);

		addQuestItem(DeadSoldierOrbs);
		addQuestItem(Ring_Shout);

		addLevelCheck(StartNPC + ".htm", 76);
		addRaceCheck(StartNPC + ".htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addClassLevelCheck(StartNPC + ".htm", false, ClassLevel.SECOND);
	}

	private void initFriendNpc(QuestState st, Player player)
	{
		int npcId1 = st.getInt("sel1"); // first chosen
		int npcId2 = st.getInt("sel2"); // second chosen
		int npcId3 = Avanguard_camptain; // adolf hitler :D
		int npcId4 = Van_Archer; // 3 archers
		int npcId5 = Van_Infantry; // 3 infantry soldiers
		// spawn npc helpers
		NpcInstance sel1 = player.getReflection().addSpawnWithoutRespawn(npcId1, new Location(55976, -175672, -7980, 49151), 0);
		NpcInstance sel2 = player.getReflection().addSpawnWithoutRespawn(npcId2, new Location(56328, -175672, -7980, 49151), 0);
		NpcInstance adolf = player.getReflection().addSpawnWithoutRespawn(npcId3, new Location(56168, -175576, -7974, 49151), 0);
		// archers
		NpcInstance archer1 = player.getReflection().addSpawnWithoutRespawn(npcId4, new Location(56392, -176232, -7980, 49151), 0);
		NpcInstance archer2 = player.getReflection().addSpawnWithoutRespawn(npcId4, new Location(56184, -176168, -7974, 49151), 0);
		NpcInstance archer3 = player.getReflection().addSpawnWithoutRespawn(npcId4, new Location(55976, -176136, -7980, 49151), 0);
		// infantry
		NpcInstance infantry1 = player.getReflection().addSpawnWithoutRespawn(npcId5, new Location(56168, -176712, -7973, 49151), 0);
		NpcInstance infantry2 = player.getReflection().addSpawnWithoutRespawn(npcId5, new Location(55960, -176696, -7973, 49151), 0);
		NpcInstance infantry3 = player.getReflection().addSpawnWithoutRespawn(npcId5, new Location(56376, -176712, -7973, 49151), 0);

		switch(npcId1)
		{
			case 33171:
				sel1.setAI(new NpcHealerAI(sel1));
				break;
			case 33172:
				sel1.setAI(new NpcWarriorAI(sel1));
				break;
			case 33173:
				sel1.setAI(new NpcArcherAI(sel1));
				break;
			case 33174:
				sel1.setAI(new NpcMageAI(sel1));
				break;
			default:
				break;
		}

		switch(npcId2)
		{
			case 33171:
				sel2.setAI(new NpcHealerAI(sel2));
				break;
			case 33172:
				sel2.setAI(new NpcWarriorAI(sel2));
				break;
			case 33173:
				sel2.setAI(new NpcArcherAI(sel2));
				break;
			case 33174:
				sel2.setAI(new NpcMageAI(sel2));
				break;
			default:
				break;
		}
		adolf.setAI(new NpcWarriorAI(adolf));
		archer1.setAI(new NpcArcherAI(archer1));
		archer2.setAI(new NpcArcherAI(archer2));
		archer3.setAI(new NpcArcherAI(archer3));
		infantry1.setAI(new NpcWarriorAI(infantry1));
		infantry2.setAI(new NpcWarriorAI(infantry2));
		infantry3.setAI(new NpcWarriorAI(infantry3));
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{

		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase(StartNPC + "-5.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33407-1.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("33407-4.htm"))
		{
			st.takeItems(DeadSoldierOrbs, -1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("33166-1.htm"))
		{
			if(st.get("orb1") != null)
				return "33166-got.htm";

			st.set("orb1", "1");
			st.giveItems(DeadSoldierOrbs, 1);
			st.playSound(SOUND_MIDDLE);
			checkOrbs(player, st);
		}
		else if(event.equalsIgnoreCase("33167-1.htm"))
		{
			if(st.get("orb2") != null)
				return "33167-got.htm";

			st.set("orb2", "1");
			st.giveItems(DeadSoldierOrbs, 1);
			st.playSound(SOUND_MIDDLE);
			checkOrbs(player, st);
		}
		else if(event.equalsIgnoreCase("33168-1.htm"))
		{
			if(st.get("orb3") != null)
				return "33168-got.htm";

			st.set("orb3", "1");
			st.giveItems(DeadSoldierOrbs, 1);
			st.playSound(SOUND_MIDDLE);
			checkOrbs(player, st);
		}
		else if(event.equalsIgnoreCase("33169-1.htm"))
		{
			if(st.get("orb4") != null)
				return "33168-got.htm";

			st.set("orb4", "1");
			st.giveItems(DeadSoldierOrbs, 1);
			st.playSound(SOUND_MIDDLE);
			checkOrbs(player, st);
		}

		else if(event.equalsIgnoreCase("33170-2.htm"))
		{
			st.setCond(6, false);
		}

		else if(event.equalsIgnoreCase("33170-6.htm"))
		{
			st.setCond(10, false);
			if(st.getQuestItemsCount(Ring_Shout) == 0)
				st.giveItems(Ring_Shout, 1); // ring
			Functions.npcSay(npc, NpcString.THE_CRY_OF_FATE_PENDANT_WILL_BE_HELPFUL_TO_YOU_PLEASE_EQUIP_IT_AND_BRING_OUT_THE_POWER_OF_THE_PENDANT_TO_PREPARE_FOR_THE_NEXT_FIGHT);
		}

		else if(event.equalsIgnoreCase("selection"))
		{
			if(st.get("sel1") == null)
			{
				st.set("sel1", String.valueOf(npc.getNpcId()), false);
				npc.deleteMe();
				if(st.getCond() == 6 && st.get("sel1") != null && st.get("sel2") != null)
					st.setCond(7, false);
				return null;
			}

			if(st.get("sel2") == null)
			{
				st.set("sel2", String.valueOf(npc.getNpcId()), false);
				npc.deleteMe();
				if(st.getCond() == 6 && st.get("sel1") != null && st.get("sel2") != null)
					st.setCond(7, false);
				return null;
			}

		}

		else if(event.equalsIgnoreCase("enterinstance"))
		{
			// maybe take some other quest items?
			st.setCond(5, false);
			enterInstance(st.getPlayer());
			return null;
		}

		else if(event.equalsIgnoreCase("battleField"))
		{
			IncubatorOfEvil ioe = (IncubatorOfEvil) player.getActiveReflection();
			initFriendNpc(st, player);
			st.getPlayer().teleToLocation(56168,-175528,-7974);
			//ioe.AdolphNpc.teleToLocation(56168,-175496,-7974);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			ioe.stageStart(1);
			st.setCond(8, false);
			return null;
		}
		else if(event.equalsIgnoreCase("firstStandCompleted"))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.CREATURES_HAVE_STOPPED_THEIR_ATTACK_REST_AND_THEN_SPEAK_WITH_ADOLPH, 10000, ScreenMessageAlign.MIDDLE_CENTER, true));
			st.setCond(9, false);
			return null;
		}

		else if(event.equalsIgnoreCase("engagesecondstand"))
		{
			IncubatorOfEvil ioe = (IncubatorOfEvil) player.getActiveReflection();
			player.sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 10000, ScreenMessageAlign.MIDDLE_CENTER, true));
			ioe.nextStage();
			st.setCond(11, false);
			return null;
		}

		else if(event.equalsIgnoreCase("secondStandCompleted"))
		{
			st.setCond(12, false);
			return null;
		}
		else if(event.startsWith("giveme"))
		{
			if(player.getClassId().isOfLevel(ClassLevel.SECOND))
			{
				int reqClass = -1;
				for(ClassId cid : ClassId.VALUES)
				{
					if(cid.childOf(player.getClassId()) && cid.isOfLevel(ClassLevel.THIRD))
						reqClass = cid.getId();
				}

				if(reqClass == -1)
					player.sendMessage("Something gone wrong, please contact administrator!");

				player.setClassId(reqClass, false);
				player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1000, 0));
			}
			st.giveItems(46852, 1, false);
			st.giveItems(1467, 8000);
			st.giveItems(3952, 8000);
			st.giveItems(33518, 3);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);

			st.takeItems(DeadSoldierOrbs, -1);
			//
			st.finishQuest();
			player.broadcastUserInfo(true);
			return StartNPC + "-8.htm";
		}
		return htmltext;

	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
			
		if(npc.getNpcId() == Death_wound)
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.AGH_HUMANS_HA_IT_DOES_NOT_MATTER_YOUR_WORLD_WILL_END_ANYWAYS, 10000, ScreenMessageAlign.MIDDLE_CENTER, true));
			st.setCond(12);
			return null;
		}

		return null;
	}

	private static void checkOrbs(Player player, QuestState st)
	{
		if(st.getQuestItemsCount(DeadSoldierOrbs) == 4)
		{
			st.setCond(3);
			st.unset("orb1");
			st.unset("orb2");
			st.unset("orb3");
			st.unset("orb4");
		}
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		String htmltext = NO_QUEST_DIALOG;

		//if(id == COMPLETED)
			//return htmltext;

		if(npcId == StartNPC)
		{
			if(cond == 0)
				return StartNPC + "-1.htm";
			else if(cond == 1)
				return StartNPC + "-got.htm";
			else if(cond == 13)
				return StartNPC + "-6.htm";
		}
		else if(npcId == Avanguard_aden)
		{
			if(cond == 1)
				return "33407.htm";
			else if(cond == 2)
				return "33407-2.htm";
			else if(cond == 3)
				return "33407-3.htm";
		}
		else if(npcId == Avanguard_corpse1)
		{
			if(cond == 2)
				return "33166.htm";
		}
		else if(npcId == Avanguard_corpse2)
		{
			if(cond == 2)
				return "33167.htm";
		}
		else if(npcId == Avanguard_corpse3)
		{
			if(cond == 2)
				return "33168.htm";
		}
		else if(npcId == Avanguard_corpse4)
		{
			if(cond == 2)
				return "33169.htm";
		}
		else if(npcId == Avanguard_member)
		{
			if(cond >= 4)
				return "33165.htm";
		}

		else if(npcId == Avanguard_camptain)
		{
			if(cond == 5)
				return "33170-1.htm";
			else if(cond == 7)
				return "33170-3.htm";
			else if(cond == 9)
				return "33170-5.htm";
			else if(cond == 10)
				return "33170-7.htm";
			else if(cond == 12)
			{
				st.setCond(13);
				st.giveItems(736, 1); // SOE
				npc.broadcastPacket(new SocialActionPacket(npc.getObjectId(), 3));
				return "33170-8.htm";
			}
		}

		else if(npcId == Avanguard_Ellis)
		{
			if(cond == 6)
				return "33171-1.htm";
		}

		else if(npcId == Avanguard_Barton)
		{
			if(cond == 6)
				return "33172-1.htm";
		}

		else if(npcId == Avanguard_Xaok)
		{
			if(cond == 6)
				return "33173-1.htm";
		}

		else if(npcId == Avanguard_Ellia)
		{
			if(cond == 6)
				return "33174-1.htm";
		}
		return htmltext;
	}


	private void enterInstance(Player player)
	{
		Reflection reflection = player.getActiveReflection();
		if(reflection != null)
		{
			if(player.canReenterInstance(185))
				player.teleToLocation(reflection.getTeleportLoc(), reflection);
		}
		else if(player.canEnterInstance(185))
			ReflectionUtils.enterReflection(player, new IncubatorOfEvil(player), 185);
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
