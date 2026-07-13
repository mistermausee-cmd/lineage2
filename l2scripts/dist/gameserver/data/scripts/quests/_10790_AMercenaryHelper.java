package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10790_AMercenaryHelper extends Quest
{
	// NPC's
	private static final int TOKARA = 33847;

	// Monster's
	private static final int[] MONSTERS = {21508, 21509, 21510, 21511, 21512, 21513, 21514, 21515, 21516, 21517, 21518, 21519};

	private static final String STAKATO = "stakato";

	private static final int EXP_REWARD = 134158421;
	private static final int SP_REWARD = 226;

	public _10790_AMercenaryHelper()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(TOKARA);
		addTalkId(TOKARA);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 579011, STAKATO , 300, MONSTERS);
		addLevelCheck("chaser_dokara_q10790_02.htm", 65/*, 70*/);
		addClassIdCheck("chaser_dokara_q10790_02.htm", 182, 184, 186, 188, 190);
		addRaceCheck("chaser_dokara_q10790_02a.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("chaser_dokara_q10790_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("chaser_dokara_q10790_09.htm"))
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
			case TOKARA:
				if(cond == 0)
					htmltext = "chaser_dokara_q10790_01.htm";
				else if (cond == 1)
					htmltext = "chaser_dokara_q10790_07.htm";
				else if (cond == 2)
					htmltext = "chaser_dokara_q10790_08.htm";
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
				st.unset(STAKATO);
				st.setCond(2);
			}
		}
		return null;
	}
}