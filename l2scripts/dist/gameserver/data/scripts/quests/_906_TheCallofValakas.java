package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _906_TheCallofValakas extends Quest
{
	private static final int Klein = 31540;
	private static final int LavasaurusAlphaFragment = 21993;
	private static final int ValakasMinion = 29029;

	public _906_TheCallofValakas()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(Klein);
		addTalkId(Klein);
		addKillId(ValakasMinion);
		addQuestItem(LavasaurusAlphaFragment);
		addLevelCheck("klein_q906_00.htm", 83);
		addItemHaveCheck("klein_q906_00b.htm", 7267, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klein_q906_03.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(npc.getNpcId() == Klein)
		{
			if(cond == 0)
				htmltext = "klein_q906_01.htm";
			else if(cond == 1)
				htmltext = "klein_q906_04.htm";
			else if(cond == 2)
			{
				htmltext = "klein_q906_05.htm";
				st.takeAllItems(LavasaurusAlphaFragment);
				st.giveItems(21895, 1); // Scroll: Valakas Call
				st.finishQuest();
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Klein)
			htmltext = "klein_q906_00a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(npc.getNpcId() == ValakasMinion)
			{
				st.giveItems(LavasaurusAlphaFragment, 1);
				st.setCond(2);
			}
		}
		return null;
	}
}