package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10534_HatchlingResearch extends Quest
{
	// NPC's
	private static final int STENNA = 34221;

	private static final int DRAGONP = 46735;

	//Монстры
	private static final int[] MOBS = {23434, 23435};

	private static final int EXP_REWARD = 362053391;
	private static final int SP_REWARD = 19840;

	public _10534_HatchlingResearch()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(STENNA);
		addTalkId(STENNA);
		addKillId(MOBS);
		addQuestItem(DRAGONP);
		addLevelCheck("dv_stena_q10534_02.htm", 81/*, 84*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("dv_stena_q10534_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("dv_stena_q10534_08.htm"))
		{
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
			case STENNA:
				if(cond == 0)
					htmltext = "dv_stena_q10534_01.htm";
				else if (cond == 1)
					htmltext = "dv_stena_q10534_06.htm";
				else if (cond == 2)
					htmltext = "dv_stena_q10534_07.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.rollAndGive(DRAGONP, 1, 1, 50, 20);
			if(qs.getQuestItemsCount(DRAGONP) >= 50)
				qs.setCond(2);
		}
		return null;
	}
}