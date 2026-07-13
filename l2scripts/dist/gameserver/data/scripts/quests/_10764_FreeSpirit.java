package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10764_FreeSpirit extends Quest
{
	// NPC's
	private static final int BORBO = 33966;
	private static final int TREE = 33964;
	private static final int WIND = 33965;
	// Item's
	
	private static final int BUNDLEKEY = 39490;
	private static final int LOOSENCHEIN = 39518;

	private static final int EXP_REWARD = 1312934;	private static final int SP_REWARD = 315; 	public _10764_FreeSpirit()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BORBO);
		addTalkId(BORBO);
		addTalkId(TREE);
		addTalkId(WIND);
		addQuestCompletedCheck("33966-0.htm", 10763);
		addLevelCheck("33966-0.htm", 38);
		addRaceCheck("33966-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33966-3.htm"))
		{
			st.setCond(1);
			st.giveItems(BUNDLEKEY, 10, false);
		}
		else if(event.equalsIgnoreCase("33966-6.htm"))
		{
			
			st.takeItems(LOOSENCHEIN, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case BORBO:
				if(cond == 0)
					htmltext = "33966-1.htm";
				else if (cond == 1)
					htmltext = "33966-4.htm";
				else if (cond == 2)
					htmltext = "33966-5.htm";
			break;
			case WIND:
			case TREE:
			 if (cond == 1)
			{
				st.takeItems(BUNDLEKEY, 1);
				npc.doDie(null);
				npc.endDecayTask();
				st.giveItems(LOOSENCHEIN, 1, false);
				if(st.getQuestItemsCount(LOOSENCHEIN) >= 10)
					st.setCond(2);
				return null;
			}

			break;
		}
		return htmltext;
	}
}