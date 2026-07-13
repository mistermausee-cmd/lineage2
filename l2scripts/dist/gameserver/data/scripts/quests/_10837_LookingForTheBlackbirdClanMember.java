package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10837_LookingForTheBlackbirdClanMember extends Quest
{
	// NPC's
	private static final int NPC1 = 34058;
	private static final int NPC2 = 34063;

	// Monster's
	private static final int[] MONSTERS_A = {23506};
	private static final int[] MONSTERS_B = {23505};
	private static final int[] MONSTERS_C = {23507};

	//Reward EXP SP
	private static final long EXP_REWARD = 9683068920l;
	private static final long SP_REWARD = 23239200;

	// Item's
	private static final int REWARD1 = 46134;

	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";
	public static final String C_LIST = "C_LIST";

	public _10837_LookingForTheBlackbirdClanMember()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addTalkId(NPC2);
		addLevelCheck("captain_adolf_q10837_02.htm", 101);
		addItemHaveCheck("captain_adolf_q10837_03.htm", 46132, 1);
		addKillNpcWithLog(1, 1023506, A_LIST, 40, MONSTERS_A);
		addKillNpcWithLog(1, 1023505, B_LIST, 60, MONSTERS_B);
		addKillNpcWithLog(1, 1023507, C_LIST, 60, MONSTERS_C);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("captain_adolf_q10837_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("blackbird_glenkinchie_q10837_03.htm"))
		{
			st.giveItems(REWARD1, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			checkReward(st);
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
			case NPC1:
				if (cond == 0)
					htmltext = "captain_adolf_q10837_01.htm";
				else if (cond == 1 || cond == 2)
					htmltext = "captain_adolf_q10837_07.htm";
				break;

			case NPC2:
				if (cond == 1)
					htmltext = "blackbird_glenkinchie_q10837_01.htm";
				if (cond == 2)
					htmltext = "blackbird_glenkinchie_q10837_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(A_LIST);
				st.unset(B_LIST);
				st.unset(C_LIST);
				st.setCond(2);
			}

		}
		return null;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.haveQuestItem(46134) && st.haveQuestItem(46135) && st.haveQuestItem(46136) && st.haveQuestItem(46137))
		{
			st.getPlayer().getQuestState(10836).setCond(2);
			return true;
		}

		return false;
	}
}