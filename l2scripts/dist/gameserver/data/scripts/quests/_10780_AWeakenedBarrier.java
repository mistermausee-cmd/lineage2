package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10780_AWeakenedBarrier extends Quest
{
	// NPC's
	private static final int ENDI = 33845;
	private static final int BAIKON = 33846;

	// Monster's
	private static final int[] MONSTERS = {20555, 20558, 23305, 23306, 23307, 23308};
	private static final String Spores = "spores";

	private static final int EXP_REWARD = 15108843;
	private static final int SP_REWARD = 914; 

	public _10780_AWeakenedBarrier()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ENDI);
		addTalkId(BAIKON);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 578011, Spores, 80, MONSTERS);
		addLevelCheck("33845-0.htm", 52/*, 58*/);
		addRaceCheck("33845-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33845-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33846-2.htm"))
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
			case ENDI:
				if(cond == 0)
					htmltext = "33845-1.htm";
				else if (cond == 1 || cond == 2)
					htmltext = "33845-5.htm";
			break;

			case BAIKON:
				if (cond == 2)
					htmltext = "33846-1.htm";
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
				st.unset(Spores);
				st.setCond(2);
			}
		}
		return null;
	}
}