package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _189_ContractCompletion extends Quest
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Luka = 30621;
	private static final int Shegfield = 30068;

	private static final int Metal = 10370;

	private static final int EXP_REWARD = 704620;	private static final int SP_REWARD = 169; 	public _189_ContractCompletion()
	{
		super(PARTY_NONE, ONETIME);

		addTalkId(Kusto, Luka, Lorain, Shegfield);
		addFirstTalkId(Luka);
		addQuestItem(Metal);
		addLevelCheck("blueprint_seller_luka_q0189_02.htm", 42);
		addQuestCompletedCheck("blueprint_seller_luka_q0189_02.htm", 186);		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("blueprint_seller_luka_q0189_03.htm"))
		{
			st.setCond(1);
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("researcher_lorain_q0189_02.htm"))
		{
			st.setCond(2);
			st.takeItems(Metal, -1);
		}
		else if(event.equalsIgnoreCase("shegfield_q0189_03.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("head_blacksmith_kusto_q0189_02.htm"))
		{
			st.giveItems(ADENA_ID, 141360);
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
			if(npcId == Luka)
			{
				if(cond == 0)
					htmltext = "blueprint_seller_luka_q0189_01.htm";
				else if(cond == 1)
					htmltext = "blueprint_seller_luka_q0189_04.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 1)
					htmltext = "researcher_lorain_q0189_01.htm";
				else if(cond == 2)
					htmltext = "researcher_lorain_q0189_03.htm";
				else if(cond == 3)
				{
					htmltext = "researcher_lorain_q0189_04.htm";
					st.setCond(4);
				}
				else if(cond == 4)
					htmltext = "researcher_lorain_q0189_05.htm";
			}
			else if(npcId == Shegfield)
			{
				if(cond == 2)
					htmltext = "shegfield_q0189_01.htm";
				else if(cond == 3)
					htmltext = "shegfield_q0189_04.htm";
			}
			else if(npcId == Kusto)
				if(cond == 4)
					htmltext = "head_blacksmith_kusto_q0189_01.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(186);
		if(qs != null && qs.isCompleted() && player.getQuestState(getId()) == null)
			newQuestState(player);
		return "";
	}
}