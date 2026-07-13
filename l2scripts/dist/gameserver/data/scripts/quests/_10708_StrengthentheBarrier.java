package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10708_StrengthentheBarrier extends Quest
{
	// NPC's
	private static final int BACON = 33846;
	private static final int DEVICE = 33960;

	// Items
	private static final int KEY = 39509;

	private static final int EXP_REWARD = 20881876;	private static final int SP_REWARD = 152; 	public _10708_StrengthentheBarrier()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(BACON);
		addTalkId(BACON);
		addTalkId(DEVICE);
		addQuestItem(KEY);
		addLevelCheck("disciple_bacon_q10708_02.htm", 52/*, 57*/);
		addQuestCompletedCheck("disciple_bacon_q10708_02.htm", 10399);
		addRaceCheck("disciple_bacon_q10708_02.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("disciple_bacon_q10708_04.htm"))
		{
			st.setCond(1);
			if(!st.haveQuestItem(KEY))
				st.giveItems(KEY, 1, false);
		}
		else if(event.equalsIgnoreCase("disciple_bacon_q10708_07.htm"))
		{
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
			case BACON:
				if(cond == 0)
					htmltext = "disciple_bacon_q10708_01.htm";
				else if(cond == 1)
					htmltext = "disciple_bacon_q10708_05.htm";
				else if(cond == 2)
					htmltext = "disciple_bacon_q10708_06.htm";
			break;

			case DEVICE:
				if(cond == 1)
				{
					st.takeItems(KEY, -1);
					st.setCond(2);
					htmltext = "";
				}
				break;
		}
		return htmltext;
	}
}