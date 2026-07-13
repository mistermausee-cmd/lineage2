package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _493_KickingOutUnwelcomeGuests extends Quest
{
	//npc
	public static final int JORJINO = 33515;
	
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	public static final String C_LIST = "c_list";
	public static final String D_LIST = "d_list";
	public static final String E_LIST = "e_list";

	private static final long EXP_REWARD = 2008271880L;
	private static final long SP_REWARD = 1914480; 
	private static final long EXP_REWARD2 = 4016543760L;
	private static final long SP_REWARD2 = 3828960;
	private static final long EXP_REWARD3 = 6024815640L;
	private static final long SP_REWARD3 = 5743440;
	private static final long EXP_REWARD4 = 8033087520L;
	private static final long SP_REWARD4 = 7657920;

	public _493_KickingOutUnwelcomeGuests()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(JORJINO);
		addTalkId(JORJINO);

		addKillNpcWithLog(1, A_LIST, 50, 23147);
		addKillNpcWithLog(1, B_LIST, 50, 23148);
		addKillNpcWithLog(1, C_LIST, 50, 23149);
		addKillNpcWithLog(1, D_LIST, 50, 23150);
		addKillNpcWithLog(1, E_LIST, 50, 23151);

		addKillNpcWithLog(2, A_LIST, 200, 23147);
		addKillNpcWithLog(2, B_LIST, 200, 23148);
		addKillNpcWithLog(2, C_LIST, 200, 23149);
		addKillNpcWithLog(2, D_LIST, 200, 23150);
		addKillNpcWithLog(2, E_LIST, 200, 23151);

		addLevelCheck(NO_QUEST_DIALOG, 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33515-4.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("33515-6.htm") && st.getPlayer().getLevel() >= 95)
		{
			
			int aCount = st.getInt(A_LIST);
			int bCount = st.getInt(B_LIST);
			int cCount = st.getInt(C_LIST);
			int dCount = st.getInt(D_LIST);
			int eCount = st.getInt(E_LIST);
			if(aCount >= 200 && bCount >= 200 && cCount >= 200 && dCount >= 200 && eCount >= 200)
			{st.giveItems(57, 6567480);
			st.addExpAndSp(EXP_REWARD4, SP_REWARD4);
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.unset(C_LIST);
			st.unset(D_LIST);
			st.unset(E_LIST);
			st.finishQuest();}
			else if(aCount >= 150 && bCount >= 150 && cCount >= 150 && dCount >= 150 && eCount >= 150)
			{st.giveItems(57, 4925610);
			st.addExpAndSp(EXP_REWARD3, SP_REWARD3);
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.unset(C_LIST);
			st.unset(D_LIST);
			st.unset(E_LIST);
			st.finishQuest();}
			else if(aCount >= 100 && bCount >= 100 && cCount >= 100 && dCount >= 100 && eCount >= 100)
			{st.giveItems(57, 3283740);
			st.addExpAndSp(EXP_REWARD2, SP_REWARD2);
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.unset(C_LIST);
			st.unset(D_LIST);
			st.unset(E_LIST);
			st.finishQuest();}
			else if(aCount >= 50 && bCount >= 50 && cCount >= 50 && dCount >= 50 && eCount >= 50)
			{st.giveItems(57, 1641870);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.unset(C_LIST);
			st.unset(D_LIST);
			st.unset(E_LIST);
			st.finishQuest();}
		}				
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == JORJINO)
		{
			if(cond == 0)
				return "33515.htm";
			if(cond == 2 && st.getPlayer().getLevel() >= 95)
				return "33515-5.htm";
			if(cond == 3 && st.getPlayer().getLevel() >= 95)
				return "33515-5.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(updateKill(npc, st))
				st.setCond(2);
		}
		else if(cond == 2)
		{
			if(updateKill(npc, st))
				st.setCond(3);
		}
		return null;
	}	
}