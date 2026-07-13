package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10801_TheDimensionalWarpPart1 extends Quest
{
	// NPCs
	private static final int RESHET = 33974;

	// Items
	private static final int BRACEL = 39747;
	private static final int WARPPEACE = 39597;

	private static final int BUGBEAR = 23465;

	public static final String A_LIST = "A_LIST";

	private static final int EXP_REWARD = 0;	private static final int SP_REWARD = 100000000; 	public _10801_TheDimensionalWarpPart1()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(RESHET);
		addKillNpcWithLog(1, 1023465, A_LIST, 100, BUGBEAR);
		addTalkId(RESHET);
		addLevelCheck(RESHET, NO_QUEST_DIALOG, 99);
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
			st.giveItems(BRACEL, 1, false);
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