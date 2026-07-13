package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _806_HunterGuildRequestNorthOfTheTownOfGiran extends Quest
{
	// NPC's
	private static final int ARKTUR = 34267;

	private static final int[] MOBS = {25967, 25968, 25969, 25970, 25971, 25972};

	private static final long EXP_REWARD = 3015185490l;
	private static final int SP_REWARD = 7236360;

	public _806_HunterGuildRequestNorthOfTheTownOfGiran()
	{
		super(PARTY_ALL, DAILY);
		addTalkId(ARKTUR);
		addKillId(MOBS);
		addLevelCheck(NO_QUEST_DIALOG, 82, 102);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hunter_leader_arcturus_q0806_03.htm"))
		{
			st.giveItems(47564, 1);
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
			case ARKTUR:
				if (cond == 1)
					htmltext = "hunter_leader_arcturus_q0806_02.htm";
				else if (cond == 2)
					htmltext = "hunter_leader_arcturus_q0806_01.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.setCond(2);
		}
		return null;
	}
}