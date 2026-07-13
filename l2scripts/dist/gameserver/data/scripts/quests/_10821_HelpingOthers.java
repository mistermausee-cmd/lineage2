package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10821_HelpingOthers extends Quest
{
	// NPC's
	private static final int KRISTOF = 30756;

	//Items
	private static final int CERTIF3 = 45631;
	private static final int MARKOFNEW = 33804;
	private static final int REWARD = 45928;

	public _10821_HelpingOthers()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(KRISTOF);
		addTalkId(KRISTOF);
		addLevelCheck("sir_kristof_rodemai_q10821_02.htm", 99);
		addNobleCheck("sir_kristof_rodemai_q10821_02.htm", true);
		addItemHaveCheck("sir_kristof_rodemai_q10821_03.htm", 45632, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("sir_kristof_rodemai_q10821_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("sir_kristof_rodemai_q10821_09.htm"))
		{
			if(st.getQuestItemsCount(MARKOFNEW) >= 45000)
			{
				st.giveItems(CERTIF3, 1, false);
				st.giveItems(REWARD, 1, false);
				st.takeItems(MARKOFNEW, 45000);
				if (checkReward(st))
					htmltext = "sir_kristof_rodemai_q10821_10.htm";
				st.finishQuest();
			}
			else
				htmltext = "sir_kristof_rodemai_q10821_07.htm";
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
			case KRISTOF:
				if (cond == 0)
					htmltext = "sir_kristof_rodemai_q10821_01.htm";
				else if (cond == 1 && st.getQuestItemsCount(MARKOFNEW) < 45000)
					htmltext = "sir_kristof_rodemai_q10821_07.htm";
				else if (cond == 1)
					htmltext = "sir_kristof_rodemai_q10821_08.htm";
				break;
		}
		return htmltext;
	}


	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getLevel() > 99 && st.haveQuestItem(45628) && st.haveQuestItem(45629) && st.haveQuestItem(45630) && st.haveQuestItem(45631))
		{
			st.getPlayer().getQuestState(10817).setCond(2);
			return true;
		}
		return false;
	}

}


