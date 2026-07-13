package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10831_UnbelievableSight extends Quest
{
	// NPC's
	private static final int BELAS = 34056;

	// Monster's
	private static final int[] MONSTERS = {23559, 23560};

	// Item's
	private static final int AWASOMPOWER = 45822;
	private static final int REWARD = 46158;

	// Quest item chance drop
	private static final int CHANCE = 90;

	private static final long EXP_REWARD = 5932440000l;
	private static final int SP_REWARD = 14237820; 

	public _10831_UnbelievableSight()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BELAS);
		addTalkId(BELAS);
		addKillId(MONSTERS);
		addQuestItem(AWASOMPOWER);
		addLevelCheck("belas_q10831_02.htm", 100);
		addQuestCompletedCheck("belas_q10831_02.htm", 10830);
		addItemHaveCheck("belas_q10831_03.htm", 45840, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("belas_q10831_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("belas_q10831_09.htm"))
		{
			st.takeItems(AWASOMPOWER, -1);
			st.giveItems(REWARD, 1);
			st.giveItems(46130, 1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
			case BELAS:
				if (cond == 0)
					htmltext = "belas_q10831_01.htm";
				else if (cond == 1)
					htmltext = "belas_q10831_07.htm";
				else if (cond == 2)
					htmltext = "belas_q10831_08.htm";
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
			if (st.rollAndGive(AWASOMPOWER, 1, 1, 100, CHANCE))
				st.setCond(2);
		}
		return null;
	}

}