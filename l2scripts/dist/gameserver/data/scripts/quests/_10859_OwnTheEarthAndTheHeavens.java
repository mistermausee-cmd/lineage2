package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10859_OwnTheEarthAndTheHeavens extends Quest
{
	// NPC's
	private static final int KEKROPUS = 34222;

	//Монстры
	private static final int MOBS = 29305;

	private static final long EXP_REWARD = 26665713540l;
	private static final int SP_REWARD = 63997290;

	public _10859_OwnTheEarthAndTheHeavens()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS);
		addKillId(MOBS);
		addLevelCheck("leader_kekrops_q10859_02.htm", 102);
		addQuestCompletedCheck("leader_kekrops_q10859_02.htm", 10858);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("leader_kekrops_q10859_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("leader_kekrops_q10859_08.htm"))
		{
			st.giveItems(37801, 1);
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
			case KEKROPUS:
				if(cond == 0)
					htmltext = "leader_kekrops_q10859_01.htm";
				else if (cond == 1)
					htmltext = "leader_kekrops_q10859_06.htm";
				else if (cond == 2)
					htmltext = "leader_kekrops_q10859_07.htm";
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