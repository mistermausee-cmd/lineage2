package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

public class _826_InSearchOfTheSecretWeapon extends Quest
{
	// NPCs
	private static final int NETTI = 34095;
	private static final int[] MOBS = {23653,23654,23655,23656,23657,23658,23659,23660,23661,23662,23663,23664,23665,23666,23667
			,23668,23669,23670,23671,23672,23673,23674,23675,23676};
	// Item
	private static final int ITEM = 46371;

	public _826_InSearchOfTheSecretWeapon()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NETTI);
		addTalkId(NETTI);
		addLevelCheck(NETTI, "as_neti_q0826_02.htm", 99);
		addQuestItem(ITEM);
		addKillId(MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("as_neti_q0826_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("as_neti_q0826__08.htm"))
		{
			st.giveItems(46376, 1);
			st.takeItems(ITEM, -1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if(npc.getNpcId() == NETTI)
		{
			if(st.getCond() == 0)
				htmtext = "as_neti_q0826_01.htm";
			else if(st.getCond() == 1)
				htmtext = "as_neti_q0826_06.htm";
			else if(st.getCond() == 2)
				htmtext = "as_neti_q0826_07.htm";
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
			if (st.getQuestItemsCount(ITEM) >= 8)
				st.setCond(2);
		}
		return null;
	}
}