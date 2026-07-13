package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10775_InSearchofanAncientGiant extends Quest
{
	// NPC's
	private static final int ROMBEL = 30487;
	private static final int BELKATI = 30485;

	// Monster's
	private static final int[] MONSTERS = {20753, 20754, 21040, 21037, 21038, 23153, 23154, 23155};
	// Item's
	
	private static final int ENERGYOFREGENERATION = 39715;

	private static final int EXP_REWARD = 10526523;
	private static final int SP_REWARD = 1066; 

	public _10775_InSearchofanAncientGiant()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ROMBEL);
		addTalkId(BELKATI);
		addKillId(MONSTERS);
		addLevelCheck("30487-0.htm", 46);
		addRaceCheck("30487-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30487-5.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30485-4.htm"))
		{
			st.takeItems(ENERGYOFREGENERATION, -1);
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
			case ROMBEL:
				if(cond == 0)
					htmltext = "30487-1.htm";
				else if (cond == 1)
					htmltext = "30487-6.htm";
			break;

			case BELKATI:
				if(cond == 2)
					htmltext = "30485-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(ENERGYOFREGENERATION, 1, 1, 40, 90))
				st.setCond(2);
		}
		return null;
	}
}