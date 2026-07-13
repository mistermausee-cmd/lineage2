package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10757_QuietingtheStorm extends Quest
{
	// NPC's
	private static final int FIO = 33963;

	// Monster's
	private static final int VORTEXWIND = 23417;
	private static final int[] GIANT = {23419, 23420};

	private static final String GigantW = "GigantW";
	private static final String Vortex = "VORTEX";

	private static final int EXP_REWARD = 808754;	private static final int SP_REWARD = 151; 	public _10757_QuietingtheStorm()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(FIO);
		addTalkId(FIO);
		addKillId(VORTEXWIND);
		addKillId(GIANT);
		addKillNpcWithLog(1, 1023417, Vortex, 5, VORTEXWIND);
		addKillNpcWithLog(1, 575711, GigantW, 1, GIANT);
		addQuestCompletedCheck("33963-0.htm", 10756);
		addLevelCheck("33963-0.htm", 24);
		addRaceCheck("33963-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33963-8.htm"))
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
			case FIO:
				if(cond == 0)
					htmltext = "33963-1.htm";
				else if (cond == 1)
					htmltext = "33963-4.htm";
				else if (cond == 2)
					htmltext = "33963-7.htm";
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
				st.unset(GigantW);
				st.unset(Vortex);
				st.setCond(2);
			}
		}
		return null;
	}
}