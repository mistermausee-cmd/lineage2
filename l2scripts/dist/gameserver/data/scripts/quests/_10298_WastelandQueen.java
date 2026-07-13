package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10298_WastelandQueen extends Quest
{
	private static final int BATIS = 30332;

	private static final int QUEEN = 29001;

	private static final long EXP_REWARD = 8064000;
	private static final long SP_REWARD = 9676;

	public _10298_WastelandQueen()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(BATIS);
		addTalkId(BATIS);
		addKillId(QUEEN);

		addLevelCheck("captain_bathia_q10298_02.htm", 40/*, 43*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("captain_bathia_q10298_06.htm"))
		{
			st.setCond(1);
		}

		else if (event.equalsIgnoreCase("captain_bathia_q10298_09.htm"))
		{
			st.giveItems(57, 96732);
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
			case BATIS:
				if (cond == 0)
					htmltext = "captain_bathia_q10298_01.htm";
				else if (cond == 1)
					htmltext = "captain_bathia_q10298_07.htm";
				else if (cond == 2)
					htmltext = "captain_bathia_q10298_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
		return null;
	}
}
