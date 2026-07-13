package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _926_ExploringTheDimension30daySearchOperation extends Quest
{
	// NPC's
	private static final int BELORA = 34227;
	private static final int SUBI = 34228;

	private static final int[] MOBS = {23755, 23757, 23759};

	private static final int ENERGY = 46785;

	private static final long EXP_REWARD = 1507592779;
	private static final int SP_REWARD = 3618222;

	public _926_ExploringTheDimension30daySearchOperation()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(BELORA, SUBI);
		addTalkId(BELORA, SUBI);
		addKillId(MOBS);
		addQuestItem(ENERGY);
		addLevelCheck("cod_inner_officer_a_q0926_02.htm", 95, 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("cod_inner_officer_a_q0926_05.htm") || event.equalsIgnoreCase("cod_inner_officer_b_q0926_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("cod_inner_officer_a_q0926_08.htm") || event.equalsIgnoreCase("cod_inner_officer_b_q0926_08.htm"))
		{
			st.giveItems(47043, 1);
			st.giveItems(46787, 1);
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
			case BELORA:
				if (cond == 0)
					htmltext = "cod_inner_officer_a_q0926_01.htm";
				else if (cond == 1)
					htmltext = "cod_inner_officer_a_q0926_06.htm";
				else if (cond == 2)
					htmltext = "cod_inner_officer_a_q0926_07.htm";
				break;

			case SUBI:
				if (cond == 0)
					htmltext = "cod_inner_officer_b_q0926_01.htm";
				else if (cond == 1)
					htmltext = "cod_inner_officer_b_q0926_06.htm";
				else if (cond == 2)
					htmltext = "cod_inner_officer_b_q0926_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if(qs.rollAndGive(ENERGY, 1, 1, 100, 70)) //TODO CHANCE?
				qs.setCond(2);
		}
		return null;
	}
}