package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10395_NotATraitor extends Quest
{
	//npc
	private static final int LEO = 33863;
	private static final int KELIOS = 33862;
	//mob
	private static final int[] MOBS = { 20161, 20575, 20576, 20261 };
	//rewards
	
	private static final String A_LIST = "A_LIST";

	private static final int EXP_REWARD = 8419210;	private static final int SP_REWARD = 907; 	public _10395_NotATraitor()
	{
		super(PARTY_ALL, ONETIME);
		
		addStartNpc(LEO);
		addTalkId(LEO);
		addTalkId(KELIOS);
		addKillId(MOBS);
		addKillNpcWithLog(1, 539511, A_LIST, 50, MOBS);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("no_level.htm", 46/*, 52*/);
		addQuestCompletedCheck("no_level.htm", 10394);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npcId == LEO)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 2)
				htmltext = "4.htm";
		}
		else if(npcId == KELIOS)
		{
			if(cond == 2)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			if (st.getCond() == 1)
			{
				st.unset(A_LIST);
				st.setCond(2);
			}
		}
		return null;
	}
}