package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD 

public class _10844_BloodyBattleSeizingSupplies extends Quest
{

	private static final int ELRIKA = 34057;
	private static final int GLEN = 34063;

	private static final int SUPPL = 34137;

	private static final int E_SUPPLY = 46282;

	private static final long EXP_REWARD = 7262301690l;
	private static final int SP_REWARD = 17429400;
	private static final int FP_REWARD = 100;      //TODO FIND

	public _10844_BloodyBattleSeizingSupplies()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ELRIKA);
		addTalkId(ELRIKA, GLEN, SUPPL);
		addLevelCheck("ellikia_vanguard_q10844_02.htm", 101);
		addFactionLevelCheck("ellikia_vanguard_q10844_02.htm", FactionType.KINGDOM_ROYALGUARD, 2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (event.equalsIgnoreCase("ellikia_vanguard_q10844_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("blackbird_glenkinchie_q10844_03.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("blackbird_glenkinchie_q10844_06.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case ELRIKA:
				if (cond == 0)
					htmltext = "ellikia_vanguard_q10844_01.htm";
				else if (cond == 1)
					htmltext = "ellikia_vanguard_q10844_07.htm";
				break;

			case GLEN:
				if (cond == 1)
					htmltext = "blackbird_glenkinchie_q10844_01.htm";
				else if (cond == 2)
					htmltext = "blackbird_glenkinchie_q10844_04.htm";
				else if (cond == 3)
					htmltext = "blackbird_glenkinchie_q10844_05.htm";
				break;

			case SUPPL:
				if (cond == 2)
				{
					st.giveItems(E_SUPPLY, 1, false);
					npc.deleteMe();
					if(st.getQuestItemsCount(E_SUPPLY) >= 20)
						st.setCond(3);
					return null;
				}
				break;
		}
		return htmltext;
	}
}
