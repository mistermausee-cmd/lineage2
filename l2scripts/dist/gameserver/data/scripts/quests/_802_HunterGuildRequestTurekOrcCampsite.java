package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _802_HunterGuildRequestTurekOrcCampsite extends Quest
{
	// NPC's
	private static final int ARKTUR = 34267;
	
	private static final int[] MOBS = {26000, 26001, 26002, 26003, 26004, 26005};

	private static final long EXP_REWARD = 926489070;
	private static final int SP_REWARD = 1235250;

	public _802_HunterGuildRequestTurekOrcCampsite()
	{
		super(PARTY_ALL, DAILY);
		addTalkId(ARKTUR);
		addKillId(MOBS);
		addLevelCheck(NO_QUEST_DIALOG, 87, 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hunter_leader_arcturus_q0802_03.htm"))
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
					htmltext = "hunter_leader_arcturus_q0802_02.htm";
				else if (cond == 2)
					htmltext = "hunter_leader_arcturus_q0802_01.htm";
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