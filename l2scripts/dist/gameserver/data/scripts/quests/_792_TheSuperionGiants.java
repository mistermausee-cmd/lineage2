package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _792_TheSuperionGiants extends Quest
{
	// NPC's
	private static final int HISTI = 34243;

	private static final int GIANTPART = 47192;

	//Монстры
	private static final int[] MOBS = {23774, 23775, 23776, 23777, 23778, 23779, 23780, 23781, 23782, 23783};

	private static final long EXP_REWARD = 8888571180l;
	private static final int SP_REWARD = 21332430;

	public _792_TheSuperionGiants()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(HISTI);
		addTalkId(HISTI);
		addKillId(MOBS);
		addQuestItem(GIANTPART);
		addLevelCheck("gaintchaser_hysty_q0792_02.htm", 102);
		addQuestCompletedCheck("gaintchaser_hysty_q0792_02.htm", 10856);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gaintchaser_hysty_q0792_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("gaintchaser_hysty_q0792_08.htm"))
		{
			st.takeItems(GIANTPART, -1);
			st.giveItems(47213, 1);
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
			case HISTI:
				if(cond == 0)
					htmltext = "gaintchaser_hysty_q0792_01.htm";
				else if (cond == 1)
					htmltext = "gaintchaser_hysty_q0792_06.htm";
				else if (cond == 2)
					htmltext = "gaintchaser_hysty_q0792_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if(qs.rollAndGive(GIANTPART, 1, 1, 100, 40))
				qs.setCond(2);
		}
		return null;
	}
}