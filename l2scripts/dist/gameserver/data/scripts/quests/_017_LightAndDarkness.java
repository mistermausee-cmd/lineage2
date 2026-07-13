package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _017_LightAndDarkness extends Quest
{

	private static final int EXP_REWARD = 1469840;	private static final int SP_REWARD = 352; 	public _017_LightAndDarkness()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(31517);

		addTalkId(31508);
		addTalkId(31509);
		addTalkId(31510);
		addTalkId(31511);

		addQuestItem(7168);
		addLevelCheck("dark_presbyter_q0017_03.htm", 61);
		addQuestCompletedCheck("dark_presbyter_q0017_03.htm", 15);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("dark_presbyter_q0017_04.htm"))
		{
			st.setCond(1);
			st.giveItems(7168, 4);
		}
		else if(event.equals("blessed_altar1_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(2);
		}
		else if(event.equals("blessed_altar2_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(3);
		}
		else if(event.equals("blessed_altar3_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(4);
		}
		else if(event.equals("blessed_altar4_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(5);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 31517)
		{
			if(cond == 0)
				htmltext = "dark_presbyter_q0017_01.htm";
			else if(cond > 0 && cond < 5 && st.getQuestItemsCount(7168) > 0)
				htmltext = "dark_presbyter_q0017_05.htm";
			else if(cond > 0 && cond < 5 && st.getQuestItemsCount(7168) == 0)
			{
				htmltext = "dark_presbyter_q0017_06.htm";
				st.finishQuest();
			}
			else if(cond == 5 && st.getQuestItemsCount(7168) == 0)
			{
				htmltext = "dark_presbyter_q0017_07.htm";
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
			}
		}
		else if(npcId == 31508)
		{
			if(cond == 1)
				if(st.getQuestItemsCount(7168) != 0)
					htmltext = "blessed_altar1_q0017_01.htm";
				else
					htmltext = "blessed_altar1_q0017_03.htm";
			else if(cond == 2)
				htmltext = "blessed_altar1_q0017_05.htm";
		}
		else if(npcId == 31509)
		{
			if(cond == 2)
				if(st.getQuestItemsCount(7168) != 0)
					htmltext = "blessed_altar2_q0017_01.htm";
				else
					htmltext = "blessed_altar2_q0017_03.htm";
			else if(cond == 3)
				htmltext = "blessed_altar2_q0017_05.htm";
		}
		else if(npcId == 31510)
		{
			if(cond == 3)
				if(st.getQuestItemsCount(7168) != 0)
					htmltext = "blessed_altar3_q0017_01.htm";
				else
					htmltext = "blessed_altar3_q0017_03.htm";
			else if(cond == 4)
				htmltext = "blessed_altar3_q0017_05.htm";
		}
		else if(npcId == 31511)
			if(cond == 4)
				if(st.getQuestItemsCount(7168) != 0)
					htmltext = "blessed_altar4_q0017_01.htm";
				else
					htmltext = "blessed_altar4_q0017_03.htm";
			else if(cond == 5)
				htmltext = "blessed_altar4_q0017_05.htm";
		return htmltext;
	}
}