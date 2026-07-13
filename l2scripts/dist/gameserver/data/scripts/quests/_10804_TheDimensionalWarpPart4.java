package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10804_TheDimensionalWarpPart4 extends Quest
{
	// NPCs
	private static final int RESHET = 33974;

	// Items
	private static final int AIDOS = 35567;
	private static final int WARPPEACE = 39597;

	private static final int MOB = 23474;

	public static final String A_LIST = "A_LIST";

	private static final int EXP_REWARD = 0;	private static final int SP_REWARD = 100000000; 	public _10804_TheDimensionalWarpPart4()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(RESHET);
		addKillNpcWithLog(1, 1023474, A_LIST, 100, MOB);
		addTalkId(RESHET);
		addLevelCheck(RESHET, NO_QUEST_DIALOG, 99);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10803);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("33974-04.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("33974-07.htm"))
		{
			st.giveItems(AIDOS, 1, false);
			st.giveItems(WARPPEACE, 300);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;

		if (npc.getNpcId() == RESHET)
		{
			if (st.getCond() == 0)
			{
				htmtext = "33974-01.htm";
			}
			else if (st.getCond() == 1)
			{
				htmtext = "33974-05.htm";
			}
			else if (st.getCond() == 2)
			{
				htmtext = "33974-06.htm";
			}
		}

		return htmtext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}
}