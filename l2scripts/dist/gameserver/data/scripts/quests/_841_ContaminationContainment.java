package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _841_ContaminationContainment extends Quest
{
	// NPC's
	private static final int IREN = 34233;

	// Monster's
	private static final int[] MONSTERS = {23574, 23575, 23576, 23577};

	private static final long EXP_REWARD_LOW = 5536944000l;
	private static final int SP_REWARD_LOW = 13288590;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11073888000l;
	private static final int SP_REWARD_MEDIUM = 26577180;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 16610832l;
	private static final int SP_REWARD_HIGH = 39865770;
	private static final int FP_REWARD_HIGH = 300;

	public static final String A_LIST = "A_LIST";

	public _841_ContaminationContainment()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(IREN);
		addTalkId(IREN);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 84105, A_LIST, 100, MONSTERS);
		addKillNpcWithLog(3, 84105, A_LIST, 200, MONSTERS);
		addKillNpcWithLog(4, 84105, A_LIST, 300, MONSTERS);
		addQuestItem(47170);
		addLevelCheck("guardian_leader_q0841_02.htm", 100);
		addQuestCompletedCheck("guardian_leader_q0841_02.htm", 10851);
		addFactionLevelCheck("guardian_leader_q0841_02a.htm", FactionType.MOTHERTREE_GUARDIAN, 2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("guardian_leader_q0841_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) == 4)
				htmltext = "guardian_leader_q0841_05a.htm";
			else if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) >= 5)
				htmltext = "guardian_leader_q0841_05b.htm";
			else
				htmltext = "guardian_leader_q0841_05.htm";
		}
		else if(event.equalsIgnoreCase("guardian_leader_q0841_10.htm"))
		{
			st.setCond(2);
			if(!st.haveQuestItem(47170))
				st.giveItems(47170, 1, false);
		}
		else if(event.equalsIgnoreCase("guardian_leader_q0841_10a.htm"))
		{
			st.setCond(3);
			if(!st.haveQuestItem(47170))
				st.giveItems(47170, 1, false);
		}
		else if(event.equalsIgnoreCase("guardian_leader_q0841_10b.htm"))
		{
			st.setCond(4);
			if(!st.haveQuestItem(47170))
				st.giveItems(47170, 1, false);
		}
		else if(event.equalsIgnoreCase("guardian_leader_q0841_13.htm"))
		{
			st.giveItems(47178, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.MOTHERTREE_GUARDIAN, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("guardian_leader_q0841_13a.htm"))
		{
			st.giveItems(47179, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.MOTHERTREE_GUARDIAN, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("guardian_leader_q0841_13b.htm"))
		{
			st.giveItems(47180, 1);
			st.addExpAndSp(EXP_REWARD_HIGH, SP_REWARD_HIGH);
			st.getPlayer().getFactionList().addProgress(FactionType.MOTHERTREE_GUARDIAN, FP_REWARD_HIGH);
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
			case IREN:
				if (cond == 0)
					htmltext = "guardian_leader_q0841_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) == 4)
						htmltext = "guardian_leader_q0841_05a.htm";
					else if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) >= 5)
						htmltext = "guardian_leader_q0841_05b.htm";
					else
						htmltext = "guardian_leader_q0841_05.htm";
				}
				else if (cond == 2)
					htmltext = "guardian_leader_q0841_11.htm";
				else if (cond == 3)
					htmltext = "guardian_leader_q0841_11a.htm";
				else if (cond == 4)
					htmltext = "guardian_leader_q0841_11b.htm";
				else if (cond == 5)
					htmltext = "guardian_leader_q0841_12.htm";
				else if (cond == 6)
					htmltext = "guardian_leader_q0841_12a.htm";
				else if (cond == 7)
					htmltext = "guardian_leader_q0841_12b.htm";
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