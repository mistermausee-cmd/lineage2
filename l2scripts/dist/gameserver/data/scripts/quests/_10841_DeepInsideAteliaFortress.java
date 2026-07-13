package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10841_DeepInsideAteliaFortress extends Quest
{
	// NPC's
	private static final int NPC1 = 34057;
	private static final int NPC2 = 34051;

	// Monster's
	private static final int[] MONSTERS_A = {26124};

	//Reward EXP SP
	private static final long EXP_REWARD = 7262301690L;
	private static final long SP_REWARD = 17429400;

	// Item's
	private static final int DROPITEM = 46144;
	private static final int REWARD1 = 46151;
	private static final int REWARD2 = 45937;

	public _10841_DeepInsideAteliaFortress()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addTalkId(NPC2);
		addQuestItem(DROPITEM);
		addKillId(MONSTERS_A);
		addLevelCheck("ellikia_vanguard_q10841_02.htm", 101);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("ellikia_vanguard_q10841_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("ar_kaysia_q10841_02.htm"))
		{
			st.giveItems(REWARD1, 1);
			st.giveItems(REWARD2, 1);
			st.takeItems(DROPITEM, -1);
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
			case NPC1:
				if (cond == 0)
					htmltext = "ellikia_vanguard_q10841_01.htm";
				else if (cond == 1)
					htmltext = "ellikia_vanguard_q10841_07.htm";
				else if (cond == 2)
					htmltext = "ellikia_vanguard_q10841_08.htm";
				break;

			case NPC2:
				if (cond == 2)
					htmltext = "ar_kaysia_q10841_01.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			st.giveItems(DROPITEM, 1, false);
			st.setCond(2);
		}
		return null;
	}
}