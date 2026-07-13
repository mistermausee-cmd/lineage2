package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10818_ConfrontingAGiantMonster extends Quest
{
	// NPC's
	private static final int DAICHIRI = 30537;

	// Monster's
	private static final int[] TRASKEN = { 29197, 19159 };
	private static final int[] MICHAEL = {25799, 25800, 26114, 26115, 26116, 26120, 26121, 26122};
	private static final int[] KECHI = {25797, 26111, 26112, 26113};
	private static final int[] EMAMBIFI = {25796, 25881, 26105, 26106, 26107, 26108, 26109, 26110};
	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";
	public static final String C_LIST = "C_LIST";
	public static final String D_LIST = "D_LIST";
	private static final int ISTHINA = 29196;
	private static final int OCTAVIS = 29212;
	private static final int TAUTI = 29237;
	private static final int BELETH = 29250;

	// Item's
	private static final int WORMSOUL = 46055;
	private static final int STONEKCHI = 46053;
	private static final int STONEMICHAEL = 46054;
	private static final int STONEEMABI = 46052;
	private static final int CERTIF1 = 45628;


	private static final long EXP_REWARD = 193815839115l;
	private static final int SP_REWARD = 0; 

	public _10818_ConfrontingAGiantMonster()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(DAICHIRI);
		addTalkId(DAICHIRI);
		addKillId(TRASKEN);
		addKillId(MICHAEL);
		addKillId(KECHI);
		addKillId(EMAMBIFI);
		addKillId(ISTHINA);
		addKillId(OCTAVIS);
		addKillId(TAUTI);
		addKillId(BELETH);
		addQuestItem(WORMSOUL);
		addQuestItem(STONEKCHI);
		addQuestItem(STONEMICHAEL);
		addQuestItem(STONEEMABI);
		addLevelCheck("daichir_priest_of_earth_q10818_02.htm", 99);
		addNobleCheck("daichir_priest_of_earth_q10818_02.htm", true);
		addItemHaveCheck("daichir_priest_of_earth_q10818_03.htm", 45632, 1);
		addKillNpcWithLog(1, 1029196, A_LIST, 1, ISTHINA);
		addKillNpcWithLog(1, 1029212, B_LIST, 1, OCTAVIS);
		addKillNpcWithLog(1, 1029237, C_LIST, 1, TAUTI);
		addKillNpcWithLog(1, 1029250, D_LIST, 1, BELETH);
		addQuestItemWithLog(1, 0, 1, 46055);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("daichir_priest_of_earth_q10818_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("daichir_priest_of_earth_q10818_09.htm"))
		{
			st.takeItems(WORMSOUL, -1);
			st.takeItems(STONEKCHI, -1);
			st.takeItems(STONEMICHAEL, -1);
			st.takeItems(STONEEMABI, -1);
			st.giveItems(CERTIF1, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			if (checkReward(st))
				htmltext = "daichir_priest_of_earth_q10818_10.htm";
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
			case DAICHIRI:
				if (cond == 0)
					htmltext = "daichir_priest_of_earth_q10818_01.htm";
				else if (cond == 1)
					htmltext = "daichir_priest_of_earth_q10818_07.htm";
				else if (cond == 2)
					htmltext = "daichir_priest_of_earth_q10818_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			if(ArrayUtils.contains(TRASKEN, npcId) && !st.haveQuestItem(WORMSOUL))
				st.giveItems(WORMSOUL, 1, false);
			else if(ArrayUtils.contains(MICHAEL, npcId) && !st.haveQuestItem(STONEMICHAEL))
				st.giveItems(STONEMICHAEL, 1, false);
			else if(ArrayUtils.contains(KECHI, npcId) && !st.haveQuestItem(STONEKCHI))
				st.giveItems(STONEKCHI, 1, false);
			else if(ArrayUtils.contains(EMAMBIFI, npcId) && !st.haveQuestItem(STONEEMABI))
				st.giveItems(STONEEMABI, 1, false);

			if(st.haveQuestItem(STONEMICHAEL) && st.haveQuestItem(STONEKCHI) && st.haveQuestItem(STONEEMABI))
			{
				st.giveItems(WORMSOUL, 1, false);
				st.takeItems(STONEMICHAEL, -1);
				st.takeItems(STONEEMABI, -1);
				st.takeItems(STONEKCHI, -1);
			}
			if(updateKill(npc, st) && st.haveQuestItem(WORMSOUL))
			{
				st.unset(A_LIST);
				st.unset(B_LIST);
				st.unset(C_LIST);
				st.unset(D_LIST);
				st.setCond(2);
			}

		}
		return null;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getLevel() > 99 && st.haveQuestItem(45628) && st.haveQuestItem(45629) && st.haveQuestItem(45630) && st.haveQuestItem(45631))
		{
			st.getPlayer().getQuestState(10817).setCond(2);
			return true;
		}

		return false;
	}
}