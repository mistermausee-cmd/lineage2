package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10396_WitchOfSpores extends Quest
{

	private static final int MOEN = 30196;

	private static final int ORFEN = 29014;

	private static final long EXP_REWARD = 13628160;
	private static final long SP_REWARD = 16353;


	public _10396_WitchOfSpores()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(MOEN);
		addTalkId(MOEN);
		addKillId(ORFEN);

		addLevelCheck("mouen_q10396_02.htm", 52/*, 67*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("mouen_q10396_06.htm"))
		{
			st.setCond(1);
		}

		else if (event.equalsIgnoreCase("mouen_q10396_09.htm"))
		{
			st.giveItems(57, 136260);
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
			case MOEN:
				if (cond == 0)
					htmltext = "mouen_q10396_01.htm";
				else if (cond == 1)
					htmltext = "mouen_q10396_07.htm";
				else if (cond == 2)
					htmltext = "mouen_q10396_08.htm";
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