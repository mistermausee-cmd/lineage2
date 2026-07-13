package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10786_ResidentProblemSolver extends Quest
{
	// NPC's
	private static final int ZUBAN = 33867;

	// Monster's
	private static final int[] MONSTERS = {21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 20674, 20974, 20975, 20976};

	// Item's
	private static final String Massacr = "Massacr";

	private static final int EXP_REWARD = 38226567;
	private static final int SP_REWARD = 1500; 

	public _10786_ResidentProblemSolver()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ZUBAN);
		addTalkId(ZUBAN);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 578611, Massacr, 150, MONSTERS);
		addLevelCheck("33867-0.htm", 61/*, 65*/);
		addRaceCheck("33867-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33867-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33867-7.htm"))
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
			case ZUBAN:
				if(cond == 0)
					htmltext = "33867-1.htm";
				else if (cond == 1)
					htmltext = "33867-5.htm";
				else if (cond == 2)
					htmltext = "33867-6.htm";
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
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(Massacr);
				st.setCond(2);
			}
		}
		return null;
	}
}