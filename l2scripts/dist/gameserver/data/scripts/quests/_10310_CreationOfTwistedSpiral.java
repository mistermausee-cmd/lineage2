package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10310_CreationOfTwistedSpiral extends Quest
{
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	public static final String C_LIST = "c_list";
	public static final String D_LIST = "d_list";
	public static final String E_LIST = "e_list";
	//npc
	private static final int SELINA = 33032;
	private static final int GORFINA = 33031;

	private static final int EXP_REWARD = 50178765;	private static final int SP_REWARD = 12042; 	public _10310_CreationOfTwistedSpiral()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addTalkId(GORFINA);

		addLevelCheck("33032-lvl.htm", 90);
		addQuestCompletedCheck("33032-lvl.htm", 10302);
		addKillNpcWithLog(2, A_LIST, 10, 22947);
		addKillNpcWithLog(2, B_LIST, 10, 22948);
		addKillNpcWithLog(2, C_LIST, 10, 22949);
		addKillNpcWithLog(2, D_LIST, 10, 22950);
		addKillNpcWithLog(2, E_LIST, 10, 22951);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33032-5.htm"))
		{
			st.setCond(1);
		}	
		else if(event.equalsIgnoreCase("33031-2.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("33031-5.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 3424540);
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
		if(npcId == SELINA)
		{
			if(cond == 0)
				return "33032.htm";
			else
				return "33032-6.htm";
		}
		else if(npcId == GORFINA)
		{
			if(cond == 1)
				return "33031.htm";
			if(cond == 2)
				return "33031-3.htm";
			if(cond == 3)
				return "33031-4.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == SELINA)
			htmltext = "33032-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.unset(C_LIST);
			qs.unset(D_LIST);
			qs.unset(E_LIST);
			qs.setCond(3);
		}

		return null;
	}	
}