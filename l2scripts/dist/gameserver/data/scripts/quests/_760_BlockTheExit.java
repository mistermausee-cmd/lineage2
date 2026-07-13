package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _760_BlockTheExit extends Quest
{
	// NPCs
	private static final int KURTIZ = 30870;
	private static final int DARK_RIDER = 26102;
	// Items
	private static final int REWARD_BOX = 46560; // Curtiz's Reward Box

	public _760_BlockTheExit()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(KURTIZ);
		addTalkId(KURTIZ);
		addKillId(DARK_RIDER);
		addLevelCheck(KURTIZ, "30870-07.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30870-04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30870-06.htm"))
		{
			st.giveItems(REWARD_BOX, 1, false);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;

		if(npc.getNpcId() == KURTIZ)
		{
			if(st.getCond() == 0)
				htmtext = "30870-01.htm";
			else if(st.getCond() == 2)
				htmtext = "30870-05.htm";
		}
		return htmtext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == KURTIZ)
			htmltext = "30870-07.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
		return null;
	}
}
