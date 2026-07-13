package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10856_SuperionAppears extends Quest
{
	// NPC's
	private static final int KEKROPUS = 34222;
	private static final int MELDINA = 32214;
	private static final int HISTI = 34243;

	private static final int EXP_REWARD = 592571412;
	private static final int SP_REWARD = 1422162;

	public _10856_SuperionAppears()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS, MELDINA, HISTI);
		addLevelCheck("leader_kekrops_q10856_02.htm", 102);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("leader_kekrops_q10856_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("grandmaster_meldina_q10856_03.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("leader_kekrops_q10856_08.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("teleport"))
		{
			st.getPlayer().teleToLocation(79895, 152614, 2304, st.getPlayer().getReflection());
			return null;
		}
		else if(event.equalsIgnoreCase("gaintchaser_hysty_q10856_03.htm"))
		{
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
					htmltext = "leader_kekrops_q10856_01.htm";
				else if (cond == 1)
					htmltext = "leader_kekrops_q10856_06.htm";
				else if (cond == 2)
					htmltext = "leader_kekrops_q10856_07.htm";
				else if (cond == 3)
					htmltext = "leader_kekrops_q10856_10.htm";
				break;

			case MELDINA:
				if (cond == 1)
					htmltext = "grandmaster_meldina_q10856_01.htm";
				else if (cond == 2)
					htmltext = "grandmaster_meldina_q10856_04.htm";
				break;

			case HISTI:
				if (cond == 3)
					htmltext = "gaintchaser_hysty_q10856_02.htm";
				break;

		}
		return htmltext;
	}
}
