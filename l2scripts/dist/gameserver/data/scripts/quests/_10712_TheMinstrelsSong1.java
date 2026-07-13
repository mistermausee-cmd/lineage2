package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

//By Evil_dnk

public class _10712_TheMinstrelsSong1 extends Quest
{
	// NPC's
	private static final int LIBERADO = 33955;

	private static final int EXP_REWARD = 8656128;	private static final int SP_REWARD = 10386; 	public _10712_TheMinstrelsSong1()
	{
		super(PARTY_NONE, ONETIME);
		addTalkId(LIBERADO);
		addLevelCheck(NO_QUEST_DIALOG, 85/*, 87*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("reward"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 50508);
			st.giveItems(47056, 5);
			st.giveItems(47057, 5);
			st.giveItems(47058, 5);
			st.finishQuest();
			htmltext = "";
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
			case LIBERADO:
				if(cond == 1)
					htmltext = "33955-1.htm";
			break;

		}
		return htmltext;
	}
}