package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//by Evil_dnk

public class _10726_TheMinstrelsSongPart5 extends Quest
{
	// NPC's
	private static final int BORMETU = 33958;


	private static final int EXP_REWARD = 66942396;	private static final int SP_REWARD = 63816; 	public _10726_TheMinstrelsSongPart5()
	{
		super(PARTY_NONE, ONETIME);
		addTalkId(BORMETU);
		addLevelCheck(NO_QUEST_DIALOG, 95, 96);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33958-3.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 60810);
			st.giveItems(47060, 5);
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
			case BORMETU:
				if(cond == 1)
					htmltext = "33958-1.htm";
				break;

		}
		return htmltext;
	}
}