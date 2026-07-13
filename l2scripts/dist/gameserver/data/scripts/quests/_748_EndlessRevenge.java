package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _748_EndlessRevenge extends Quest
{
	private static final int PETR = 33864;
	private static final int SUBAN = 33867;
	private static final int MATIAS = 31340;

	private static final int SIGHN = 47053;

	private static final int[] MOBS = {20974, 20975, 20976, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 20674};

	private static final long EXP_REWARD = 49177227;
	private static final int SP_REWARD = 3193;

	public _748_EndlessRevenge()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(PETR);
		addTalkId(PETR, SUBAN, MATIAS);
		addKillId(MOBS);
		addQuestItem(SIGHN);
		addLevelCheck("petterzan_q0748_02.htm", 61, 64);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("petterzan_q0748_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("schwann_q0748_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("captain_mathias_q0748_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case PETR:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "petterzan_q0748_03.htm";
					else
						htmltext = "petterzan_q0748_01.htm";
				}
				else if (cond == 1)
					htmltext = "petterzan_q0748_07.htm";
				break;

			case SUBAN:
				if (cond == 1)
					htmltext = "schwann_q0748_01.htm";
				else if (cond == 2)
					htmltext = "schwann_q0748_04.htm";
				else if (cond == 3)
					htmltext = "schwann_q0748_05.htm";
				break;

			case MATIAS:
				if (cond == 2)
					htmltext = "captain_mathias_q0748_01.htm";
				else if (cond == 3)
					htmltext = "captain_mathias_q0748_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(SIGHN, 1, 1, 200, 70))
				qs.setCond(3);
		}
		return null;
	}
}

