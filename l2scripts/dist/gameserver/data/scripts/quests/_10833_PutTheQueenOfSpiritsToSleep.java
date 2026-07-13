package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10833_PutTheQueenOfSpiritsToSleep extends Quest
{
	// NPC's
	private static final int NPC1 = 34054;

	// Monster's
	private static final int[] MONSTERS = {26131};

	// Item's
	private static final int DROPITEM = 45837;
	private static final long EXP_REWARD = 5932440000l;
	private static final long SP_REWARD = 14237820;
	private static final int REWARD1 = 46158;
	private static final int REWARD2 = 36514;
	private static final int REWARD3 = 46152;

	public _10833_PutTheQueenOfSpiritsToSleep()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addKillId(MONSTERS);
		addQuestItem(DROPITEM);
		addLevelCheck("el_apple_de_khan_q10833_02.htm", 100);
		addQuestCompletedCheck("el_apple_de_khan_q10833_02.htm", 10832);
		addItemHaveCheck("el_apple_de_khan_q10833_03.htm", 45848, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("el_apple_de_khan_q10833_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("el_apple_de_khan_q10833_09.htm"))
		{
			st.takeItems(DROPITEM, -1);
			st.giveItems(REWARD1, 1);
			st.giveItems(REWARD2, 1);
			st.giveItems(REWARD3, 1);
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
					htmltext = "el_apple_de_khan_q10833_01.htm";
				else if (cond == 1)
					htmltext = "el_apple_de_khan_q10833_07.htm";
				else if (cond == 2)
					htmltext = "el_apple_de_khan_q10833_08.htm";
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
				st.giveItems(DROPITEM,1, false);
			st.setCond(2);
		}
		return null;
	}

}