package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _800_HunterGuildRequestAltarOfEvil extends Quest
{
	// NPC's
	private static final int ARKTUR = 34267;

	//Монстры
	private static final int[] MOBS = {26011, 26012, 26013, 26014, 26015, 26016};

	private static final long EXP_REWARD = 481069620;
	private static final int SP_REWARD = 577260;

	public _800_HunterGuildRequestAltarOfEvil()
	{
		super(PARTY_ALL, DAILY);
		addTalkId(ARKTUR);
		addKillId(MOBS);
		addLevelCheck("leader_kekrops_q10857_02.htm", 85, 93);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hunter_leader_arcturus_q0800_03.htm"))
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
					htmltext = "hunter_leader_arcturus_q0800_02.htm";
				else if (cond == 2)
					htmltext = "hunter_leader_arcturus_q0800_01.htm";
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