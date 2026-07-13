package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10456_OperationRescue extends Quest
{
	// NPC's
	private static final int DEVIAN = 31590;
	//Mobs
	private static final int[] MONSTERS = { 23354, 23355, 23356, 23357, 23358, 23360, 23361, 23362, 23363, 23364, 23365};

	private static final long REWARD_EXP = 1507456500L;
	private static final int REWARD_SP = 3617880;
	public _10456_OperationRescue()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(DEVIAN);
		addTalkId(DEVIAN);
		addKillId(MONSTERS);
		addQuestCompletedCheck("truthseeker_devianne_q10456_02.htm", 10455);
		addLevelCheck("truthseeker_devianne_q10456_02.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("truthseeker_devianne_q10456_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("truthseeker_devianne_q10456_08.htm"))
		{
			st.addExpAndSp(REWARD_EXP, REWARD_SP);
			st.giveItems(57, 659250);
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
			case DEVIAN:
				if(cond == 0)
					htmltext = "truthseeker_devianne_q10456_01.htm";
				else if (cond == 1)
					htmltext = "truthseeker_devianne_q10456_06.htm";
				else if (cond == 2)
					htmltext = "truthseeker_devianne_q10456_07.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(Rnd.chance(15))
				st.setCond(2);
		}
		return null;
	}
}