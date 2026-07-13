package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _829_MaphrsSalvation extends Quest
{
	// NPCs
	private static final int CLUTO = 34098;
	private static final int CLUTOEND = 34153;
	private static final int BOX = 34102;
	// Item
	private static final int ITEM = 46373;

	private static final long EXP_REWARD = 2422697985l;
	private static final int SP_REWARD = 5814450; 

	public _829_MaphrsSalvation()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(CLUTO);
		addTalkId(CLUTO);
		addTalkId(CLUTOEND);
		addTalkId(BOX);
		addLevelCheck(CLUTO, "as_blacksmith_kluto_q0829_02.htm", 99);
		addQuestItem(ITEM);
		addKillId(BOX);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("as_blacksmith_kluto_q0829_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("as_blacksmith_kluto_q0829_08.htm"))
		{
			st.giveItems(46375, 1);
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
		if(npc.getNpcId() == CLUTO)
		{
			if(st.getCond() == 0)
				htmtext = "as_blacksmith_kluto_q0829_01.htm";
			else if(st.getCond() == 1)
				htmtext = "as_blacksmith_kluto_q0829_06.htm";
			else if(st.getCond() == 2)
				htmtext = "as_blacksmith_kluto_q0829_07.htm";
		}
		else if(npc.getNpcId() == CLUTOEND)
		{
			if(st.getCond() == 2)
				htmtext = "as_blacksmith_kluto_q0829_07.htm";
		}
		else if(npc.getNpcId() == BOX)
		{
			if(st.getCond() == 1)
			{
				st.giveItems(ITEM, 1, false);
				st.setCond(2);
				npc.deleteMe();
				return null;
			}
		}
		return htmtext;
	}

}
