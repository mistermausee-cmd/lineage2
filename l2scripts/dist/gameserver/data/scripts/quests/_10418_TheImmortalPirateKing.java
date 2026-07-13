package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10418_TheImmortalPirateKing extends Quest
{
	private static final int JERION = 30196;

	private static final int ZAKEN = 29181;

	private static final long EXP_REWARD = 34720560;
	private static final long SP_REWARD = 41664;

	public _10418_TheImmortalPirateKing()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(JERION);
		addTalkId(JERION);
		addKillId(ZAKEN);

		addLevelCheck("jeronin_q10418_02.htm", 83/*, 90*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("jeronin_q10418_06.htm"))
		{
			st.setCond(1);
		}

		else if (event.equalsIgnoreCase("jeronin_q10418_09.htm"))
		{
			st.giveItems(57, 359064);
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
			case JERION:
				if (cond == 0)
					htmltext = "jeronin_q10418_01.htm";
				else if (cond == 1)
					htmltext = "jeronin_q10418_07.htm";
				else if (cond == 2)
					htmltext = "jeronin_q10418_08.htm";
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
