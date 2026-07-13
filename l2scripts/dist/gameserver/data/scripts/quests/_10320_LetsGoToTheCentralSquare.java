package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

/**
 * @author Эванесса & Bonux
 */
public class _10320_LetsGoToTheCentralSquare extends Quest
{
	//NPC's
	private static final int PANTEON = 32972;
	private static final int TEODOR = 32975;

	private static final int EXP_REWARD = 70;
	private static final int SP_REWARD = 5;

	public _10320_LetsGoToTheCentralSquare()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(PANTEON);
		addFirstTalkId(PANTEON);
		addTalkId(PANTEON, TEODOR);
		addLevelCheck("32972_00.htm", 1/*, 20*/);
		addRaceCheck("32972_00.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32972_03.htm"))
		{
			st.setCond(1);
			st.showTutorialClientHTML("QT_001_Radar_01");
		}
		else if(event.equalsIgnoreCase("32975_02.htm"))
		{
			st.giveItems(ADENA_ID, 3000);
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
		if(npcId == PANTEON)
		{
			if(cond == 0)
				htmltext = "32972_01.htm";
			else if(cond == 1)
				htmltext = "32972_04.htm";
		}
		else if(npcId == TEODOR && st.getCond() == 1)
		{
			htmltext = "32975_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		if(npc.getNpcId() == PANTEON)
		{
			QuestState st = player.getQuestState(getId());
			if((st == null || st.getCond() == 0) && checkStartCondition(npc, player) == null)
				player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_TUTORIAL_QUESTS, 5000, ScreenMessageAlign.TOP_CENTER));
			return "";
		}
		return null;
	}
}