package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;


//By Evil_dnk dev.fairytale-world.ru

public class _10366_RuinsStatusUpdate extends Quest
{
	private static final int SEBION = 32978;

	private static final int[] MOBS = {23122, 22993, 22994, 22995};

	public static final String A_LIST = "A_LIST";

	private static final int EXP_REWARD = 114000;
	private static final int SP_REWARD = 15;

	public _10366_RuinsStatusUpdate() 
	{

		super(PARTY_NONE, ONETIME);
		addStartNpc(SEBION);
		addTalkId(SEBION);
		addKillNpcWithLog(1, 536606, A_LIST, 40, MOBS);

		addRaceCheck("si_illusion_sebion_q10366_02a", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_sebion_q10366_02.", 17/*, 25*/);
		addQuestCompletedCheck("si_illusion_sebion_q10366_02.", 10365);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("si_illusion_sebion_q10366_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("si_illusion_sebion_q10366_08.htm"))
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
			case SEBION:
				if (cond == 0)
					htmltext = "si_illusion_sebion_q10366_01.htm";
				else if (cond == 1)
					htmltext = "si_illusion_sebion_q10366_06.htm";
				else if (cond == 2)
					htmltext = "si_illusion_sebion_q10366_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.setCond(2);
		}
		return null;
	}
}