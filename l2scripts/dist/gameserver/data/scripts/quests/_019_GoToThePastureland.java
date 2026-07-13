package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _019_GoToThePastureland extends Quest
{
	int VLADIMIR = 31302;
	int TUNATUN = 31537;

	int BEAST_MEAT = 7547;

	private static final int EXP_REWARD = 1456218;	private static final int SP_REWARD = 349; 	public _019_GoToThePastureland()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(VLADIMIR);

		addTalkId(VLADIMIR);
		addTalkId(TUNATUN);

		addQuestItem(BEAST_MEAT);
		addLevelCheck("trader_vladimir_q0019_0103.htm", 82);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("trader_vladimir_q0019_0104.htm"))
		{
			st.giveItems(BEAST_MEAT, 1);
			st.setCond(1);
		}
		if(event.equals("beast_herder_tunatun_q0019_0201.htm"))
		{
			st.takeItems(BEAST_MEAT, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 299928);
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
		if(npcId == VLADIMIR)
		{
			if(cond == 0)
				htmltext = "trader_vladimir_q0019_0101.htm";
			else
				htmltext = "trader_vladimir_q0019_0105.htm";
		}
		else if(npcId == TUNATUN)
			if(st.getQuestItemsCount(BEAST_MEAT) >= 1)
				htmltext = "beast_herder_tunatun_q0019_0101.htm";
			else
			{
				htmltext = "beast_herder_tunatun_q0019_0202.htm";
				st.finishQuest();
			}
		return htmltext;
	}
}