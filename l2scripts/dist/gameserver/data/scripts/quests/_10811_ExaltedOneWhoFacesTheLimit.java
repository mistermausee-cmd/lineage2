package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10811_ExaltedOneWhoFacesTheLimit extends Quest
{
	// NPC's
	private static final int LEONEL = 33907;

	public _10811_ExaltedOneWhoFacesTheLimit()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(LEONEL);
		addTalkId(LEONEL);
		addLevelCheck("lionel_hunter_q10811_02.htm", 99);
		addNobleCheck("lionel_hunter_q10811_02.htm", true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("lionel_hunter_q10811_04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("lionel_hunter_q10811_06.htm"))
		{
			if(!st.haveQuestItem(45627))
				st.giveItems(45627, 1, false);

			st.setCond(2);
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
			case LEONEL:
				if (cond == 0)
					htmltext = "lionel_hunter_q10811_01a.htm";
				else if (cond == 1)
					htmltext = "lionel_hunter_q10811_03.htm";
				else if (cond == 2)
				{
					checkReward(st);
					htmltext = "lionel_hunter_q10811_07.htm";
				}
				else if (cond == 3)
				{
					htmltext = "lionel_hunter_q10811_08.htm";
					st.giveItems(45922, 1, false);
					st.takeItems(45627, -1);
					st.takeItems(45623, -1);
					st.takeItems(45624, -1);
					st.takeItems(45625, -1);
					st.takeItems(45626, -1);
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	public boolean checkReward(QuestState st)
	{
		if(st.haveQuestItem(45623) && st.haveQuestItem(45624) && st.haveQuestItem(45625) && st.haveQuestItem(45626))
		{
			st.setCond(3);
			return true;
		}
		return false;
	}
}