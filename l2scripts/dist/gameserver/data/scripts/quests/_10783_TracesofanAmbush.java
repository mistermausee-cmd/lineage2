package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

//By Evil_dnk

public class _10783_TracesofanAmbush extends Quest
{
	// NPC's
	private static final int NOVAIN = 33866;

	// Monster's
	private static final int[] MONSTER = {20679, 20680, 21017, 21018, 21019, 21020, 21258, 21259, 21021, 21022, 27539};

	// Item's
	private static final int SCRAB = 39722;

	private NpcInstance predator = null;

	//Npc Say
	private static final NpcString[] sayinfight =
		{
			NpcString.I_WILL_GIVE_YOU_DEATH,
			NpcString.BACK_FOR_MORE_HUH,
			NpcString.YOU_LITTLE_PUNK_TAKE_THAT,
		};


	private static final int EXP_REWARD = 12146608;
	private static final int SP_REWARD = 1315; 

	public _10783_TracesofanAmbush()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(NOVAIN);
		addTalkId(NOVAIN);
		addKillId(MONSTER);
		addLevelCheck("33866-0.htm", 58/*, 61*/);
		addRaceCheck("33866-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33866-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33866-7.htm"))
		{
			
			st.takeItems(SCRAB, -1);
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
			case NOVAIN:
				if(cond == 0)
					htmltext = "33866-1.htm";
				else if (cond == 1)
					htmltext = "33866-5.htm";
				else if (cond == 2)
					htmltext = "33866-6.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if (npc.getNpcId() != 27539)
			{
				predator = st.addSpawn(27539, npc.getX(), npc.getY(), npc.getZ(), 180000);
				predator.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
				Functions.npcSay(predator, sayinfight[Rnd.get(sayinfight.length)]);
			}
			else if (npc.getNpcId() == 27539)
			{
				if(st.rollAndGive(SCRAB, 1, 1, 50, 100))
					st.setCond(2);
			}
		}
		return null;
	}
}