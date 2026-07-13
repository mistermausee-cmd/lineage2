package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10832_EnergyOfSadnessAndAnger extends Quest
{
	// NPC's
	private static final int BELAS = 34056;

	// Monster's
	private static final int[] MONSTERS = {23561};

	// Item's
	private static final int ENERGYOFSA = 45837;
	private static final int ENERGYOFA = 45838;
	private static final int REWARD = 46158;

	// Quest item chance drop
	private static final int CHANCE = 20;

	private static final long EXP_REWARD = 7909920000l;
	private static final int SP_REWARD = 18983760; 

	public _10832_EnergyOfSadnessAndAnger()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BELAS);
		addTalkId(BELAS);
		addKillId(MONSTERS);
		addQuestItem(ENERGYOFSA);
		addQuestItem(ENERGYOFA);
		addLevelCheck("belas_q10832_02.htm", 100);
		addQuestCompletedCheck("belas_q10832_02.htm", 10831);
		addItemHaveCheck("belas_q10832_03.htm", 45843, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("belas_q10832_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("belas_q10832_09.htm"))
		{
			st.takeItems(ENERGYOFA, -1);
			st.takeItems(ENERGYOFSA, -1);
			st.giveItems(REWARD, 1);
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
			case BELAS:
				if (cond == 0)
					htmltext = "belas_q10832_01.htm";
				else if (cond == 1)
					htmltext = "belas_q10832_07.htm";
				else if (cond == 2)
					htmltext = "belas_q10832_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			if(npc.getNpcId() == 23561)
			{
				st.rollAndGive(ENERGYOFSA, 1, 1, 1, CHANCE);
				st.rollAndGive(ENERGYOFA, 1, 1, 1, CHANCE);
			}			
			if(st.haveQuestItem(ENERGYOFA) && st.haveQuestItem(ENERGYOFSA))
				st.setCond(2);
		}
		return null;
	}

}