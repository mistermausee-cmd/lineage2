package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10297_GrandOpeningComeToOurTavern extends Quest
{
	// NPCs
	private static final int SETTLEN = 34180;
	private static final int LOLLIA = 34182;
	private static final int HANNA = 34183;
	private static final int MEI = 34186;
	private static final int BRODIEN = 34184;
	private static final int LUPIA = 34185;
	private static final int LAILLY = 34181;
	// Item
	private static final int REWARD = 46564;


	public _10297_GrandOpeningComeToOurTavern()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(SETTLEN);
		addTalkId(SETTLEN, LOLLIA, HANNA, MEI, BRODIEN, LUPIA, LAILLY);
		addLevelCheck(SETTLEN, "34180-05.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("34180-03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("34182-04.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("34183-04.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("34186-04.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("34184-04.htm"))
		{
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("34185-04.htm"))
		{
			st.setCond(6);
		}
		else if(event.equalsIgnoreCase("34181-02.htm"))
		{
			st.giveItems(REWARD, 1, false);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if(npc.getNpcId() == SETTLEN)
		{
			if(st.getCond() == 0)
				htmtext = "34180-01.htm";
			else if(st.getCond() == 1)
				htmtext = "34180-04.htm";
		}
		else if(npc.getNpcId() == LOLLIA)
		{
			if(st.getCond() == 1)
				htmtext = "34182-01.htm";
			else if(st.getCond() == 2)
				htmtext = "34182-05.htm";
		}
		else if(npc.getNpcId() == HANNA)
		{
			if(st.getCond() == 2)
				htmtext = "34183-01.htm";
			else if(st.getCond() == 3)
				htmtext = "34183-05.htm";
		}
		else if(npc.getNpcId() == MEI)
		{
			if(st.getCond() == 3)
				htmtext = "34186-01.htm";
			else if(st.getCond() == 4)
				htmtext = "34186-05.htm";
		}
		else if(npc.getNpcId() == BRODIEN)
		{
			if(st.getCond() == 4)
				htmtext = "34184-01.htm";
			else if(st.getCond() == 5)
				htmtext = "34184-05.htm";
		}
		else if(npc.getNpcId() == LUPIA)
		{
			if(st.getCond() == 5)
				htmtext = "34185-01.htm";
			else if(st.getCond() == 6)
				htmtext = "34185-05.htm";
		}
		else if(npc.getNpcId() == LAILLY)
		{
			if(st.getCond() == 6)
				htmtext = "34181-01.htm";
		}
		return htmtext;
	}
}