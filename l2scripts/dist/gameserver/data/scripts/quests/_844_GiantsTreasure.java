package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD 

public class _844_GiantsTreasure extends Quest
{
	// NPC's
	private static final int KRENAT = 34237;

	private static final int[] MOBS = {23730, 23731, 23732, 23751};

	private static final int OLDBOX = 47212;

	private static final long EXP_REWARD_LOW = 5932440000l;
	private static final int SP_REWARD_LOW = 14237820;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11864880000l;
	private static final int SP_REWARD_MEDIUM = 28475640;
	private static final int FP_REWARD_MEDIUM = 200;

	public _844_GiantsTreasure()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(KRENAT);
		addTalkId(KRENAT);
		addKillId(MOBS);
		addQuestItem(OLDBOX);
		addLevelCheck("giantchaser_officer_q0844_02.htm", 100);
		addFactionLevelCheck("giantchaser_officer_q0844_02a.htm", FactionType.GIANT_CHASER, 2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("giantchaser_officer_q0844_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) < 4)
				htmltext = "giantchaser_officer_q0844_05a.htm";
			else
				htmltext = "giantchaser_officer_q0844_05.htm";
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0844_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0844_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0844_13.htm"))
		{
			st.giveItems(47359, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("giantchaser_officer_q0844_13a.htm"))
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
					htmltext = "giantchaser_officer_q0844_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) < 4)
						htmltext = "giantchaser_officer_q0844_05a.htm";
					else
						htmltext = "giantchaser_officer_q0844_05.htm";
				}
				else if (cond == 2)
					htmltext = "giantchaser_officer_q0844_11.htm";
				else if (cond == 3)
					htmltext = "giantchaser_officer_q0844_11a.htm";
				else if (cond == 4)
					htmltext = "giantchaser_officer_q0844_12.htm";
				else if (cond == 5)
					htmltext = "giantchaser_officer_q0844_12a.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(OLDBOX, 1, 1, 10, 35)) //TODO CHANCE?
				qs.setCond(4);
		}
		else if(qs.getCond() == 3)
		{
			if(qs.rollAndGive(OLDBOX, 1, 1, 20, 35)) //TODO CHANCE?
				qs.setCond(5);
		}
		return null;
	}
}