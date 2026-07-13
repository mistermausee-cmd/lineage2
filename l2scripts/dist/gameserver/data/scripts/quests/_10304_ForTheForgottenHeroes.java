package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10304_ForTheForgottenHeroes extends Quest
{
	//npc
	private static final int ISHAEL = 32894;
	
	//mobs
	private static final int YUI = 25837;
	private static final int KINEN = 25840;
	private static final int KONJAN = 25845;
	private static final int RASINDA = 25841;
	
	private static final int MAKYSHA = 25838;
	private static final int HORNAPI = 25839;
	
	private static final int YONTYMAK = 25846;
	private static final int FRON = 25825;

	private static final int fortunaId = 179;

	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";

	private static final int EXP_REWARD = 15197798;	private static final int SP_REWARD = 3647; 	public _10304_ForTheForgottenHeroes()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(ISHAEL);
		addTalkId(ISHAEL);
		addKillId(YUI, KINEN, KONJAN, RASINDA, MAKYSHA, HORNAPI, YONTYMAK, FRON);

		addKillNpcWithLog(6, 1025838, A_LIST, 1, MAKYSHA);
		addKillNpcWithLog(6, 1025839, B_LIST, 1, HORNAPI);
		addLevelCheck("izshael_q10304_01.htm", 90);
		addQuestCompletedCheck("izshael_q10304_02.htm", 10302);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final int cond = st.getCond();

		String htmltext = event;

		if(htmltext.equalsIgnoreCase("izshael_q10304_08.htm"))
		{
			if(cond == 1)
			{
				if(st.getPlayer().canEnterInstance(fortunaId))
				{
					htmltext = "izshael_q10304_09.htm";
					st.setCond(2);
					st.takeItems(17618, -1);
				}
			}
		}
		else if(event.equalsIgnoreCase("finish_1") || event.equalsIgnoreCase("finish_2") || event.equalsIgnoreCase("finish_3"))
		{
			if(cond == 9)
			{
				htmltext = "izshael_q10304_13.htm";
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(57, 47085998);

				if(event.equalsIgnoreCase("finish_1"))
					st.giveItems(36563, 68);
				else if(event.equalsIgnoreCase("finish_2"))
				{
					st.giveItems(17526, 1);
					st.giveItems(17527, 1);
				}
				else if(event.equalsIgnoreCase("finish_3"))
					st.giveItems(34861, 4);
				st.finishQuest();
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = NO_QUEST_DIALOG;

		if(npcId == ISHAEL)
		{
			if(cond == 0)
			{
				htmltext = "izshael_q10304_04.htm";
				st.setCond(1);
			}
			else if(cond == 1)
				htmltext = "izshael_q10304_06.htm";
			else if(cond >= 2 && cond <= 8)
				htmltext = "izshael_q10304_10.htm";
			else if(cond == 9)
				htmltext = "izshael_q10304_11.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == ISHAEL)
			htmltext = "izshael_q10304_03.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();
		if(npcId == YUI)
		{
			if(cond == 2)
				st.setCond(3);
		}
		else if(npcId == KINEN)
		{
			if(cond == 3)
				st.setCond(4);
		}
		else if(npcId == KONJAN)
		{
			if(cond == 4)
				st.setCond(5);
		}
		else if(npcId == RASINDA)
		{
			if(cond == 5)
				st.setCond(6);
		}
		else if(npcId == MAKYSHA || npcId == HORNAPI)
		{
			if(cond == 6)
			{
				boolean doneKill = updateKill(npc, st);
				if(doneKill)
				{
					st.unset(A_LIST);
					st.unset(B_LIST);
					st.setCond(7);
				}
			}
		}
		else if(npcId == YONTYMAK)
		{
			if(cond == 7)
				st.setCond(8);
		}
		else if(npcId == FRON)
		{
			if(cond == 8)
				st.setCond(9);
		}
		return null;
	}
}