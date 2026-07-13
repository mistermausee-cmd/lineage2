package quests;

import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _183_RelicExploration extends Quest
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;

	private static final int EXP_REWARD = 133636;	private static final int SP_REWARD = 32; 					public _183_RelicExploration()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Kusto);
		addStartNpc(Nikola);
		addTalkId(Lorain);
		addLevelCheck("30512-00.htm", 40);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if(event.equalsIgnoreCase("30512-03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30673-04.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("Contract"))
		{
			Quest q1 = QuestHolder.getInstance().getQuest(184);
			if(q1 != null)
			{
				st.giveItems(ADENA_ID, 26866);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				QuestState qs1 = q1.newQuestState(player);
				q1.notifyEvent("30621-01.htm", qs1, npc);
				st.finishQuest();
			}
			return null;
		}
		else if(event.equalsIgnoreCase("Consideration"))
		{
			Quest q2 = QuestHolder.getInstance().getQuest(185);
			if(q2 != null)
			{
				st.giveItems(ADENA_ID, 26866);
				QuestState qs2 = q2.newQuestState(st.getPlayer());
				q2.notifyEvent("30621-01.htm", qs2, npc);
				st.finishQuest();
			}
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Kusto)
		{
			if(st.isNotAccepted())
				htmltext = "30512-01.htm";
			else
				htmltext = "30512-04.htm";
		}
		else if(npcId == Lorain)
			if(cond == 1)
				htmltext = "30673-01.htm";
			else
				htmltext = "30673-05.htm";
		else if(npcId == Nikola && cond == 2)
			htmltext = "30621-01.htm";
		return htmltext;
	}
}