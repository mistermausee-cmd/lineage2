package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

import java.util.ArrayList;

//By Evil_dnk

public class _10787_ASpyMission extends Quest
{
	// NPC's
	private static final int ZUBAN = 33867;
	private static final int STRANGCHEST = 33994;

	// Item's
	private static final int EMBRYOMASSIVES = 39724;

	private static ArrayList<NpcInstance> fighters = new ArrayList<NpcInstance>();


	private static final int EXP_REWARD = 17234475;
	private static final int SP_REWARD = 750; 

	public _10787_ASpyMission()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ZUBAN);
		addTalkId(ZUBAN);
		addTalkId(STRANGCHEST);
		addQuestCompletedCheck("33867-0.htm", 10786);
		addLevelCheck("33867-0.htm", 61/*, 65*/);
		addRaceCheck("33867-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33867-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33867-7.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("33994-2.htm"))
		{
			if(Rnd.chance(15))
			{
				st.setCond(2);
				st.giveItems(EMBRYOMASSIVES, 1, false);
				npc.doDie(null);
				npc.endDecayTask();
				return "33994-2.htm";
			}
			if(fighters != null)
				fighters.clear();
			fighters.add(st.addSpawn(27540, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			fighters.add(st.addSpawn(27541, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			for (NpcInstance fighter : fighters)
			{
				fighter.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			}
			npc.doDie(null);
			npc.endDecayTask();
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
			case ZUBAN:
				if(cond == 0)
					htmltext = "33867-1.htm";
				else if (cond == 1)
					htmltext = "33867-5.htm";
				else if (cond == 2)
					htmltext = "33867-6.htm";
			break;

			case STRANGCHEST:
				if (cond == 1)
					htmltext = "33994-1.htm";
	        break;
		}
		return htmltext;
	}
}