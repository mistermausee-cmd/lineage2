package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10532_UncoveringTheConspiracy extends Quest
{
	// NPC's
	private static final int NAMO = 33973;

	//Монстры
	private static final int[] MOBS = {23430, 23431, 23432, 23433, 23441, 23442, 23443, 23444};

	public static final String A_LIST = "A_LIST";

	private static final int EXP_REWARD = 543080087;
	private static final int SP_REWARD = 30466;

	public _10532_UncoveringTheConspiracy()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NAMO);
		addTalkId(NAMO);
		addKillId(MOBS);
		addKillNpcWithLog(1, 553211, A_LIST, 200, MOBS);
		addRaceCheck("dv_guide_namoo_q10532_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("dv_guide_namoo_q10532_02.htm", 81/*, 84*/);
		addQuestCompletedCheck("dv_guide_namoo_q10532_02.htm", 10531);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("dv_guide_namoo_q10532_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("dv_guide_namoo_q10532_08.htm"))
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
			case NAMO:
				if(cond == 0)
					htmltext = "dv_guide_namoo_q10532_01.htm";
				else if (cond == 1)
					htmltext = "dv_guide_namoo_q10532_06.htm";
				else if (cond == 2)
					htmltext = "dv_guide_namoo_q10532_07.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if (updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(2);
			}
		}
		return null;
	}
}