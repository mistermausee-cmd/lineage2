package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

import static l2s.gameserver.model.base.FactionType.GIANT_CHASER;

//By Evil_dnk

public class _10857_SecretTeleport extends Quest
{
	// NPC's
	private static final int HISTI = 34243;
	private static final int KEKROPUS = 34222;

	private static final int MAPSUP = 47191;

	//Монстры
	private static final int[] MOBS = {23774, 23775, 23776, 23777, 23778, 23779, 23780, 23781, 23782, 23783};

	private static final long EXP_REWARD = 17777142360l;
	private static final int SP_REWARD = 42664860;

	public _10857_SecretTeleport()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS, HISTI);
		addQuestItem(MAPSUP);
		addKillId(MOBS);
		addLevelCheck("leader_kekrops_q10857_02.htm", 102);
		addQuestCompletedCheck("leader_kekrops_q10857_02.htm", 10856);
		addFactionLevelCheck("leader_kekrops_q10857_02a.htm", GIANT_CHASER, 3);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("leader_kekrops_q10857_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("gaintchaser_hysty_q10857_04.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("gaintchaser_hysty_q10857_07.htm"))
		{
			st.giveItems(35563, 1);
			st.takeItems(MAPSUP, -1);
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
					htmltext = "leader_kekrops_q10857_01.htm";
				else if (cond == 1)
					htmltext = "leader_kekrops_q10857_06.htm";
				break;

			case HISTI:
				if (cond == 1)
					htmltext = "gaintchaser_hysty_q10857_01.htm";
				else if (cond == 2)
					htmltext = "gaintchaser_hysty_q10857_05.htm";
				else if (cond == 3)
					htmltext = "gaintchaser_hysty_q10857_06.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(MAPSUP, 1, 1, 20, 25))
				qs.setCond(3);
		}
		return null;
	}
}