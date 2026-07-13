package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10422_AssassinationOfTheVarkaSilenosChif extends Quest
{
    //Квестовые персонажи
    private static final int HANSEN = 33853;

    //Монстры
    private static final int MOBS = 27503;

	private static final int EXP_REWARD = 351479;
	private static final int SP_REWARD = 1839; 

	public _10422_AssassinationOfTheVarkaSilenosChif()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(MOBS);
		addLevelCheck("hansen_q10422_02.htm", 76/*, 80*/);
		addRaceCheck("hansen_q10422_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addQuestCompletedCheck("hansen_q10422_02.htm", 10421);
		addClassTypeCheck("hansen_q10422_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hansen_q10422_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("hansen_q10422_08.htm"))
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
					htmltext = "hansen_q10422_01.htm";
				else if (cond == 1)
					htmltext = "hansen_q10422_06.htm";
				else if (cond == 2)
					htmltext = "hansen_q10422_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
			qs.setCond(2);
		return null;
	}	
}