package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10274_CollectingInTheAir extends Quest
{
	private final static int Lekon = 32557;

	private final static int StarStoneExtractionScroll = 13844;
	private final static int ExpertTextStarStoneExtractionSkillLevel1 = 13728;
	private final static int ExtractedCoarseRedStarStone = 13858;
	private final static int ExtractedCoarseBlueStarStone = 13859;
	private final static int ExtractedCoarseGreenStarStone = 13860;

	private static final int EXP_REWARD = 6660000;	private static final int SP_REWARD = 1598; 	public _10274_CollectingInTheAir()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Lekon);
		addLevelCheck("32557-00.htm", 75);
		addQuestCompletedCheck("32557-00.htm", 10273);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32557-03.htm"))
		{
			st.setCond(1);
			st.giveItems(StarStoneExtractionScroll, 8);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(cond == 0)
			htmltext = "32557-01.htm";
		else if(cond > 0)
		{
			if(st.getQuestItemsCount(ExtractedCoarseRedStarStone) + st.getQuestItemsCount(ExtractedCoarseBlueStarStone) + st.getQuestItemsCount(ExtractedCoarseGreenStarStone) >= 8)
			{
				htmltext = "32557-05.htm";
				st.takeAllItems(ExtractedCoarseRedStarStone, ExtractedCoarseBlueStarStone, ExtractedCoarseGreenStarStone);
				st.giveItems(ExpertTextStarStoneExtractionSkillLevel1, 1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
			}
			else
				htmltext = "32557-04.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Lekon)
			htmltext = "32557-0a.htm";
		return htmltext;
	}
}