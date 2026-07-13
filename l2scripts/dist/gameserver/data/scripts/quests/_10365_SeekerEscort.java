package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10365_SeekerEscort extends Quest
{
	private static final int DEP = 33453;
	private static final int SEBION = 32978;

	private static final int TONIK = 47607;

	private static final int[] MOBS = {23122, 22993, 22995};

	private static final int EXP_REWARD = 172000;
	private static final int SP_REWARD = 15;

	public _10365_SeekerEscort()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(DEP);
		addTalkId(DEP, SEBION);
		addKillId(MOBS);
		addRaceCheck("si_illusion_def_q10365_02a", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_def_q10365_02", 15/*, 25*/);
		addQuestCompletedCheck("si_illusion_def_q10365_02", 10364);
		addQuestItemWithLog(1, 0, 20, 47607);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("si_illusion_def_q10365_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("si_illusion_sebion_q10365_03.htm"))
		{
			st.takeItems(TONIK, -1);
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
			case DEP:
				if (cond == 0)
					htmltext = "si_illusion_def_q10365_01.htm";
				else if (cond == 1)
					htmltext = "si_illusion_def_q10365_06.htm";
				else if (cond == 2)
					htmltext = "si_illusion_def_q10365_07.htm";
				break;

			case SEBION:
				if (cond != 2)
					htmltext = "si_illusion_sebion_q10365_01.htm";
				else if (cond == 2)
					htmltext = "si_illusion_sebion_q10365_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.giveItems(TONIK, 1);
			if (st.getQuestItemsCount(TONIK) >= 20)
				st.setCond(2);
		}
		return null;
	}
}