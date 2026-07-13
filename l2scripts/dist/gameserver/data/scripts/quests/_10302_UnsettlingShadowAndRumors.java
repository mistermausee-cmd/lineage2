package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10302_UnsettlingShadowAndRumors extends Quest
{
	//npc
	private static final int KANIBYS = 32898;
	private static final int ISHAEL = 32894;
	
	private static final int KES = 32901;
	private static final int KEY = 32903;
	private static final int KIK = 32902;

	private static final int EXP_REWARD = 6728850;	private static final int SP_REWARD = 1614; 	public _10302_UnsettlingShadowAndRumors()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(KANIBYS);
		addTalkId(KANIBYS);
		addTalkId(ISHAEL);
		
		addTalkId(KES);
		addTalkId(KEY);
		addTalkId(KIK);

		addLevelCheck("32898-lvl.htm", 88);
		addQuestCompletedCheck("32898-lvl.htm", 10301);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32898-4.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("32898-8.htm"))
		{		
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 2177190);
			st.giveItems(34033, 1);
			st.finishQuest();
		}		

		if(event.equalsIgnoreCase("32894-1.htm"))
			st.setCond(2);

		if(event.equalsIgnoreCase("32901-1.htm"))
			st.setCond(3);

		if(event.equalsIgnoreCase("32903-1.htm"))
			st.setCond(4);
		
		if(event.equalsIgnoreCase("32902-1.htm"))
			st.setCond(5);

		if(event.equalsIgnoreCase("32894-5.htm"))
			st.setCond(6);
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == KANIBYS)
		{
			if(cond == 0)	
				return "32898.htm";
			else if(cond >= 1 && cond < 6)
				return "32898-5.htm";	
			else if(cond == 6)
				return "32898-6.htm";
		}
		else if(npcId == ISHAEL)
		{
			if(cond == 1)
				return "32894.htm";
			else if(cond >= 2 && cond < 5)
				return "32894-2.htm";
			else if(cond == 5)
				return "32894-3.htm";
			else if(cond == 6)
				return "32894-6.htm";
		}
		else if(npcId == KES)
		{
			if(cond == 2)
				return "32901.htm";
			else
				return "32901-2.htm";
		}

		else if(npcId == KEY)
		{
			if(cond == 3)
				return "32903.htm";
			else
				return "32903-2.htm";
		}

		else if(npcId == KIK)
		{
			if(cond == 4)
				return "32902.htm";
			else
				return "32902-2.htm";
		}		
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == KANIBYS)
			htmltext = "32898-comp.htm";
		return htmltext;
	}
}