package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _031_SecretBuriedInTheSwamp extends Quest
{
	int ABERCROMBIE = 31555;
	int FORGOTTEN_MONUMENT_1 = 31661;
	int FORGOTTEN_MONUMENT_2 = 31662;
	int FORGOTTEN_MONUMENT_3 = 31663;
	int FORGOTTEN_MONUMENT_4 = 31664;
	int CORPSE_OF_DWARF = 31665;

	int KRORINS_JOURNAL = 7252;

	private static final int EXP_REWARD = 1650970;	private static final int SP_REWARD = 396; 	public _031_SecretBuriedInTheSwamp()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ABERCROMBIE);

		for(int i = 31661; i <= 31665; i++)
			addTalkId(i);

		addQuestItem(KRORINS_JOURNAL);
		addLevelCheck("31555-0a.htm", 66/*, 76*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;
		if(event.equals("31555-1.htm"))
		{
			st.setCond(1);
		}
		else if(event.equals("31665-1.htm") && cond == 1)
		{
			st.setCond(2);
			st.giveItems(KRORINS_JOURNAL, 1);
		}
		else if(event.equals("31555-4.htm") && cond == 2)
			st.setCond(3);
		else if(event.equals("31661-1.htm") && cond == 3)
			st.setCond(4);
		else if(event.equals("31662-1.htm") && cond == 4)
			st.setCond(5);
		else if(event.equals("31663-1.htm") && cond == 5)
			st.setCond(6);
		else if(event.equals("31664-1.htm") && cond == 6)
		{
			st.setCond(7);
		}
		else if(event.equals("31555-7.htm") && cond == 7)
		{
			st.takeItems(KRORINS_JOURNAL, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 343430);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == ABERCROMBIE)
		{
			if(cond == 0)
				htmltext = "31555-0.htm";
			else if(cond == 1)
				htmltext = "31555-2.htm";
			else if(cond == 2)
				htmltext = "31555-3.htm";
			else if(cond == 3)
				htmltext = "31555-5.htm";
			else if(cond == 7)
				htmltext = "31555-6.htm";
		}
		else if(npcId == CORPSE_OF_DWARF)
		{
			if(cond == 1)
				htmltext = "31665-0.htm";
			else if(cond == 2)
				htmltext = "31665-2.htm";
		}
		else if(npcId == FORGOTTEN_MONUMENT_1)
		{
			if(cond == 3)
				htmltext = "31661-0.htm";
			else if(cond > 3)
				htmltext = "31661-2.htm";
		}
		else if(npcId == FORGOTTEN_MONUMENT_2)
		{
			if(cond == 4)
				htmltext = "31662-0.htm";
			else if(cond > 4)
				htmltext = "31662-2.htm";
		}
		else if(npcId == FORGOTTEN_MONUMENT_3)
		{
			if(cond == 5)
				htmltext = "31663-0.htm";
			else if(cond > 5)
				htmltext = "31663-2.htm";
		}
		else if(npcId == FORGOTTEN_MONUMENT_4)
			if(cond == 6)
				htmltext = "31664-0.htm";
			else if(cond > 6)
				htmltext = "31664-2.htm";
		return htmltext;
	}
}