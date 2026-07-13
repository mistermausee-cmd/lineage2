package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10827_StepUpToLead extends Quest
{
	// NPC's
	private static final int GUSTAV = 30760;

	// Monster's
	private static final int[] RAIDBOSS = { 29260, 29261, 29263, 29266, 29267, 29268, 29269, 29270, 29271, 29272, 29273, 29274, 29275, 29276,
			29277, 29278, 29279, 29280, 29281, 29282, 29283, 29284, 29285, 29286, 29287, 29288, 29289, 29290, 29291, 29292, 29293, 29294, 29295, 29296, 29297, 29298,
			29299, 29300, 25824, 25843, 29022, 29163, 29176, 29181, 29195, 29196, 29233, 29235, 29236, 19160, 19253, 19254, 19255, 25603, 25696, 25697, 25698, 25760, 25763,
			25766, 25767, 25770, 25775, 25779, 25784, 25787, 25790, 25793, 25794, 25797, 25800, 25825, 25837, 25838, 25839, 25840, 25841, 25845, 25846, 25857, 25858,
			25866, 25867, 25868, 25871, 25875, 25880, 25881, 25882, 25883, 25884, 25885, 25886, 25887, 25888, 25892, 25901, 25902, 25922, 25927, 25928, 25929, 25930, 25931, 25932, 25933, 25942,
			25943, 25944, 25945, 25946, 25947, 25948, 25949, 25950, 25956, 25957, 25958, 25959, 25960, 25967, 25968, 25969, 25970, 25971, 25972, 25978, 25979, 25980, 25981, 25982, 25983, 25989,
			25990, 25991, 25992, 25993, 25994, 29062, 29118, 29150, 29197, 29218, 29251, 26000, 26001, 26002, 26003, 26004, 26005, 26011, 26012, 26013, 26014, 26015, 26016, 26022,
			26023, 26024, 26025, 26026, 26027, 26033, 26034, 26035, 26036, 26037, 26038, 26044, 26045, 26046, 26047, 26048, 26049, 26055, 26056, 26057, 26058, 26059, 26060,
			26066, 26067, 26068, 26069, 26070, 26071, 26077, 26078, 26079, 26080, 26081, 26082};

	// Item's
	private static final int CERTIF4 = 45636;
	private static final int BOOK = 45870;

	public static final String A_LIST = "A_LIST";

	public _10827_StepUpToLead()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(GUSTAV);
		addTalkId(GUSTAV);
		addKillId(RAIDBOSS);
		addLevelCheck("sir_gustaf_athebaldt_q10827_02.htm", 99);
		addNobleCheck("sir_gustaf_athebaldt_q10827_02.htm", true);
		addItemHaveCheck("sir_gustaf_athebaldt_q10827_03.htm", 45637, 1);
		addKillNpcWithLog(1, 582711, A_LIST, 30, RAIDBOSS);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("sir_gustaf_athebaldt_q10827_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("sir_gustaf_athebaldt_q10827_09.htm"))
		{
			st.giveItems(CERTIF4, 1, false);
			st.giveItems(BOOK, 1, false);
			if (checkReward(st))
				htmltext = "sir_gustaf_athebaldt_q10827_10.htm";
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
			case GUSTAV:
				if (cond == 0)
					htmltext = "sir_gustaf_athebaldt_q10827_01.htm";
				else if (cond == 1 && !checkReward(st))
					htmltext = "sir_gustaf_athebaldt_q10827_07.htm";
				else if (cond == 2)
					htmltext = "sir_gustaf_athebaldt_q10827_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1 && st.getPlayer().isInParty() && st.getPlayer().getParty().isLeader(st.getPlayer()))
		{
			if(updateKill(npc, st))
			{
				st.unset(A_LIST);
				st.setCond(2);
			}
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