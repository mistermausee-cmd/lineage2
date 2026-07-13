package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;

import org.apache.commons.lang3.ArrayUtils;

public class _10421_AssasinationOfTheVarkaSilenosComm extends Quest
{
    //Квестовые персонажи
    private static final int HANSEN = 33853;

    //Монстры
	private static final int MOBS = 27502;

	private static final int EXP_REWARD = 327446943;
	private static final int SP_REWARD = 1839; 

	public _10421_AssasinationOfTheVarkaSilenosComm()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(MOBS);
		addLevelCheck("hansen_q10421_02.htm", 76/*, 80*/);
		addRaceCheck("hansen_q10421_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addQuestCompletedCheck("hansen_q10421_02.htm", 10420);
		addClassTypeCheck("hansen_q10421_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("hansen_q10421_05.htm"))
		{
			st.setCond(1);
			return "accept.htm";
		}	
		else if(event.equalsIgnoreCase("hansen_q10421_08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();	
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;

		switch (npcId)
		{
			case HANSEN:
				if (cond == 0)
					htmltext = "hansen_q10421_01.htm";
				else if (cond == 1)
					htmltext = "hansen_q10421_06.htm";
				else if (cond == 2)
					htmltext = "hansen_q10421_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		qs.setCond(2);
		return null;
	}	
}