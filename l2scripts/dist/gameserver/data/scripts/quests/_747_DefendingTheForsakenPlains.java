package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _747_DefendingTheForsakenPlains extends Quest
{
	private static final int PETR = 33864;
	private static final int EVELINA = 33865;

	private static final int SIGHN_1 = 47051;
	private static final int SIGHN_2 = 47052;

	private static final int[] MOBS = {20679, 20680, 21017, 21018, 21019, 21020, 21021, 21022, 20647, 20648, 20649, 20650};

	private static final long EXP_REWARD = 24605844;
	private static final int SP_REWARD = 3068;

	public _747_DefendingTheForsakenPlains()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(PETR);
		addTalkId(PETR, EVELINA);
		addKillId(MOBS);
		addQuestItem(SIGHN_1, SIGHN_2);
		addLevelCheck("petterzan_q0747_02.htm", 58, 60);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("petterzan_q0747_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("evluena_q0747_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("petterzan_q0747_10.htm"))
		{
			st.giveItems(46851, 1, false);
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
						htmltext = "petterzan_q0747_03.htm";
					else
						htmltext = "petterzan_q0747_01.htm";
				}
				else if (cond == 1)
					htmltext = "petterzan_q0747_07.htm";
				else if (cond == 2)
					htmltext = "petterzan_q0747_08.htm";
				else if (cond == 3)
					htmltext = "petterzan_q0747_09.htm";
				break;

			case EVELINA:
				if (cond == 1)
					htmltext = "evluena_q0747_01.htm";
				else if (cond == 2)
					htmltext = "evluena_q0747_04.htm";
				else if (cond == 3)
					htmltext = "evluena_q0747_05.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			qs.rollAndGive(SIGHN_1, 1, 1, 120, 100);
			qs.rollAndGive(SIGHN_2, 1, 1, 120, 100);
			if(qs.getQuestItemsCount(SIGHN_1) >= 120 && qs.getQuestItemsCount(SIGHN_2) >= 120)
				qs.setCond(3);
		}
		return null;
	}
}
