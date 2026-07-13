package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _020_BringUpWithLove extends Quest
{
	private static final int TUNATUN = 31537;
	// Item
	private static final int BEAST_WHIP = 15473;
	private static final int CRYSTAL = 9553;
	private static final int JEWEL = 7185;

	private static final int EXP_REWARD = 26950000;	private static final int SP_REWARD = 6468; 	public _020_BringUpWithLove()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(TUNATUN);
		addTalkId(TUNATUN);
		addLevelCheck("31537-00.htm", 82);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(npc.getNpcId() == TUNATUN)
		{
			if(event.equalsIgnoreCase("31537-12.htm"))
			{
				st.setCond(1);
			}
			else if(event.equalsIgnoreCase("31537-03.htm"))
			{
				if(st.getQuestItemsCount(BEAST_WHIP) > 0)
					return "31537-03a.htm";
				else
					st.giveItems(BEAST_WHIP, 1);
			}
			else if(event.equalsIgnoreCase("31537-15.htm"))
			{
				st.takeItems(JEWEL, -1);
				st.giveItems(CRYSTAL, 1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if(npc.getNpcId() == TUNATUN)
		{
			if(st.getCond() == 0)
				htmtext = "31537-01.htm";
			else if(st.getCond() == 1)
				htmtext = "31537-13.htm";
			else if(st.getCond() == 2)
				htmtext = "31537-14.htm";
		}
		return htmtext;
	}
}