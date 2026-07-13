package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD 

public class _923_ShinedustExtraction extends Quest
{
	// NPC's
	private static final int SHUMADR = 34217;
	private static final int STOR = 34219;

	private static final int[] MOBS = {23748, 23733, 23734, 23746, 23747, 23739, 23740, 23741, 23742, 23743, 23744, 23745};

	private static final int DUST = 46747;

	private static final long EXP_REWARD_LOW = 5536944000l;
	private static final int SP_REWARD_LOW = 13288590;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11073888000l;
	private static final int SP_REWARD_MEDIUM = 26577180;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 16610832000l;
	private static final int SP_REWARD_HIGH = 39865770;
	private static final int FP_REWARD_HIGH = 300;

	public _923_ShinedustExtraction()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(SHUMADR, STOR);
		addTalkId(SHUMADR, STOR);
		addKillId(MOBS);
		addQuestItem(DUST);
		addLevelCheck("schmadriba_q0923_02.htm", 100);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("schmadriba_q0923_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) <= 3)
				htmltext = "schmadriba_q0923_05a.htm";
			else if (st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 4)
				htmltext = "schmadriba_q0923_05b.htm";
			else
				htmltext = "schmadriba_q0923_05.htm";
		}
		else if(event.equalsIgnoreCase("stor_q0923_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) <= 3)
				htmltext = "stor_q0923_05a.htm";
			else if (st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 4)
				htmltext = "stor_q0923_05b.htm";
			else
				htmltext = "stor_q0923_05.htm";
		}
		else if(event.equalsIgnoreCase("schmadriba_q0923_10.htm") || event.equalsIgnoreCase("stor_q0923_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("schmadriba_q0923_10a.htm") || event.equalsIgnoreCase("stor_q0923_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("schmadriba_q0923_10b.htm") || event.equalsIgnoreCase("stor_q0923_10b.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("schmadriba_q0923_13.htm") || event.equalsIgnoreCase("stor_q0923_13.htm"))
		{
			st.giveItems(47184, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("schmadriba_q0923_13a.htm") || event.equalsIgnoreCase("stor_q0923_13a.htm"))
		{
			st.giveItems(47185, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("schmadriba_q0923_13b.htm") || event.equalsIgnoreCase("stor_q0923_13b.htm"))
		{
			st.giveItems(47186, 1);
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
			case SHUMADR:
				if (cond == 0)
					htmltext = "schmadriba_q0923_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) <= 3)
						htmltext = "schmadriba_q0923_05a.htm";
					else if (st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 4)
						htmltext = "schmadriba_q0923_05b.htm";
					else
						htmltext = "schmadriba_q0923_05.htm";
				}
				else if (cond == 2)
					htmltext = "schmadriba_q0923_11.htm";
				else if (cond == 3)
					htmltext = "schmadriba_q0923_11a.htm";
				else if (cond == 4)
					htmltext = "schmadriba_q0923_11b.htm";
				else if (cond == 5)
					htmltext = "schmadriba_q0923_12.htm";
				else if (cond == 6)
					htmltext = "schmadriba_q0923_12a.htm";
				else if (cond == 7)
					htmltext = "schmadriba_q0923_12b.htm";
				break;

			case STOR:
				if (cond == 0)
					htmltext = "stor_q0923_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) <= 3)
						htmltext = "stor_q0923_05a.htm";
					else if (st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) >= 4)
						htmltext = "stor_q0923_05b.htm";
					else
						htmltext = "stor_q0923_05.htm";
				}
				else if (cond == 2)
					htmltext = "stor_q0923_11.htm";
				else if (cond == 3)
					htmltext = "stor_q0923_11a.htm";
				else if (cond == 4)
					htmltext = "stor_q0923_11b.htm";
				else if (cond == 5)
					htmltext = "stor_q0923_12.htm";
				else if (cond == 6)
					htmltext = "stor_q0923_12a.htm";
				else if (cond == 7)
					htmltext = "stor_q0923_12b.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(DUST, 1, 1, 200, 60)) //TODO CHANCE?
				qs.setCond(5);
		}
		else if(qs.getCond() == 3)
		{
			if(qs.rollAndGive(DUST, 1, 1, 400, 60)) //TODO CHANCE?
				qs.setCond(6);
		}
		else if(qs.getCond() == 4)
		{
			if(qs.rollAndGive(DUST, 1, 1, 600, 60)) //TODO CHANCE?
				qs.setCond(7);
		}
		return null;
	}
}