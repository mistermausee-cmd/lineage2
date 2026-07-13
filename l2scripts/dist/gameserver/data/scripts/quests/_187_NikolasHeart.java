package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _187_NikolasHeart extends Quest
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;

	private static final int Certificate = 10362;
	private static final int Metal = 10368;

	private static final int EXP_REWARD = 549120;	private static final int SP_REWARD = 131; 				public _187_NikolasHeart()
	{
		super(PARTY_NONE, ONETIME);

		addTalkId(Kusto, Nikola, Lorain);
		addFirstTalkId(Lorain);
		addQuestItem(Certificate, Metal);
		addLevelCheck("researcher_lorain_q0187_02.htm", 41);
		addQuestCompletedCheck("researcher_lorain_q0187_02.htm", 184);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("researcher_lorain_q0187_03.htm"))
		{
			st.setCond(1);
			st.takeItems(Certificate, -1);
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("maestro_nikola_q0187_03.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("head_blacksmith_kusto_q0187_03.htm"))
		{
			st.giveItems(ADENA_ID, 110336);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(st.isStarted())
			if(npcId == Lorain)
			{
				if(cond == 0)
					htmltext = "researcher_lorain_q0187_01.htm";
				else if(cond == 1)
					htmltext = "researcher_lorain_q0187_04.htm";
			}
			else if(npcId == Nikola)
			{
				if(cond == 1)
					htmltext = "maestro_nikola_q0187_01.htm";
				else if(cond == 2)
					htmltext = "maestro_nikola_q0187_04.htm";
			}
			else if(npcId == Kusto)
				if(cond == 2)
					htmltext = "head_blacksmith_kusto_q0187_01.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(184);
		if(qs != null && qs.isCompleted() && player.getQuestState(getId()) == null)
			newQuestState(player);
		return "";
	}
}