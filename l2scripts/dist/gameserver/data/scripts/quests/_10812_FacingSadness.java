package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10812_FacingSadness extends Quest
{
	// NPC's
	private static final int ELIKI = 31620;

	// Monster's
	private static final int[] MONSTERS = { 23314, 23315, 23316, 23317, 23318, 23319, 23320, 23321, 23322, 23323, 23324, 23325, 23326, 23327, 23328, 23329,
			23354, 23355, 23356, 23357, 23358, 23360, 23361, 23362, 23363, 23364, 23365, 23366, 23367, 23468, 23369, 23370, 23372, 23373,
			23384, 23385, 23386, 23387, 23388, 23389, 23390, 23391, 23392, 23393,
			23394, 23395, 23396, 23397, 23398, 23399 };

	// Item's
	private static final int DISPOSAL = 45871;
	private static final int CERTIF1 = 45623;

	private static final int EXP_REWARD = 0;	private static final int SP_REWARD = 498204432; 	public _10812_FacingSadness()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ELIKI);
		addTalkId(ELIKI);
		addKillId(MONSTERS);
		addQuestItem(DISPOSAL);
		addLevelCheck("verdure_sage_ellikia_q10812_02.htm", 99);
		addNobleCheck("verdure_sage_ellikia_q10812_02.htm", true);
		addItemHaveCheck("verdure_sage_ellikia_q10812_03.htm", 45627, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("verdure_sage_ellikia_q10812_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("verdure_sage_ellikia_q10812_09.htm"))
		{
			st.takeItems(DISPOSAL, -1);
			st.giveItems(CERTIF1, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			if (checkReward(st))
				htmltext = "verdure_sage_ellikia_q10812_10.htm";
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
			case ELIKI:
				if (cond == 0)
					htmltext = "verdure_sage_ellikia_q10812_01.htm";
				else if (cond == 1)
					htmltext = "verdure_sage_ellikia_q10812_07.htm";
				else if (cond == 2)
					htmltext = "verdure_sage_ellikia_q10812_08.htm";
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
			if (st.rollAndGive(DISPOSAL, 1, 1, 8000, 90))
				st.setCond(2);
		}
		return null;
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