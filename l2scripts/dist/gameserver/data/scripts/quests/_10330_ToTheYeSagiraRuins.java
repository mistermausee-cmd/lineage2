package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10330_ToTheYeSagiraRuins extends Quest
{
	private static final int MILIA = 30006;
	private static final int RAKSIS = 32977;
	private static final int PRANA = 32153;
	private static final int RIVIAN = 32147;
	private static final int DEVON = 32160;
	private static final int TUK = 32150;
	private static final int MOKA = 32157;
	private static final int VALPOR = 32146;

	private static final int EXP_REWARD = 20100;
	private static final int SP_REWARD = 11;

	public _10330_ToTheYeSagiraRuins()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(PRANA, RIVIAN, DEVON, TUK, MOKA, VALPOR);
		addTalkId(PRANA, RIVIAN, DEVON, TUK, MOKA, VALPOR, MILIA, RAKSIS);
		addLevelCheck(NO_QUEST_DIALOG, 7, 20);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("highpriest_prana_q10330_05.htm") || event.equalsIgnoreCase("grandmaster_rivian_q10330_05.htm") ||
				event.equalsIgnoreCase("grandmagister_devon_q10330_05.htm") || event.equalsIgnoreCase("high_prefect_toonks_q10330_05.htm")
				|| event.equalsIgnoreCase("head_blacksmith_mokabred_q10330_05.htm")|| event.equalsIgnoreCase("grandmaster_valpar_q10330_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("rapunzel_q10330_03.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("si_illusion_larcis_q10330_03.htm"))
		{
			st.giveItems(875, 2, false);
			st.giveItems(1060, 100);
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
			case PRANA:
				if (cond == 0)
				{
					if(st.getPlayer().getRace() == Race.HUMAN)
						htmltext = "highpriest_prana_q10330_01.htm";
					else
						htmltext = "highpriest_prana_q10330_02a.htm";
				}
				else if (cond == 1)
					htmltext = "highpriest_prana_q10330_06.htm";
				break;
			case RIVIAN:
				if (cond == 0)
				{
					if(st.getPlayer().getRace() == Race.ELF)
						htmltext = "grandmaster_rivian_q10330_01.htm";
					else
						htmltext = "grandmaster_rivian_q10330_02a.htm";
				}
				else if (cond == 1)
					htmltext = "grandmaster_rivian_q10330_06.htm";
				break;
			case DEVON:
				if (cond == 0)
				{
					if(st.getPlayer().getRace() == Race.DARKELF)
						htmltext = "grandmagister_devon_q10330_01.htm";
					else
						htmltext = "grandmagister_devon_q10330_02a.htm";
				}
				else if (cond == 1)
					htmltext = "grandmagister_devon_q10330_06.htm";
				break;
			case TUK:
				if (cond == 0)
				{
					if(st.getPlayer().getRace() == Race.ORC)
						htmltext = "high_prefect_toonks_q10330_01.htm";
					else
						htmltext = "high_prefect_toonks_q10330_02a.htm";
				}
				else if (cond == 1)
					htmltext = "high_prefect_toonks_q10330_06.htm";
				break;
			case MOKA:
				if (cond == 0)
				{
					if(st.getPlayer().getRace() == Race.DWARF)
						htmltext = "head_blacksmith_mokabred_q10330_01.htm";
					else
						htmltext = "head_blacksmith_mokabred_q10330_02a.htm";
				}
				else if (cond == 1)
					htmltext = "head_blacksmith_mokabred_q10330_06.htm";
				break;
			case VALPOR:
				if (cond == 0)
				{
					if(st.getPlayer().getRace() == Race.KAMAEL)
						htmltext = "grandmaster_valpar_q10330_01.htm";
					else
						htmltext = "grandmaster_valpar_q10330_02a.htm";
				}
				else if (cond == 1)
					htmltext = "grandmaster_valpar_q10330_06.htm";
				break;
			case MILIA:
				if (cond == 1)
					htmltext = "rapunzel_q10330_01.htm";
				break;
			case RAKSIS:
				if (cond != 2)
					htmltext = "si_illusion_larcis_q10330_01.htm";
				else if (cond == 2)
					htmltext = "si_illusion_larcis_q10330_02.htm";
				break;
		}
		return htmltext;
	}
}