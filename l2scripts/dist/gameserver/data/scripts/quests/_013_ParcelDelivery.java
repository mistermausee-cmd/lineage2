package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _013_ParcelDelivery extends Quest
{
	private static final int PACKAGE = 7263;

	private static final int EXP_REWARD = 1279632;	private static final int SP_REWARD = 307; 	public _013_ParcelDelivery()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(31274);

		addTalkId(31274);
		addTalkId(31539);

		addQuestItem(PACKAGE);
		addLevelCheck("mineral_trader_fundin_q0013_0103.htm", 74/*, 80*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("mineral_trader_fundin_q0013_0104.htm"))
		{
			st.setCond(1);
			st.giveItems(PACKAGE, 1);
		}
		else if(event.equalsIgnoreCase("warsmith_vulcan_q0013_0201.htm"))
		{
			st.takeItems(PACKAGE, -1);
			st.giveItems(ADENA_ID, 271980, true);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 31274)
		{
			if(cond == 0)
				htmltext = "mineral_trader_fundin_q0013_0101.htm";
			else if(cond == 1)
				htmltext = "mineral_trader_fundin_q0013_0105.htm";
		}
		else if(npcId == 31539)
			if(cond == 1 && st.getQuestItemsCount(PACKAGE) == 1)
				htmltext = "warsmith_vulcan_q0013_0101.htm";
		return htmltext;
	}
}