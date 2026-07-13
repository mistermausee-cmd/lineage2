package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10394_MutualBenefit extends Quest
{
	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";
	public static final String C_LIST = "C_LIST";
	//npc
	private static final int KELIOS = 33862;
	//mob
	private static final int MOB1 = 20241;
	private static final int MOB2 = 20573;
	private static final int MOB3 = 20574;
	
	private static final int EXP_REWARD = 6758129;	private static final int SP_REWARD = 756; 	public _10394_MutualBenefit()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(KELIOS);
		addTalkId(KELIOS);
		addKillNpcWithLog(1, A_LIST, 15, MOB1);
		addKillNpcWithLog(1, B_LIST, 20, MOB2);
		addKillNpcWithLog(1, C_LIST, 20, MOB3);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("no_level.htm", 46/*, 52*/);
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
			st.giveItems(952, 6);
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
		
		if(npcId == KELIOS)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 2)
				htmltext = "4.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.unset(C_LIST);
			qs.setCond(2);
		}
		return null;
	}
}