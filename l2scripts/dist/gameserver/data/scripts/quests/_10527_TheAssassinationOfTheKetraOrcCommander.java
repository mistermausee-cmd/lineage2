package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10527_TheAssassinationOfTheKetraOrcCommander extends Quest
{
	// NPC's
	private static final int LUKONES = 33852;

	private static final int TAIR = 27500;

	private static final int EXP_REWARD = 327446943;
	private static final int SP_REWARD = 1839;

	public _10527_TheAssassinationOfTheKetraOrcCommander()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(LUKONES);
		addTalkId(LUKONES);
		addKillId(TAIR);
		addRaceCheck("rugoness_q10527_02a.htm", Race.ERTHEIA);
		addLevelCheck("rugoness_q10527_02.htm", 76, 80);
		addQuestCompletedCheck("rugoness_q10527_02.htm", 10526);
		addClassTypeCheck("rugoness_q10527_02.htm", ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("rugoness_q10527_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("rugoness_q10527_08.htm"))
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
			case LUKONES:
				if (cond == 0)
					htmltext = "rugoness_q10527_01.htm";
				else if (cond == 1)
					htmltext = "rugoness_q10527_06.htm";
				else if (cond == 2)
					htmltext = "rugoness_q10527_07.htm";
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