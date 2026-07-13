package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _817_BlackAteliaResearch extends Quest
{
	// NPC's
	private static final int KAISY = 34051;

	private static final int SPICE = 46145;

	private static final int[] MOBS = {23603, 23604, 26128, 26127, 26126};

	private static final long EXP_REWARD = 3631150845l;
	private static final int SP_REWARD = 8714700;

	public _817_BlackAteliaResearch()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(KAISY);
		addTalkId(KAISY);
		addKillId(MOBS);
		addLevelCheck("ar_kaysia_q0817_02.htm", 101);
		addQuestCompletedCheck("ar_kaysia_q0817_02.htm", 10841);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("ar_kaysia_q0817_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("ar_kaysia_q0817_08.htm"))
		{
			st.giveItems(32779, 1);
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
			case KAISY:
				if (cond == 0)
					htmltext = "ar_kaysia_q0817_01.htm";
				else if (cond == 1)
					htmltext = "ar_kaysia_q0817_06.htm";
				else if (cond == 2)
					htmltext = "ar_kaysia_q0817_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if(qs.rollAndGive(SPICE, 1, 1, 1, 50))
				qs.setCond(2);
		}
		return null;
	}
}