package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10824_ConfrontingTheGreatestDanger extends Quest
{
	// NPC's
	private static final int MERLOT = 34018;

	// Monster's
	private static final int[] RAIDBOSS = { 29260, 29261, 29263, 29266, 29267, 29268, 29269, 29270, 29271, 29272, 29273, 29274, 29275, 29276,
			29277, 29278, 29279, 29280, 29281, 29282, 29283, 29284, 29285, 29286, 29287, 29288, 29289, 29290, 29291, 29292, 29293, 29294, 29295, 29296, 29297, 29298,
			29299, 29300};

	// Item's
	private static final int PROOFOFBATTLE = 46058;
	private static final int CERTIF1 = 46056;
	private static final int BOOK = 45926;


	public _10824_ConfrontingTheGreatestDanger()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(MERLOT);
		addTalkId(MERLOT);
		addKillId(RAIDBOSS);
		addLevelCheck("merlot_enter_q10824_02.htm", 99);
		addNobleCheck("merlot_enter_q10824_02.htm", true);
		addItemHaveCheck("merlot_enter_q10824_03.htm", 45637, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("merlot_enter_q10824_06.htm"))
		{
			st.setCond(2); //TODO –ейд иных измерений
		}
		else if (event.equalsIgnoreCase("merlot_enter_q10824_09.htm"))
		{
			st.takeItems(PROOFOFBATTLE, -1);
			st.giveItems(CERTIF1, 1, false);
			st.giveItems(BOOK, 1, false);
			if (checkReward(st))
				htmltext = "merlot_enter_q10824_10.htm";
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
			case MERLOT:
				if (cond == 0)
					htmltext = "merlot_enter_q10824_01.htm";
				else if (cond == 1 && st.getQuestItemsCount(PROOFOFBATTLE) > 2)
				{
					st.setCond(2);
					htmltext = "merlot_enter_q10824_08.htm";
				}
				else if (cond == 1)
					htmltext = "merlot_enter_q10824_07.htm";
				else if (cond == 2)
					htmltext = "merlot_enter_q10824_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			if(ArrayUtils.contains(RAIDBOSS, npcId))
				st.giveItems(PROOFOFBATTLE, 1);
			if(st.getQuestItemsCount(PROOFOFBATTLE) >=3)
				st.setCond(2);
		}
		return null;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getDualClassLevel() > 99 && st.haveQuestItem(46056) && st.haveQuestItem(46057) && st.haveQuestItem(45635) && st.haveQuestItem(45636))
		{
			st.getPlayer().getQuestState(10823).setCond(2);
			return true;
		}

		return false;
	}
}