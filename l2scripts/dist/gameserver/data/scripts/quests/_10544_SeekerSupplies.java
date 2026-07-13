package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10544_SeekerSupplies extends Quest
{
	private static final int WILF = 30005;
	private static final int KATERINA = 30004;
	private static final int LEKTOR = 30001;
	private static final int JEKSON = 30002;
	private static final int TREVOR = 32166;

	private static final int PRANA = 32153;
	private static final int RIVIAN = 32147;
    private static final int DEVON = 32160;
	private static final int TUK = 32150;
	private static final int MOKA = 32157;
	private static final int VALPOR = 32146;

	private static final int QUEST_ITEM1 = 47604;
	private static final int QUEST_ITEM2 = 47603;
	private static final int QUEST_ITEM3 = 47602;
	private static final int QUEST_ITEM4 = 47605;


	private static final int REWARD1 = 112;
	private static final int REWARD2 = 906;

	private static final int EXP_REWARD = 2630;
	private static final int SP_REWARD = 10;

	public _10544_SeekerSupplies()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(WILF);
		addTalkId(WILF);
		addTalkId(KATERINA);
		addTalkId(LEKTOR);
		addTalkId(JEKSON);
		addTalkId(TREVOR);
		addTalkId(PRANA);
		addTalkId(RIVIAN);
		addTalkId(DEVON);
		addTalkId(TUK);
		addTalkId(MOKA);
		addTalkId(VALPOR);

		addRaceCheck("wilph_q10544_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("wilph_q10544_02.htm", 1/*, 20*/);
		addQuestCompletedCheck("wilph_q10544_02.htm", 10543);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("wilph_q10544_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("katrine_q10544_02.htm"))
		{
			st.setCond(2);
			st.giveItems(QUEST_ITEM1, 1, false);
		}
		else if (event.equalsIgnoreCase("lector_q10544_02.htm"))
		{
			st.setCond(3);
			st.giveItems(QUEST_ITEM2, 1, false);
			st.takeItems(QUEST_ITEM1, -1);
		}
		else if (event.equalsIgnoreCase("lector_q10544_02.htm"))
		{
			st.setCond(3);
			st.giveItems(QUEST_ITEM2, 1, false);
			st.takeItems(QUEST_ITEM1, -1);
		}
		else if (event.equalsIgnoreCase("jackson_q10544_02.htm"))
		{
			st.setCond(4);
			st.giveItems(QUEST_ITEM3, 1, false);
			st.takeItems(QUEST_ITEM2, -1);
		}
		else if (event.equalsIgnoreCase("trader_treauvi_q10544_02.htm"))
		{
			st.giveItems(QUEST_ITEM4, 1, false);
			st.takeItems(QUEST_ITEM3, -1);
			if(st.getPlayer().getRace() == Race.HUMAN)
			{
				htmltext = "trader_treauvi_q10544_02.htm";
				st.setCond(5);
			}
			else if(st.getPlayer().getRace() == Race.ELF)
			{
				htmltext = "trader_treauvi_q10544_03.htm";
				st.setCond(6);
			}
			else if(st.getPlayer().getRace() == Race.DARKELF)
			{
				htmltext = "trader_treauvi_q10544_04.htm";
				st.setCond(7);
			}
			else if(st.getPlayer().getRace() == Race.ORC)
			{
				htmltext = "trader_treauvi_q10544_05.htm";
				st.setCond(8);
			}
			else if(st.getPlayer().getRace() == Race.DWARF)
			{
				htmltext = "trader_treauvi_q10544_06.htm";
				st.setCond(9);
			}
			else if(st.getPlayer().getRace() == Race.KAMAEL)
			{
				htmltext = "trader_treauvi_q10544_07.htm";
				st.setCond(10);
			}
		}
		else if (event.equalsIgnoreCase("highpriest_prana_q10544_03.htm") || event.equalsIgnoreCase("grandmaster_rivian_q10544_03.htm") ||
					event.equalsIgnoreCase("grandmagister_devon_q10544_03.htm") || event.equalsIgnoreCase("high_prefect_toonks_q10544_03.htm") ||
						event.equalsIgnoreCase("head_blacksmith_mokabred_q10544_03.htm") || event.equalsIgnoreCase("grandmaster_valpar_q10544_03.htm"))
		{
			st.takeItems(QUEST_ITEM4, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(REWARD1, 2, false);
			st.giveItems(REWARD2, 1, false);
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
			case WILF:
				if (cond == 0)
					htmltext = "wilph_q10544_01.htm";
				else if (cond == 1)
					htmltext = "wilph_q10544_06.htm";
				break;

			case KATERINA:
				if (cond == 1)
					htmltext = "katrine_q10544_01.htm";
				else if (cond == 2)
					htmltext = "katrine_q10544_03.htm";
				break;
			case LEKTOR:
				if (cond == 2)
					htmltext = "lector_q10544_01.htm";
				else if (cond == 3)
					htmltext = "lector_q10544_03.htm";
				break;
			case JEKSON:
				if (cond == 3)
					htmltext = "jackson_q10544_01.htm";
				else if (cond == 4)
					htmltext = "jackson_q10544_03.htm";
				break;
			case TREVOR:
				if (cond == 4)
					htmltext = "trader_treauvi_q10544_01.htm";
				else if (cond >= 5 && cond <= 10)
					htmltext = "trader_treauvi_q10544_08.htm";
				break;

			case PRANA:
				if (cond == 5)
					htmltext = "highpriest_prana_q10544_01.htm";
				break;
			case RIVIAN:
				if (cond == 6)
					htmltext = "grandmaster_rivian_q10544_01.htm";
				break;
			case DEVON:
				if (cond == 7)
					htmltext = "grandmagister_devon_q10544_01.htm";
				break;
			case TUK:
				if (cond == 8)
					htmltext = "high_prefect_toonks_q10544_01.htm";
				break;
			case MOKA:
				if (cond == 9)
					htmltext = "head_blacksmith_mokabred_q10544_01.htm";
				break;
			case VALPOR:
				if (cond == 10)
					htmltext = "grandmaster_valpar_q10544_01.htm";
				break;
		}
		return htmltext;
	}
}
