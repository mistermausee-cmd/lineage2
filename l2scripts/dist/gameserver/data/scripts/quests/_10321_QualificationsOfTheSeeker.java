package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Bonux
 */
public class _10321_QualificationsOfTheSeeker extends Quest
{
	//NPC's
	private static final int TEODOR = 32975;
	private static final int SHENON = 32974;

	private static final int EXP_REWARD = 300;
	private static final int SP_REWARD = 6;
	
	public _10321_QualificationsOfTheSeeker()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(TEODOR);
		addTalkId(TEODOR, SHENON);
		addLevelCheck("32975_00.htm", 1/*, 20*/);
		addQuestCompletedCheck("32975_00.htm", 10320);
		addRaceCheck("32975_00.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		//String htmltext = event;
		if(event.equalsIgnoreCase("32975_03.htm"))
		{
			st.setCond(1);
			st.showTutorialClientHTML("QT_027_Quest_01");
		}
		else if(event.equalsIgnoreCase("32974_02.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == TEODOR)
		{
			if(cond == 0)
				htmltext = "32975_01.htm";
			else if(cond == 1)
				htmltext = "32975_04.htm";
		}
		else if(npcId == SHENON && st.getCond() == 1)
		{
			htmltext = "32974_01.htm";
		}
		return htmltext;
	}
}