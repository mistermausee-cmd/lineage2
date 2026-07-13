package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10524_TheAssassinationOfTheVarkaSilenosCommanderChief extends Quest
{
	// NPC's
	private static final int HANSEN = 33853;

	private static final int HORUS = 27503;

	private static final int EXP_REWARD = 351479151;
	private static final int SP_REWARD = 1839;

	public _10524_TheAssassinationOfTheVarkaSilenosCommanderChief()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(HORUS);
		addRaceCheck("hansen_q10524_02a.htm", Race.ERTHEIA);
		addLevelCheck("hansen_q10524_02a.htm", 76/*, 80*/);
		addQuestCompletedCheck("hansen_q10524_02a.htm", 10523);
		addClassTypeCheck("hansen_q10524_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("hansen_q10524_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("hansen_q10524_08.htm"))
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
			case HANSEN:
				if (cond == 0)
					htmltext = "hansen_q10524_01.htm";
				else if (cond == 1)
					htmltext = "hansen_q10524_06.htm";
				else if (cond == 2)
					htmltext = "hansen_q10524_07.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.setCond(2);
		}
		return null;
	}
}