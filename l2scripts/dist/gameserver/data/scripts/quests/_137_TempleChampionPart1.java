package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _137_TempleChampionPart1 extends Quest
{
	// NPCs
	private static final int SYLVAIN = 30070;

	// ITEMs
	private static final int FRAGMENT = 10340;
	private static final int BadgeTempleExecutor = 10334;
	private static final int BadgeTempleMissionary = 10339;

	// Monsters
	//private final static int GraniteGolem = 20083;
	//private final static int HangmanTree = 20144;
	//private final static int AmberBasilisk = 20199;
	//private final static int Strain = 20200;
	//private final static int Ghoul = 20201;
	//private final static int DeadSeeker = 20202;
	
	private final static int Hobgoblin = 20147;
	private final static int DionGrizzly = 20203;
	private final static int DireWolf = 20205;
	private final static int OlMahumRanger = 20224;
	private final static int MonsterEyeSearcher = 20265;
	private final static int MonsterEyeGazer1 = 20055;
	private final static int MonsterEyeGazer2 = 20266;
	private final static int EnkuOrcHero = 20291;
	private final static int EnkuOrcShaman = 20292;

	private static final int EXP_REWARD = 219975;	private static final int SP_REWARD = 52; 			public _137_TempleChampionPart1()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(SYLVAIN);
		addKillId(Hobgoblin, DionGrizzly, DireWolf, OlMahumRanger, MonsterEyeSearcher, MonsterEyeGazer1, MonsterEyeGazer2, EnkuOrcHero, EnkuOrcShaman);
		addQuestItem(FRAGMENT);
		addLevelCheck("sylvain_q0137_03.htm", 35);
		addQuestCompletedCheck("sylvain_q0137_03.htm", 135);
		addItemHaveCheck("sylvain_q0137_03.htm", BadgeTempleExecutor, 1);
		addItemHaveCheck("sylvain_q0137_03.htm", BadgeTempleMissionary, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("sylvain_q0137_04.htm"))
		{
			st.takeItems(BadgeTempleExecutor, -1);
			st.takeItems(BadgeTempleMissionary, -1);
			st.setCond(1);
			st.set("talk", "0");
		}
		else if(event.equalsIgnoreCase("sylvain_q0137_08.htm"))
			st.set("talk", "1");
		else if(event.equalsIgnoreCase("sylvain_q0137_10.htm"))
			st.set("talk", "2");
		else if(event.equalsIgnoreCase("sylvain_q0137_13.htm"))
		{
			st.unset("talk");
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("sylvain_q0137_24.htm"))
		{
			st.giveItems(ADENA_ID, 69146);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == SYLVAIN)
			if(cond == 0)
				htmltext = "sylvain_q0137_01.htm";
			else if(cond == 1)
			{
				if(st.getInt("talk") == 0)
					htmltext = "sylvain_q0137_05.htm";
				else if(st.getInt("talk") == 1)
					htmltext = "sylvain_q0137_08.htm";
				else if(st.getInt("talk") == 2)
					htmltext = "sylvain_q0137_10.htm";
			}
			else if(cond == 2)
				htmltext = "sylvain_q0137_13.htm";
			else if(cond == 3 && st.getQuestItemsCount(FRAGMENT) >= 30)
			{
				htmltext = "sylvain_q0137_15.htm";
				st.set("talk", "1");
				st.takeItems(FRAGMENT, -1);
			}
			else if(cond == 3 && st.getInt("talk") == 1)
				htmltext = "sylvain_q0137_16.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 2)
			if(st.getQuestItemsCount(FRAGMENT) < 30)
			{
				st.giveItems(FRAGMENT, 1);
				if(st.getQuestItemsCount(FRAGMENT) >= 30)
				{
					st.setCond(3);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}