package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10830_TheLostGardenOfSpirits extends Quest
{
	// NPC's
	private static final int GIFONA = 34055;

	// Monster's
	private static final int[] MONSTERS = {23541, 23542, 23543, 23544, 23546, 23547, 23548, 23548, 23549, 23550, 23551, 23552, 23553, 23555, 23556, 23557, 23558};

	// Item's
	private static final int POWERCURSSP = 45821;
	private static final int REWARD = 46158;

	// Quest item chance drop
	private static final int CHANCE = 35;

	private static final long EXP_REWARD = 5932440000l;
	private static final int SP_REWARD = 14237820; 

	public _10830_TheLostGardenOfSpirits()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(GIFONA);
		addTalkId(GIFONA);
		addKillId(MONSTERS);
		addQuestItem(POWERCURSSP);
		addLevelCheck("cyphona_q10830_02.htm", 100);
		addQuestCompletedCheck("cyphona_q10830_02.htm", 10829);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("cyphona_q10830_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("cyphona_q10830_08.htm"))
		{
			st.takeItems(POWERCURSSP, -1);
			st.giveItems(REWARD, 1);
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
			case GIFONA:
				if (cond == 0)
					htmltext = "cyphona_q10830_01.htm";
				else if (cond == 1)
					htmltext = "cyphona_q10830_06.htm";
				else if (cond == 2)
					htmltext = "cyphona_q10830_07.htm";
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
			if (st.rollAndGive(POWERCURSSP, 1, 1, 100, CHANCE))
				st.setCond(2);
		}
		return null;
	}

}