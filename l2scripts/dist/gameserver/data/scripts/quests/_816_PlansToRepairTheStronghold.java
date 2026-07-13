package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _816_PlansToRepairTheStronghold extends Quest
{
	// NPC's
	private static final int ADOLF = 34058;

	private static final int[] MOBS = {23505, 23506, 23507, 23508, 23509, 23510, 23511, 23512, 23512};

	private static final int CLOSE = 46142;

	private static final long EXP_REWARD_LOW = 7262301690l;
	private static final int SP_REWARD_LOW = 17429400;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 14524603380l;
	private static final int SP_REWARD_MEDIUM = 34858800;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 21786905070l;
	private static final int SP_REWARD_HIGH = 52288200;
	private static final int FP_REWARD_HIGH = 300;

	private static final long EXP_REWARD_VERY_HIGH = 29049206760l;
	private static final int SP_REWARD_VERY_HIGH = 69717600;
	private static final int FP_REWARD_VERY_HIGH = 400;

	public _816_PlansToRepairTheStronghold()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(ADOLF);
		addTalkId(ADOLF);
		addKillId(MOBS);
		addQuestItem(CLOSE);
		addLevelCheck("captain_adolf_q0816_02.htm", 101);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("captain_adolf_q0816_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) <= 2)
				htmltext = "captain_adolf_q0816_05a.htm";
			else if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 3 && st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) <= 5)
				htmltext = "captain_adolf_q0816_05b.htm";
			else if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 6)
				htmltext = "captain_adolf_q0816_05c.htm";
			else
				htmltext = "captain_adolf_q0816_05.htm";
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_10b.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_10c.htm"))
		{
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_13.htm"))
		{
			st.giveItems(47175, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_13a.htm"))
		{
			st.giveItems(47176, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_13b.htm"))
		{
			st.giveItems(47177, 1);
			st.addExpAndSp(EXP_REWARD_HIGH, SP_REWARD_HIGH);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_HIGH);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("captain_adolf_q0816_13c.htm"))
		{
			st.giveItems(47177, 2);
			st.addExpAndSp(EXP_REWARD_VERY_HIGH, SP_REWARD_VERY_HIGH);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_VERY_HIGH);
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
			case ADOLF:
				if (cond == 0)
					htmltext = "captain_adolf_q0816_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 1 && st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) <= 2)
						htmltext = "captain_adolf_q0816_05a.htm";
					else if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 3 && st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) <= 5)
						htmltext = "captain_adolf_q0816_05b.htm";
					else if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 6)
						htmltext = "captain_adolf_q0816_05c.htm";
					else
						htmltext = "captain_adolf_q0816_05.htm";
				}
				else if (cond == 2)
					htmltext = "captain_adolf_q0816_11.htm";
				else if (cond == 3)
					htmltext = "captain_adolf_q0816_11a.htm";
				else if (cond == 4)
					htmltext = "captain_adolf_q0816_11b.htm";
				else if (cond == 5)
					htmltext = "captain_adolf_q0816_11c.htm";
				else if (cond == 6)
					htmltext = "captain_adolf_q0816_12.htm";
				else if (cond == 7)
					htmltext = "captain_adolf_q0816_12a.htm";
				else if (cond == 8)
					htmltext = "captain_adolf_q0816_12b.htm";
				else if (cond == 9)
					htmltext = "captain_adolf_q0816_12c.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(CLOSE, 1, 1, 200, 55)) //TODO CHANCE?
				qs.setCond(6);
		}
		else if(qs.getCond() == 3)
		{
			if(qs.rollAndGive(CLOSE, 1, 1, 400, 55)) //TODO CHANCE?
				qs.setCond(7);
		}
		else if(qs.getCond() == 4)
		{
			if(qs.rollAndGive(CLOSE, 1, 1, 600, 55)) //TODO CHANCE?
				qs.setCond(8);
		}
		else if(qs.getCond() == 5)
		{
			if(qs.rollAndGive(CLOSE, 1, 1, 800, 55)) //TODO CHANCE?
				qs.setCond(9);
		}

		return null;
	}
}