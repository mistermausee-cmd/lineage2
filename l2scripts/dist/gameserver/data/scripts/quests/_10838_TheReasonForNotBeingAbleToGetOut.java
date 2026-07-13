package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10838_TheReasonForNotBeingAbleToGetOut extends Quest
{
	// NPC's
	private static final int NPC1 = 34064;

	// Monster's
	private static final int[] MONSTERS_A = {23506, 23505, 23507, 23508};

	//Reward EXP SP
	private static final long EXP_REWARD = 9683068920l;
	private static final long SP_REWARD = 23239200;

	// Item's
	private static final int DROPITEM = 46133;
	private static final int REWARD1 = 46135;

	// Quest item chance drop
	private static final int CHANCE = 15;

	public static final String A_LIST = "A_LIST";

	public _10838_TheReasonForNotBeingAbleToGetOut()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addQuestItem(DROPITEM);
		addKillId(MONSTERS_A);
		addLevelCheck("blackbird_hurak_q10838_02.htm", 101);
		addItemHaveCheck("blackbird_hurak_q10838_03.htm", 46132, 1);
		addKillNpcWithLog(1, 583811, A_LIST, 150, MONSTERS_A);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("blackbird_hurak_q10838_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("blackbird_hurak_q10838_09.htm"))
		{
			st.giveItems(REWARD1, 1, false);
			st.takeItems(DROPITEM, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			checkReward(st);
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
					htmltext = "blackbird_hurak_q10838_01.htm";
				else if (cond == 1)
					htmltext = "blackbird_hurak_q10838_07.htm";
				else if (cond == 2)
					htmltext = "blackbird_hurak_q10838_08.htm";
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
			st.rollAndGive(DROPITEM, 1, 1, 10, CHANCE);

			boolean doneKill = updateKill(npc, st);

			if(doneKill)
			{
				if(st.getQuestItemsCount(DROPITEM) >= 10)
				{
					st.unset(A_LIST);
					st.setCond(2);
				}
			}
		}
		return null;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.haveQuestItem(46134) && st.haveQuestItem(46135) && st.haveQuestItem(46136) && st.haveQuestItem(46137))
		{
			st.getPlayer().getQuestState(10836).setCond(2);
			return true;
		}

		return false;
	}
}