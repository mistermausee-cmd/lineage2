package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _497_IncarnationOfGreedZellakaSolo extends Quest
{
	//npc
	public static final int KARTIA_RESEARCH = 33647;
	
	//mobs
	public static final int CHALAKA = 19253;

	public _497_IncarnationOfGreedZellakaSolo()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(KARTIA_RESEARCH);
		addTalkId(KARTIA_RESEARCH);
		addKillId(CHALAKA);
		addLevelCheck(NO_QUEST_DIALOG, 85/*, 89*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33647-4.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("33647-8.htm"))
		{		
			st.giveItems(34930, 1);
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
		if(npcId == KARTIA_RESEARCH)
		{
			if(cond == 0)
				return "33647.htm";
			if(cond == 1)
				return "33647-6.htm";
			if(cond == 2)
				return "33647-7.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == KARTIA_RESEARCH)
			htmltext = "33647-5.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		st.setCond(2);
		return null;
	}
}