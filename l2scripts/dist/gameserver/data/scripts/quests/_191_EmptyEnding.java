package quests;


import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _191_EmptyEnding extends Quest
{
	private static final int DOROTI = 30970;
	private static final int LOREIN = 30673;
	private static final int SHEGFILD = 30068;
	private static final int KYSTO = 30512;

	private static final int METAL = 10371;

	public _191_EmptyEnding()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(DOROTI);
		addTalkId(DOROTI, LOREIN, SHEGFILD, KYSTO);
		addQuestItem(METAL);
		addLevelCheck("dorothy_the_locksmith_q0191_02.htm", 42);
		addQuestCompletedCheck("dorothy_the_locksmith_q0191_02.htm", 188);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("dorothy_the_locksmith_q0191_04.htm"))
		{
			st.setCond(1);
			if(!st.haveQuestItem(METAL))
				st.giveItems(METAL, 1, false);
		}
		else if (event.equalsIgnoreCase("researcher_lorain_q0191_02.htm"))
		{
			st.setCond(2);
			st.takeItems(METAL, -1);
		}
		else if (event.equalsIgnoreCase("shegfield_q0191_03.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("head_blacksmith_kusto_q0191_02.htm"))
		{
			st.giveItems(57, 134292);
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
			case DOROTI:
				if (cond == 0)
					htmltext = "dorothy_the_locksmith_q0191_01.htm";
				else if (cond == 1)
					htmltext = "dorothy_the_locksmith_q0191_05.htm";
				break;

			case LOREIN:
				if (cond == 1)
					htmltext = "researcher_lorain_q0191_01.htm";
				else if (cond == 2)
					htmltext = "researcher_lorain_q0191_03.htm";
				else if (cond == 3)
				{
					st.setCond(4);
					htmltext = "researcher_lorain_q0191_04.htm";
				}
				else if (cond == 4)
					htmltext = "researcher_lorain_q0191_05.htm";
				break;

			case SHEGFILD:
				if (cond == 2)
					htmltext = "shegfield_q0191_01.htm";
				else if (cond == 3)
					htmltext = "shegfield_q0191_04.htm";
				break;
			case KYSTO:
				if (cond == 4)
					htmltext = "head_blacksmith_kusto_q0191_01.htm";
				break;

		}
		return htmltext;
	}

}