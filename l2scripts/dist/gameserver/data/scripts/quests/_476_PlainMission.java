package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _476_PlainMission extends Quest
{
	//npc
	public static final int GUIDE = 33463;
	public static final int ANDREI = 31292;

	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	public static final String C_LIST = "c_list";
	public static final String D_LIST = "d_list";

	private static final int EXP_REWARD = 4685175;	private static final int SP_REWARD = 1124; 	public _476_PlainMission()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(GUIDE);
		addTalkId(ANDREI);

		addKillNpcWithLog(1, A_LIST, 12, 21278, 21279, 21280);
		addKillNpcWithLog(1, B_LIST, 12, 21282, 21283, 21284);
		addKillNpcWithLog(1, C_LIST, 12, 21286, 21287, 21288);
		addKillNpcWithLog(1, D_LIST, 12, 21290, 21291, 21292);

		addLevelCheck("33463-lvl.htm", 65/*, 69*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33463-3.htm"))
		{
			st.setCond(1);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == GUIDE)
		{
			if(cond == 0)
				return "33463.htm";
			if(cond == 1)
				return "33463-4.htm";
			if(cond == 2)
				return "33463-5.htm";
		}
		if(npcId == ANDREI)
		{
			if(cond == 1)
				return "31292-1.htm";
			if(cond == 2)
			{
				st.giveItems(57, 142200);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
				return "31292.htm"; //no further html do here
			}	
		}		
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == GUIDE)
			htmltext = "33463-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.unset(C_LIST);
			st.unset(D_LIST);
			st.setCond(2);
		}
			
		return null;
	}		
}