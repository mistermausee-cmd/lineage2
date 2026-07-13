package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10794_InvestigatetheForest extends Quest
{
	// NPC's
	private static final int HATUVA = 33849;
	private static final int TOMBSTONE = 31531;

	// Monster's
	private static final int[] MONSTERS = {21562, 21568, 21568, 21573, 21576, 21577, 21578,
			21563, 21565, 21567, 21572, 21574, 21575, 21583, 21584, 21580, 21581, 21564, 21566,
			21596, 21599, 21570, 21571, 21579, 21582, 21585, 21586, 21587, 21588, 21589, 21590,
			21591, 21592, 21593, 21594, 21595};

	// Item's
	private static final int OLDJEWELRYBOX = 39725;

	private static final String Forestofdead2 = "forestofdead2";

	private static final int EXP_REWARD = 93856309;
	private static final int SP_REWARD = 4072; 

	public _10794_InvestigatetheForest()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(HATUVA);
		addTalkId(TOMBSTONE);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 579411, Forestofdead2, 100, MONSTERS);
		addKillNpcWithLog(2, 579411, Forestofdead2, 100, MONSTERS);
		addKillNpcWithLog(3, 579411, Forestofdead2, 100, MONSTERS);
		addQuestItemWithLog(1, 579412, 1, OLDJEWELRYBOX);
		addQuestItemWithLog(2, 579412, 1, OLDJEWELRYBOX);
		addQuestItemWithLog(3, 579412, 1, OLDJEWELRYBOX);
		addQuestCompletedCheck("33849-0.htm", 10793);
		addClassIdCheck("33849-0.htm", 183, 185, 187, 189, 191);
		addLevelCheck("33849-0.htm", 65/*, 70*/);
		addRaceCheck("33849-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33849-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33849-7.htm"))
		{
			
			st.takeItems(OLDJEWELRYBOX, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("31531-2.htm"))
		{
		  st.giveItems(OLDJEWELRYBOX, 1, false);
			if (st.getCond() == 1)
				st.setCond(2);
			else if (st.getCond() == 3)
				st.setCond(5);
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
			case HATUVA:
				if(cond == 0)
					htmltext = "33849-1.htm";
				else if (cond == 1)
					htmltext = "33849-5.htm";
				else if (cond == 4 || cond == 5)
					htmltext = "33849-6.htm";
			break;

			case TOMBSTONE:
				if (cond == 1 || cond == 2 || cond == 3 || cond == 4 || cond == 5)
				{
					if(!st.haveQuestItem(OLDJEWELRYBOX))
						htmltext = "31531-1.htm";
					else
						htmltext = "31531-3.htm";
				}
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1 || cond == 2)
		{
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(Forestofdead2);
				if (cond == 1)
				{
					st.setCond(3);
					st.set("forestofdead2", 50);
				}
				else if (cond == 2)
					st.setCond(4);
			}
		}
		return null;
	}
}