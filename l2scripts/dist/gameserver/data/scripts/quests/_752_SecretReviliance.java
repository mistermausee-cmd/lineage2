package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _752_SecretReviliance extends Quest
{
	//q items
	private static final int SOUL = 36074;
	private static final int INIE = 36075;
	//reward items
	private static final int SCROLL = 36082;

	private static final int HESET = 33780;

	private static final int EXP_REWARD = 408665250;
	private static final int SP_REWARD = 98079;

	public _752_SecretReviliance()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(HESET);
		addTalkId(HESET);
		addQuestItem(SOUL);
		addQuestItem(INIE);
		
		addKillId(23252, 23253, 23254, 23257, 23255, 23256, 23258, 23259);
		
		addLevelCheck("You cannot procceed with this quest until you have completed the Mystrerious Journey quest!", 93);
		addQuestCompletedCheck("You cannot procceed with this quest until you have completed the Mystrerious Journey quest!", 10386);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.takeAllItems(SOUL);
			st.takeAllItems(INIE);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(SCROLL, 1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if(npcId == HESET)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "notcollected.htm";
			else if(cond == 2)
				htmltext = "collected.htm";
		}
			
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == HESET)
			htmltext = "You have completed this quest today, come back tomorow at 6:30!";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getQuestItemsCount(SOUL) < 10 && Rnd.chance(10))
			qs.giveItems(SOUL, 1);
		if(qs.getQuestItemsCount(INIE) < 20 && Rnd.chance(10))
			qs.giveItems(INIE, 1);
		if(qs.getQuestItemsCount(SOUL) >= 10 && qs.getQuestItemsCount(INIE) >= 20)
			qs.setCond(2);
		return null;
	}
}