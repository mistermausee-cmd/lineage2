package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class _782_UtilizetheDarknessSeedofHellfire extends Quest
{
	// NPC's
	private static final int SIZRAK = 33669;

	// Monster's
	private static final int[] MONSTERS = {19262, 19263, 19264, 19265, 19266, 19270, 19287, 23213, 23214, 23215, 23216
			, 23217, 23218, 23219, 23220, 23233, 23234, 23235, 23236, 23237
	};

	// Item's
	private static final int BR_PETRA = 34976;
	private static final int PETRA = 35656;

	public _782_UtilizetheDarknessSeedofHellfire()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(SIZRAK);
		addKillId(MONSTERS);
		addLevelCheck("sofa_sizraku_q0782_02.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_sizraku_q0782_04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("sofa_sizraku_q0782_08.htm"))
		{
			int reward = (int)st.getQuestItemsCount(BR_PETRA) / 5;
			st.giveItems(PETRA, reward);
			st.takeItems(BR_PETRA, -1);
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
			case SIZRAK:
				if(cond == 0)
					htmltext = "sofa_sizraku_q0782_01.htm";
				else if(cond == 1 && st.getQuestItemsCount(BR_PETRA) < 50)
					htmltext = "sofa_sizraku_q0782_05.htm";
				else if(cond == 1 || cond == 2)
					htmltext = "sofa_sizraku_q0782_06.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == SIZRAK)
			htmltext = "sofa_sizraku_q0782_10.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(BR_PETRA, 1, 1, 500, 100))
				st.setCond(2);
		}
		return null;
	}
}