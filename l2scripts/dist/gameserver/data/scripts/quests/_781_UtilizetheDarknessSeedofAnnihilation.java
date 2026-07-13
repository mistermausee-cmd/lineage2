package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class _781_UtilizetheDarknessSeedofAnnihilation extends Quest
{
	// NPC's
	private static final int KLEMIS = 32734;

	// Monster's
	private static final int[] MONSTERS = {22746, 22750, 22747, 22751, 22748, 22752, 22749, 22753, 22755, 22758, 22756, 22759, 22754, 22757,
			22762, 22765, 22760, 22763, 22761, 22764, 23033, 23034, 23035, 23036, 23037
	};

	// Item's
	private static final int STONDUST = 15536;
	private static final int STONSOUL = 15486;

	public _781_UtilizetheDarknessSeedofAnnihilation()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(KLEMIS);
		addKillId(MONSTERS);
		addLevelCheck("clemis_q0781_02.htm", 85);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("clemis_q0781_04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("clemis_q0781_08.htm"))
		{
			int reward = (int)st.getQuestItemsCount(STONDUST) / 5;
			st.giveItems(STONSOUL, reward);
			st.takeItems(STONDUST, -1);
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
			case KLEMIS:
				if(cond == 0)
					htmltext = "clemis_q0781_01.htm";
				else if(cond == 1 && st.getQuestItemsCount(STONDUST) < 50)
					htmltext = "clemis_q0781_05.htm";
				else if(cond == 1 || cond == 2)
					htmltext = "clemis_q0781_06.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == KLEMIS)
			htmltext = "clemis_q0781_10.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(STONDUST, 1, 1, 500, 100))
				st.setCond(2);
		}
		return null;
	}
}