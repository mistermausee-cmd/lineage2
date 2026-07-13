package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10858_QueenRamonControllerOfTheVessel extends Quest
{
	// NPC's
	private static final int KEKROPUS = 34222;

	//Монстры
	private static final int MOBS = 26143;

	private static final long EXP_REWARD = 5925714120l;
	private static final int SP_REWARD = 14221620;

	public _10858_QueenRamonControllerOfTheVessel()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS);
		addKillId(MOBS);
		addLevelCheck("leader_kekrops_q10858_02.htm", 102);
		addQuestCompletedCheck("leader_kekrops_q10858_02.htm", 10857);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("leader_kekrops_q10858_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("leader_kekrops_q10858_08.htm"))
		{
			st.giveItems(46150, 1);
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
					htmltext = "leader_kekrops_q10858_01.htm";
				else if (cond == 1)
					htmltext = "leader_kekrops_q10858_06.htm";
				else if (cond == 2)
					htmltext = "leader_kekrops_q10858_07.htm";
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