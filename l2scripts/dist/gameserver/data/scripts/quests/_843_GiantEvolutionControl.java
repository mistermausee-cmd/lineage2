package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _843_GiantEvolutionControl extends Quest
{
	// NPC's
	private static final int KRENAT = 34237;

	// Monster's
	private static final int[] MONSTERS = {23791, 23792, 23793, 23794};

	private static final long EXP_REWARD_LOW = 5536944000l;
	private static final int SP_REWARD_LOW = 13288590;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11073888000l;
	private static final int SP_REWARD_MEDIUM = 28475640;
	private static final int FP_REWARD_MEDIUM = 200;

	public static final String A_LIST = "A_LIST";

	public _843_GiantEvolutionControl()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(KRENAT);
		addTalkId(KRENAT);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 84305, A_LIST, 50, MONSTERS);
		addKillNpcWithLog(3, 84305, A_LIST, 100, MONSTERS);
		addLevelCheck("giantchaser_officer_q0843_02.htm", 100);
		addFactionLevelCheck("giantchaser_officer_q0843_02a.htm", FactionType.GIANT_CHASER, 2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("giantchaser_officer_q0843_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) < 5)
				htmltext = "giantchaser_officer_q0843_05.htm";
			else
				htmltext = "giantchaser_officer_q0843_05a.htm";
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0843_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0843_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0843_13.htm"))
		{
			st.giveItems(47359, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0843_13a.htm"))
		{
			st.giveItems(47360, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_MEDIUM);
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
			case KRENAT:
				if (cond == 0)
					htmltext = "giantchaser_officer_q0843_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) < 5)
						htmltext = "giantchaser_officer_q0843_05.htm";
					else
						htmltext = "giantchaser_officer_q0843_05a.htm";
				}
				else if (cond == 2)
					htmltext = "giantchaser_officer_q0843_11.htm";
				else if (cond == 3)
					htmltext = "giantchaser_officer_q0843_11a.htm";
				else if (cond == 4)
					htmltext = "giantchaser_officer_q0843_12.htm";
				else if (cond == 5)
					htmltext = "giantchaser_officer_q0843_12a.htm";
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
				qs.setCond(4);
			}
		}
		else if(qs.getCond() == 3)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(5);
			}
		}
		return null;
	}
}