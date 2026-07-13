package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10839_BlackbirdsNameValue extends Quest
{
	// NPC's
	private static final int NPC1 = 34065;

	// Monster's
	private static final int[] MONSTERS_A = {23507, 23508, 23512, 23509};

	//Reward EXP SP
	private static final long EXP_REWARD = 12103836150l;
	private static final long SP_REWARD = 29049000;

	// Item's
	private static final int REWARD1 = 46136;

	public static final String A_LIST = "A_LIST";

	public _10839_BlackbirdsNameValue()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addLevelCheck("blackbird_laffian_q10839_02.htm", 101);
		addItemHaveCheck("blackbird_laffian_q10839_03.htm", 46132, 1);
		addKillNpcWithLog(1, 583911, A_LIST, 200, MONSTERS_A);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("blackbird_laffian_q10839_07.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("blackbird_laffian_q10839_10.htm"))
		{
			st.giveItems(REWARD1, 1, false);
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
					htmltext = "blackbird_laffian_q10839_01.htm";
				else if (cond == 1)
					htmltext = "blackbird_laffian_q10839_08.htm";
				else if (cond == 2)
					htmltext = "blackbird_laffian_q10839_09.htm";
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
			boolean doneKill = updateKill(npc, st);

			if(doneKill)
			{
				st.unset(A_LIST);
				st.setCond(2);
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