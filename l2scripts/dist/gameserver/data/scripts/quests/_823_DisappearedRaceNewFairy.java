package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _823_DisappearedRaceNewFairy extends Quest
{
	// NPC's
	private static final int MIMU = 30747;

	// Monster's
	private static final int[] MONSTERS = {23578, 23566, 23567, 23574, 23582, 23786, 23568, 23569, 23575, 23583, 23787, 23570, 23571, 23576, 23584, 23788, 23572, 23573, 23577, 23585, 23789};

	private static final long EXP_REWARD_LOW = 3045319200l;
	private static final int SP_REWARD_LOW = 7308474;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 6090638400l;
	private static final int SP_REWARD_MEDIUM = 14617495;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 9135957600l;
	private static final int SP_REWARD_HIGH = 21926243;
	private static final int FP_REWARD_HIGH = 300;

	public static final String A_LIST = "A_LIST";

	public _823_DisappearedRaceNewFairy()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(MIMU);
		addTalkId(MIMU);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 46258, A_LIST, 300, MONSTERS);
		addKillNpcWithLog(3, 46258, A_LIST, 600, MONSTERS);
		addKillNpcWithLog(4, 46258, A_LIST, 900, MONSTERS);
		addLevelCheck("fairy_mymyu_q0823_02.htm", 100);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("fairy_mymyu_q0823_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) <= 2)
				htmltext = "fairy_mymyu_q0823_05a.htm";
			else if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) >= 3)
				htmltext = "fairy_mymyu_q0823_05b.htm";
			else
				htmltext = "fairy_mymyu_q0823_05.htm";
		}
		else if(event.equalsIgnoreCase("fairy_mymyu_q0823_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("fairy_mymyu_q0823_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("fairy_mymyu_q0823_10b.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("fairy_mymyu_q0823_13.htm"))
		{
			st.giveItems(47178, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.MOTHERTREE_GUARDIAN, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("fairy_mymyu_q0823_13a.htm"))
		{
			st.giveItems(47179, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.MOTHERTREE_GUARDIAN, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("fairy_mymyu_q0823_13b.htm"))
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
			case MIMU:
				if (cond == 0)
					htmltext = "fairy_mymyu_q0823_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) <= 2)
						htmltext = "fairy_mymyu_q0823_05a.htm";
					else if(st.getPlayer().getFactionList().getLevel(FactionType.MOTHERTREE_GUARDIAN) >= 3)
						htmltext = "fairy_mymyu_q0823_05b.htm";
					else
						htmltext = "fairy_mymyu_q0823_05.htm";
				}
				else if (cond == 2)
					htmltext = "fairy_mymyu_q0823_11.htm";
				else if (cond == 3)
					htmltext = "fairy_mymyu_q0823_11a.htm";
				else if (cond == 4)
					htmltext = "fairy_mymyu_q0823_11b.htm";
				else if (cond == 5)
					htmltext = "fairy_mymyu_q0823_12.htm";
				else if (cond == 6)
					htmltext = "fairy_mymyu_q0823_12a.htm";
				else if (cond == 7)
					htmltext = "fairy_mymyu_q0823_12b.htm";
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