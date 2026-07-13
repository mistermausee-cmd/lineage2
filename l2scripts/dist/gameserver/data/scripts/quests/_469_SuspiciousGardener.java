package quests;


import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _469_SuspiciousGardener extends Quest
{
	//npc
	public static final int GOFINA = 33031;
	
	public static final String A_LIST = "a_list";

	public _469_SuspiciousGardener()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(GOFINA);
		addTalkId(GOFINA);
		
		addKillNpcWithLog(1, A_LIST, 30, 22964);

		addLevelCheck("33031-lvl.htm", 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33031-3.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("33031-6.htm"))
		{
			st.giveItems(30385, 2);
			st.finishQuest();
		}			
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == GOFINA)
		{
			if(cond == 0)
				return "33031.htm";
			if(cond == 1)
				return "33031-4.htm";
			if(cond == 2)
				return "33031-5.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == GOFINA)
			htmltext = "33031-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.setCond(2);
		}
		return null;
	}	
}