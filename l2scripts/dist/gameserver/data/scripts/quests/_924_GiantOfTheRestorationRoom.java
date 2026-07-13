package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD 

public class _924_GiantOfTheRestorationRoom extends Quest
{

	// NPC's
	private static final int SHUMAD = 34217;

	// Monster's
	private static final int[] MONSTERS = {23727, 23728, 23729, 23750};

	private static final long EXP_REWARD_LOW = 5932440000l;
	private static final int SP_REWARD_LOW = 14237820;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11864880000l;
	private static final int SP_REWARD_MEDIUM = 28475640;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 17797320000l;
	private static final int SP_REWARD_HIGH = 42713460;
	private static final int FP_REWARD_HIGH = 300;

	public static final String A_LIST = "A_LIST";

	public _924_GiantOfTheRestorationRoom()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(SHUMAD);
		addTalkId(SHUMAD);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 92411, A_LIST, 100, MONSTERS);
		addKillNpcWithLog(3, 92411, A_LIST, 200, MONSTERS);
		addKillNpcWithLog(4, 92411, A_LIST, 300, MONSTERS);
		addLevelCheck("schmadriba_q0924_02.htm", 100);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("schmadriba_q0924_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) <= 2)
				htmltext = "schmadriba_q0924_05a.htm";
			else if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 3)
				htmltext = "schmadriba_q0924_05b.htm";
			else
				htmltext = "schmadriba_q0924_05.htm";
		}
		else if(event.equalsIgnoreCase("schmadriba_q0924_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("schmadriba_q0924_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("schmadriba_q0924_10b.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("schmadriba_q0924_13.htm"))
		{
			st.giveItems(47359, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("schmadriba_q0924_13a.htm"))
		{
			st.giveItems(47360, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("schmadriba_q0924_13b.htm"))
		{
			st.giveItems(47361, 1);
			st.addExpAndSp(EXP_REWARD_HIGH, SP_REWARD_HIGH);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_HIGH);
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
			case SHUMAD:
				if (cond == 0)
					htmltext = "schmadriba_q0924_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) <= 2)
						htmltext = "schmadriba_q0924_05a.htm";
					else if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 3)
						htmltext = "schmadriba_q0924_05b.htm";
					else
						htmltext = "schmadriba_q0924_05.htm";
				}
				else if (cond == 2)
					htmltext = "schmadriba_q0924_11.htm";
				else if (cond == 3)
					htmltext = "schmadriba_q0924_11a.htm";
				else if (cond == 4)
					htmltext = "schmadriba_q0924_11b.htm";
				else if (cond == 5)
					htmltext = "schmadriba_q0924_12.htm";
				else if (cond == 6)
					htmltext = "schmadriba_q0924_12a.htm";
				else if (cond == 7)
					htmltext = "schmadriba_q0924_12b.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(5);
			}
		}
		else if(qs.getCond() == 3)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(6);
			}
		}
		else if(qs.getCond() == 4)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(7);
			}
		}
		return null;
	}
}