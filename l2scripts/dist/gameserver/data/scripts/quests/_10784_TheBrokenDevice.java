package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10784_TheBrokenDevice extends Quest
{
	// NPC's
	private static final int NOVAIN = 33866;

	// Monster's
	private static final int[] MONSTER = {20650, 20648, 20647, 20649};

	private static final int BRIKENDEVICEFRAGMENT = 39723;

	private static final int EXP_REWARD = 14369328;
	private static final int SP_REWARD = 1578; 

	public _10784_TheBrokenDevice()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(NOVAIN);
		addTalkId(NOVAIN);
		addKillId(MONSTER);
		addQuestCompletedCheck("33866-0.htm", 10783);
		addLevelCheck("33866-0.htm", 58/*, 61*/);
		addRaceCheck("33866-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33866-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33866-7.htm"))
		{
			st.takeItems(BRIKENDEVICEFRAGMENT, -1);
			st.giveItems(57, 990000);
			st.giveItems(46851, 1, false);
			st.giveItems(1466, 6000);
			st.giveItems(3951, 6000);
			st.giveItems(33640, 3);
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
			case NOVAIN:
				if(cond == 0)
					htmltext = "33866-1.htm";
				else if (cond == 1)
					htmltext = "33866-5.htm";
				else if (cond == 2)
					htmltext = "33866-6.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
				st.giveItems(BRIKENDEVICEFRAGMENT, 1);
			if(st.getQuestItemsCount(BRIKENDEVICEFRAGMENT) >= 100)
				st.setCond(2);
		}
		return null;
	}
}