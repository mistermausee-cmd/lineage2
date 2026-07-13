package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class _10756_AnInterdimensionalDraft extends Quest
{
	// NPC's
	private static final int FIO = 33963;

	// Monster's
	private static final int[] MONSTERS = {23414, 23415, 23416, 21023, 20078, 21026, 21025, 21024};

	// Item's
	private static final int UNDERWIND = 39493;
	
	private static final int EXP_REWARD = 267479;	private static final int SP_REWARD = 41; 	public _10756_AnInterdimensionalDraft()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(FIO);
		addTalkId(FIO);
		addKillId(MONSTERS);
		addLevelCheck("33963-0.htm", 20);
		addRaceCheck("33963-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33963-8.htm"))
		{
			st.takeItems(UNDERWIND, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
			case FIO:
				if(cond == 0)
					htmltext = "33963-1.htm";
				else if (cond == 1)
					htmltext = "33963-6.htm";
				else if (cond == 2)
					htmltext = "33963-7.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 1)
				st.giveItems(UNDERWIND, 1);
			if(st.getQuestItemsCount(UNDERWIND) >= 30)
				st.setCond(2);
		}
		return null;
	}
}