package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _772_PurifyingSouls extends Quest
{
	// NPC's
	private static final int QUINSI = 33838;

	// Monster's
	private static final int[] MONSTERS = { 23330, 23337, 23332, 23333, 23334, 23335, 23336, 25927};

	// Item's
	private static final int SOULOFDARKNESS = 36680;
	private static final int SOULOFLIGHT = 36696;


	public _772_PurifyingSouls()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(QUINSI);
		addKillId(MONSTERS);
		addQuestItem(SOULOFDARKNESS, SOULOFLIGHT);
		addLevelCheck("33838-0.htm", 99);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33838-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33838-9.htm"))
		{
			st.giveItems(30357, 5);
			st.giveItems(30358, 5);
			st.giveItems(32316, 5);
			st.giveItems(37018, 5);
			st.takeItems(SOULOFLIGHT, -1);
			if(st.getQuestItemsCount(SOULOFLIGHT) >= 1000 && st.getQuestItemsCount(SOULOFLIGHT) <= 1999)
				st.giveItems(37021, 1);
			else if(st.getQuestItemsCount(SOULOFLIGHT) >= 2000)
				st.giveItems(37022, 1);
				st.giveItems(35562, 1);
			st.takeItems(SOULOFLIGHT, -1);
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
			case QUINSI:
				if(cond == 0)
					htmltext = "33838-1.htm";
				else if(cond == 1)
					htmltext = "33838-5.htm";
				else if(cond == 2)
					htmltext = "33838-6.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == QUINSI)
			htmltext = "33838-10.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(SOULOFDARKNESS, 1, 1, 50, 30))
				st.setCond(2);
		}
		else if(cond == 2)
		{
			if(st.rollAndGive(SOULOFLIGHT, 1, 1, 2000, 70))
				st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}