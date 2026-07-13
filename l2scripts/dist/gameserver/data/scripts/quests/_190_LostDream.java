package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _190_LostDream extends Quest
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;
	private static final int Juris = 30113;

	private static final int EXP_REWARD = 634158;	private static final int SP_REWARD = 152; 	public _190_LostDream()
	{
		super(PARTY_NONE, ONETIME);

		addTalkId(Kusto, Nikola, Lorain, Juris);
		addFirstTalkId(Kusto);
		addLevelCheck("head_blacksmith_kusto_q0190_02.htm", 42);
		addQuestCompletedCheck("head_blacksmith_kusto_q0190_02.htm", 187);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("head_blacksmith_kusto_q0190_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("head_blacksmith_kusto_q0190_06.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("juria_q0190_03.htm"))
		{
			st.setCond(2);
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
			if(npcId == Kusto)
			{
				if(cond == 0)
					htmltext = "head_blacksmith_kusto_q0190_01.htm";
				else if(cond == 1)
					htmltext = "head_blacksmith_kusto_q0190_04.htm";
				else if(cond == 2)
					htmltext = "head_blacksmith_kusto_q0190_05.htm";
				else if(cond == 3)
					htmltext = "head_blacksmith_kusto_q0190_07.htm";
				else if(cond == 5)
				{
					htmltext = "head_blacksmith_kusto_q0190_08.htm";
					st.giveItems(ADENA_ID, 127224);
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
					st.finishQuest();
				}
			}
			else if(npcId == Juris)
			{
				if(cond == 1)
					htmltext = "juria_q0190_01.htm";
				else if(cond == 2)
					htmltext = "juria_q0190_04.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 3)
				{
					htmltext = "researcher_lorain_q0190_01.htm";
					st.setCond(4);
				}
				else if(cond == 4)
					htmltext = "researcher_lorain_q0190_02.htm";
			}
			else if(npcId == Nikola)
				if(cond == 4)
				{
					htmltext = "maestro_nikola_q0190_01.htm";
					st.setCond(5);
				}
				else if(cond == 5)
					htmltext = "maestro_nikola_q0190_02.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(187);
		if(qs != null && qs.isCompleted() && player.getQuestState(getId()) == null)
			newQuestState(player);
		return "";
	}
}