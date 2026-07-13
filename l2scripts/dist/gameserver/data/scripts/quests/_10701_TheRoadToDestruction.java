package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10701_TheRoadToDestruction extends Quest
{
	// NPCs
	private static final int KEUCEREUS = 32548;
	private static final int ALLENOS = 32526;
	// Item
	private static final int ITEM = 38577;

	private static final int EXP_REWARD = 8173305;	private static final int SP_REWARD = 1961; 	public _10701_TheRoadToDestruction()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, ALLENOS);
		addLevelCheck(KEUCEREUS, "32548-06.htm", 93);
		addQuestItem(ITEM);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32548-04.htm"))
		{
			st.setCond(1);
			st.giveItems(ITEM, 1);
		}
		else if(event.equalsIgnoreCase("32526-02.htm"))
		{
			st.giveItems(57, 17612, true);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(ITEM, -1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if(npc.getNpcId() == KEUCEREUS)
		{
			if(st.getCond() == 0)
				htmtext = "32548-01.htm";
			else if(st.getCond() == 1)
				htmtext = "32548-05.htm";
		}
		else if(npc.getNpcId() == ALLENOS)
		{
			if(st.getCond() == 1)
				htmtext = "32526-01.htm";
		}
		return htmtext;
	}

}