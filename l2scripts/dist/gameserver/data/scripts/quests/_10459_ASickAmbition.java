package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10459_ASickAmbition extends Quest
{
	// NPC's
	private static final int LEONA = 31595;
	private static final int LEONAEND = 33899;
	//Mobs
	private static final int[] MONSTERS = {29250, 29246};

	private static final long EXP_REWARD = 555716700L;
	private static final int SP_REWARD = 2133952;
	public _10459_ASickAmbition()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(LEONA);
		addTalkId(LEONAEND);
		addTalkId(LEONA);
		addKillId(MONSTERS);
		addQuestCompletedCheck("lionna_blackbird_q10459_02.htm", 10455);
		addLevelCheck("lionna_blackbird_q10459_02.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("lionna_blackbird_q10459_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q10459_09.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(37903, 1);
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
			case LEONA:
				if(cond == 0)
					htmltext = "lionna_blackbird_q10459_01.htm";
				else if (cond == 1)
					htmltext = "lionna_blackbird_q10459_06.htm";
				break;
			case LEONAEND:
				if (cond == 2)
					htmltext = "lionna_blackbird_q10459_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
				st.setCond(2);
		return null;
	}
}