package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10815_StepUp extends Quest
{
	// NPC's
	private static final int ERIK = 30868;

	// Item's
	private static final int CERTIF4 = 45626;


	public _10815_StepUp()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ERIK);
		addTalkId(ERIK);
		addLevelCheck("sir_eric_rodemai_q10815_02.htm", 99);
		addNobleCheck("sir_eric_rodemai_q10815_02.htm", true);
		addItemHaveCheck("sir_eric_rodemai_q10815_03.htm", 45627, 1);
		addCustomLog(1, "CHAT_WORLD_MESSAGES", 581511, 120);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("sir_eric_rodemai_q10815_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("sir_eric_rodemai_q10815_09.htm"))
		{
			st.giveItems(CERTIF4, 1, false);
			st.giveItems(45642, 1);
			if (checkReward(st))
				htmltext = "sir_eric_rodemai_q10815_10.htm";
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
			case ERIK:
				if (cond == 0)
					htmltext = "sir_eric_rodemai_q10815_01.htm";
				else if (cond == 1 && st.get("CHAT_WORLD_MESSAGES") != null && Integer.parseInt(st.get("CHAT_WORLD_MESSAGES")) >= 120)
				{
					st.setCond(2);
					htmltext = "sir_eric_rodemai_q10815_08.htm";
				}
				else if (cond == 1)
					htmltext = "sir_eric_rodemai_q10815_07.htm";
				else if (cond == 2)
					htmltext = "sir_eric_rodemai_q10815_08.htm";
				break;
		}
		return htmltext;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getUsedAbilitiesPoints() >= 16 && st.haveQuestItem(45623) && st.haveQuestItem(45624) && st.haveQuestItem(45625) && st.haveQuestItem(45626))
		{
			st.getPlayer().getQuestState(10811).setCond(3);
			return true;
		}

		return false;
	}
}
