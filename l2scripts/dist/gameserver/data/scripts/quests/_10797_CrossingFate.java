package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10797_CrossingFate extends Quest
{
	// NPC's
	private static final int EYEAGROS = 31683;

	//Mobs
	private static final int DAIMON = 27499;
	// Item's
	
	private static final int ENCHANTARMOR = 23418;


	private static final int EXP_REWARD = 306167814;	private static final int SP_REWARD = 653; 	public _10797_CrossingFate()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(EYEAGROS);
		addTalkId(EYEAGROS);
		addKillId(DAIMON);
		addQuestCompletedCheck("31683-0.htm", 10796);
		addLevelCheck("31683-0.htm", 70/*, 75*/);
		addRaceCheck("31683-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31683-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("31683-7.htm"))
		{
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
			case EYEAGROS:
				if(cond == 0)
					htmltext = "31683-1.htm";
				else if (cond == 1)
					htmltext = "31683-5.htm";
				else if (cond == 2)
					htmltext = "31683-6.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
		return null;
	}
}