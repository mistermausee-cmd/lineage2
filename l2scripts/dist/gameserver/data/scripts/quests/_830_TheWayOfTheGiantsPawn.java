package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

public class _830_TheWayOfTheGiantsPawn extends Quest
{
	// NPCs
	private static final int ENICHE = 34099;
	private static final int ENICHEEND = 34154;
	private static final int[] MOBS = {23616,23617,23618,23619,23620,23621,23622,23623,23624,23625,23626,23627,23628,23629,23630
			,23631,23632,23633,23634,23635,23636,23637,23638,23639,23640,23641,23642,23643,23644,23645,23646,23647};
	public static final String A_LIST = "A_LIST";

	// Item
	private static final int ITEM = 46372;

	private static final long EXP_REWARD = 2422697985l;
	private static final int SP_REWARD = 5814450; 

	public _830_TheWayOfTheGiantsPawn()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(ENICHE);
		addTalkId(ENICHE);
		addTalkId(ENICHEEND);
		addLevelCheck(ENICHE, NO_QUEST_DIALOG, 99);
		addQuestItem(ITEM);
		addKillNpcWithLog(1, 83011, A_LIST, 45, MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("34099-04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("34099-07.htm"))
		{
			st.giveItems(46375, 1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(ITEM, -1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if(npc.getNpcId() == ENICHE)
		{
			if(st.getCond() == 0)
				htmtext = "34099-01.htm";
			else if(st.getCond() == 1)
				htmtext = "34099-05.htm";
			else if(st.getCond() == 2)
				htmtext = "34099-06.htm";
		}
		else if(npc.getNpcId() == ENICHEEND)
		{
			if(st.getCond() == 2)
				htmtext = "34099-06.htm";
		}
		return htmtext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}
}