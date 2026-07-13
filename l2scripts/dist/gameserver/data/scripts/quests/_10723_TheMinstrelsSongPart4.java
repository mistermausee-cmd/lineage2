package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//by Evil_dnk

public class _10723_TheMinstrelsSongPart4 extends Quest
{
	// NPC's
	private static final int MERMEL = 33957;


	private static final int EXP_REWARD = 44549961;	private static final int SP_REWARD = 44574; 	public _10723_TheMinstrelsSongPart4()
	{
		super(PARTY_NONE, ONETIME);
		addTalkId(MERMEL);
		addLevelCheck(NO_QUEST_DIALOG, 92, 94);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33957-3.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(47059, 5);
			st.giveItems(57, 58707);
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
			case MERMEL:
				if(cond == 1)
					htmltext = "33957-1.htm";
				break;

		}
		return htmltext;
	}
}