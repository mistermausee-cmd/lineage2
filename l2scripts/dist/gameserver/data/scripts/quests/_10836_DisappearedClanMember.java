package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10836_DisappearedClanMember extends Quest
{
	// NPC's
	private static final int NPC1 = 34057;

	//Items
	private static final int ITEM1 = 46134;
	private static final int ITEM2 = 46135;
	private static final int ITEM3 = 46136;
	private static final int ITEM4 = 46137;
	private static final int ITEM5 = 46132;


    private static final int REWARD1 = 17527;
	private static final int REWARD2 = 35675;
	private static final int REWARD3 = 35676;

	public _10836_DisappearedClanMember()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addQuestItem(ITEM1);
		addQuestItem(ITEM2);
		addQuestItem(ITEM3);
		addQuestItem(ITEM4);
		addQuestItem(ITEM5);
		addLevelCheck("ellikia_vanguard_q10836_02.htm", 101);
		addNobleCheck("ellikia_vanguard_q10836_02.htm", true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("ellikia_vanguard_q10836_05.htm"))
		{
			st.giveItems(ITEM5, 1, false);
			st.setCond(1);
		}
		if (event.equalsIgnoreCase("ellikia_vanguard_q10836_09.htm"))
		{
			st.giveItems(REWARD1, 5);
			st.giveItems(REWARD2, 10);
			st.giveItems(REWARD3, 10);
			st.takeItems(ITEM1, -1);
			st.takeItems(ITEM2, -1);
			st.takeItems(ITEM3, -1);
			st.takeItems(ITEM4, -1);
			st.takeItems(ITEM5, -1);
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
					htmltext = "ellikia_vanguard_q10836_01.htm";
				else if (cond == 1)
					htmltext = "ellikia_vanguard_q10836_06.htm";
				else if (cond == 2)
					htmltext = "ellikia_vanguard_q10836_07.htm";
				break;
		}
		return htmltext;
	}
}

