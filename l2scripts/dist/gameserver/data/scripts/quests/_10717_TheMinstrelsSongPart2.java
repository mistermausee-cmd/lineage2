package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
//by Evil_dnk

public class _10717_TheMinstrelsSongPart2 extends Quest
{
	// NPC's
	private static final int NYASHIRO = 33956;


	private static final int EXP_REWARD = 16035654;	private static final int SP_REWARD = 53652; 	public _10717_TheMinstrelsSongPart2()
	{
		super(PARTY_NONE, ONETIME);
		addTalkId(NYASHIRO);
		addLevelCheck(NO_QUEST_DIALOG, 88, 89);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33956-3.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 53652);
			st.giveItems(47054, 5);
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
			case NYASHIRO:
				if(cond == 1)
					htmltext = "33956-1.htm";
				break;

		}
		return htmltext;
	}
}