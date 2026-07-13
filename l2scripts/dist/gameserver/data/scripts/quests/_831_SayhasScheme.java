package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _831_SayhasScheme extends Quest
{
	// NPCs
	private static final int JULIA = 34100;
	private static final int JULIAEND = 34155;
	private static final int ALTAR = 34103;
	// Item
	private static final int ITEM = 46374;

	private static final long EXP_REWARD = 2422697985l;
	private static final int SP_REWARD = 5814450; 

	public _831_SayhasScheme()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(JULIA);
		addTalkId(JULIA);
		addTalkId(JULIAEND);
		addLevelCheck(JULIA, "as_yuyuria_q0831_02.htm", 99);
		addQuestItem(ITEM);
		addKillId(ALTAR);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("as_yuyuria_q0831_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("as_yuyuria_q0831_08.htm"))
		{
			st.giveItems(46375, 1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(ITEM, -1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if(npc.getNpcId() == JULIA)
		{
			if(st.getCond() == 0)
				htmtext = "as_yuyuria_q0831_01.htm";
			else if(st.getCond() == 1)
				htmtext = "as_yuyuria_q0831_06.htm";
			else if(st.getCond() == 2)
				htmtext = "as_yuyuria_q0831_07.htm";
		}
		else if(npc.getNpcId() == JULIAEND)
		{
			if(st.getCond() == 2)
				htmtext = "as_yuyuria_q0831_07.htm";
		}
		return htmtext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1)
			return null;

		if(npc.getNpcId() == ALTAR)
		{
			st.giveItems(ITEM, 1);
			if (st.getQuestItemsCount(ITEM) >= 10)
				st.setCond(2);
		}
		return null;
	}
}