package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

public class _827_EinhasadsOrder extends Quest
{
	// NPCs
	private static final int KLAUS = 34096;
	private static final int KLAUSEND = 34151;
	private static final int[] MOBS = {23616,23617,23618,23619,23620,23621,23622,23623,23624,23625,23626,23627,23628,23629,23630
			,23631,23632,23633,23634,23635,23636,23637,23638,23639,23640,23641,23642,23643,23644,23645,23646,23647};
	// Item
	private static final int ITEM = 46372;

	private static final long EXP_REWARD = 2422697985l;
	private static final int SP_REWARD = 5814450; 

	public _827_EinhasadsOrder()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(KLAUS);
		addTalkId(KLAUS);
		addTalkId(KLAUSEND);
		addLevelCheck(KLAUS, "as_sir_karrel_vasper_q0827_02.htm", 99);
		addQuestItem(ITEM);
		addKillId(MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("as_sir_karrel_vasper_q0827_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("as_sir_karrel_vasper_q0827_08.htm"))
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
		if(npc.getNpcId() == KLAUS)
		{
			if(st.getCond() == 0)
				htmtext = "as_sir_karrel_vasper_q0827_01.htm";
			else if(st.getCond() == 1)
				htmtext = "as_sir_karrel_vasper_q0827_06.htm";
			else if(st.getCond() == 2)
				htmtext = "as_sir_karrel_vasper_q0827_07.htm";
		}
		else if(npc.getNpcId() == KLAUSEND)
		{
			if(st.getCond() == 2)
				htmtext = "as_sir_karrel_vasper_q0827_07.htm";
		}
		return htmtext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1)
			return null;

		if(ArrayUtils.contains(MOBS, npc.getNpcId()))
		{
			st.giveItems(ITEM, 1);
			if (st.getQuestItemsCount(ITEM) >= 30)
				st.setCond(2);
		}
		return null;
	}
}
