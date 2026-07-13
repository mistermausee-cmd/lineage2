package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _903_TheCallofAntharas extends Quest
{
	private static final int Theodric = 30755;
	private static final int BehemothDragonLeather = 21992;
	private static final int TaraskDragonsLeatherFragment = 21991;

	private static final int TaraskDragon = 29190;
	private static final int BehemothDragon = 29069;


	public _903_TheCallofAntharas()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(Theodric);
		addKillId(TaraskDragon, BehemothDragon);
		addQuestItem(BehemothDragonLeather, TaraskDragonsLeatherFragment);
		addLevelCheck("theodric_q903_00.htm", 83);
		addItemHaveCheck("theodric_q903_00b.htm", 3865, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("theodric_q903_03.htm"))
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
		if(npc.getNpcId() == Theodric)
		{
			if(cond == 0)
				htmltext = "theodric_q903_01.htm";
			else if(cond == 1)
				htmltext = "theodric_q903_04.htm";
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(BehemothDragonLeather) > 0 && st.getQuestItemsCount(TaraskDragonsLeatherFragment) > 0)
				{
					htmltext = "theodric_q903_05.htm";
					st.takeAllItems(BehemothDragonLeather);
					st.takeAllItems(TaraskDragonsLeatherFragment);
					st.giveItems(21897, 1); // Scroll: Antharas Call
					st.finishQuest();
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Theodric)
			htmltext = "theodric_q903_00a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			switch(npc.getNpcId())
			{
				case TaraskDragon:
					if(st.getQuestItemsCount(TaraskDragonsLeatherFragment) < 1)
						st.giveItems(TaraskDragonsLeatherFragment, 1);
					break;
				case BehemothDragon:
					if(st.getQuestItemsCount(BehemothDragonLeather) < 1)
						st.giveItems(BehemothDragonLeather, 1);
					break;
				default:
					break;
			}
			if(st.getQuestItemsCount(BehemothDragonLeather) > 0 && st.getQuestItemsCount(TaraskDragonsLeatherFragment) > 0)
				st.setCond(2);
		}
		return null;
	}
}