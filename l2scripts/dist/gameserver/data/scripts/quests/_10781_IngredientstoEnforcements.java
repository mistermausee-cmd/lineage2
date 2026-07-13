package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10781_IngredientstoEnforcements extends Quest
{
	// NPC's
	private static final int BAIKON = 33846;

	// Monster's
	private static final int[] MONSTER = {23310, 23309};

	// Item's
	
	private static final int SPIRITFRAGMENT = 39721;
	private static final int ENCHANTARMOR = 23419;

	private static final int EXP_REWARD = 19688585;
	private static final int SP_REWARD = 914; 

	public _10781_IngredientstoEnforcements()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BAIKON);
		addTalkId(BAIKON);
		addKillId(MONSTER);
		addQuestCompletedCheck("33846-0.htm", 10780);
		addLevelCheck("33846-0.htm", 52/*, 58*/);
		addRaceCheck("33846-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33846-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33846-7.htm"))
		{
			
			st.takeItems(SPIRITFRAGMENT, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
			case BAIKON:
				if(cond == 0)
					htmltext = "33846-1.htm";
				else if (cond == 1)
					htmltext = "33846-5.htm";
				else if (cond == 2)
					htmltext = "33846-6.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			st.giveItems(SPIRITFRAGMENT, 1, false);
			if(st.getQuestItemsCount(SPIRITFRAGMENT) >= 80)
				st.setCond(2);
		}
		return null;
	}
}