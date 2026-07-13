package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10825_ForVictory extends Quest
{
	// NPC's
	private static final int KERTIS = 30870;
	private static final int[] FLAGS = {36741, 36742, 36743, 36744, 36745, 36746, 36747, 36748, 36749};


	// Item's
	private static final int PROOFOFFLAG = 46059;
	private static final int CERTIF2 = 46057;
	private static final int BOOK = 45927;

	public _10825_ForVictory()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(KERTIS);
		addTalkId(KERTIS);
		addTalkId(FLAGS);
		addLevelCheck("captain_kurtis_q10825_02.htm", 99);
		addNobleCheck("captain_kurtis_q10825_02.htm", true);
		addItemHaveCheck("captain_kurtis_q10825_03.htm", 45637, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("captain_kurtis_q10825_06.htm"))
		{
			st.setCond(2); //ћежсерверные осады, квест сразу на сдачу.
		}
		else if (event.equalsIgnoreCase("captain_kurtis_q10825_09.htm"))
		{
			st.takeItems(PROOFOFFLAG, -1);
			st.giveItems(CERTIF2, 1, false);
			st.giveItems(BOOK, 1, false);
			if (checkReward(st))
				htmltext = "captain_kurtis_q10825_10.htm";
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
			case KERTIS:
				if (cond == 0)
					htmltext = "captain_kurtis_q10825_01.htm";
				else if (cond == 1)
					htmltext = "captain_kurtis_q10825_07.htm";
				else if (cond == 2)
					htmltext = "captain_kurtis_q10825_08.htm";
				break;

			case 36741:
			case 36742:
			case 36743:
			case 36744:
			case 36745:
			case 36746:
			case 36747:
			case 36748:
			case 36749:
				if (cond == 1)
				{
					if(st.get("TIME_OUT")!= null && System.currentTimeMillis() < Long.parseLong(st.get("TIME_OUT")))
						return "You have already received the Mark of Valor on this siege, try the next siege.";

					st.set("TIME_OUT", String.valueOf(System.currentTimeMillis() + 7200000000L));
					if(npcId == 36748 || npcId == 36745)
						st.giveItems(PROOFOFFLAG, 5);
					else if(npcId == 36749 || npcId == 36747 || npcId == 36746)
						st.giveItems(PROOFOFFLAG, 4);
					else
						st.giveItems(PROOFOFFLAG, Rnd.get(1, 3));

					if(st.getQuestItemsCount(PROOFOFFLAG) >= 10)
						st.setCond(2);
					return null;
				}
				break;
		}
		return htmltext;
	}


	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getDualClassLevel() > 99 && st.haveQuestItem(46056) && st.haveQuestItem(46057) && st.haveQuestItem(45635) && st.haveQuestItem(45636))
		{
			st.getPlayer().getQuestState(10823).setCond(2);
			return true;
		}

		return false;
	}
}