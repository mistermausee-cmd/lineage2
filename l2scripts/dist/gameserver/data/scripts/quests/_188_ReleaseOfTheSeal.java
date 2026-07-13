package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _188_ReleaseOfTheSeal extends Quest
{
	private static final int LOREIN = 30673;
	private static final int NIKOLA = 30621;
	private static final int DOROTI = 30970;

	public _188_ReleaseOfTheSeal()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(LOREIN);
		addTalkId(LOREIN, NIKOLA, DOROTI);
		addLevelCheck("researcher_lorain_q0188_02.htm", 41);
		addItemHaveCheck("researcher_lorain_q0188_02.htm", 10362, 1);
		addQuestCompletedCheck("researcher_lorain_q0188_02.htm", 184);
		addQuestCompletedCheck("researcher_lorain_q0188_02.htm", 185);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (event.equalsIgnoreCase("researcher_lorain_q0188_03.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("maestro_nikola_q0188_04.htm"))
		{
			st.setCond(2);
		}
		if (event.equalsIgnoreCase("dorothy_the_locksmith_q0188_03.htm"))
		{
			st.giveItems(57, 110336);
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
			case LOREIN:
				if (cond == 0)
					htmltext = "researcher_lorain_q0188_01.htm";
				else if (cond == 1)
					htmltext = "researcher_lorain_q0188_04.htm";
				break;

			case NIKOLA:
				if (cond == 1)
					htmltext = "maestro_nikola_q0188_01.htm";
				else if (cond == 2)
					htmltext = "maestro_nikola_q0188_05.htm";
				break;

			case DOROTI:
				if (cond == 2)
					htmltext = "dorothy_the_locksmith_q0188_01.htm";
				break;
		}
		return htmltext;
	}
 }