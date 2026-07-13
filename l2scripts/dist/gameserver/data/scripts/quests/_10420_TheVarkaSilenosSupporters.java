package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class _10420_TheVarkaSilenosSupporters extends Quest
{
	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";

	//Квестовые персонажи
	private static final int HANSEN = 33853;

	//Монстры
	private static final int[] MOBSW = new int[]{21350, 21351, 21353, 21354, 21358, 21362, 21366, 21369, 21370,  21372,  21374, 21375};
	private static final int[] MOBSM = new int[]{21355, 21357, 21360, 21361, 21364, 21365,  21368, 21371, 21373};
	private static final int ZAPAS_STREL = 27514;
	private static final int ZAPAS_WIZ = 27515;

	private static final int EXP_REWARD = 492760460;
	private static final int SP_REWARD = 5519; 

	public _10420_TheVarkaSilenosSupporters()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillNpcWithLog(1, A_LIST, 100, ZAPAS_STREL);
		addKillNpcWithLog(1, B_LIST, 100, ZAPAS_WIZ);
		addKillId(MOBSW);
		addKillId(MOBSM);
		addLevelCheck("hansen_q10420_02.htm", 76/*, 80*/);
		addRaceCheck("hansen_q10420_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addClassTypeCheck("hansen_q10420_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hansen_q10420_05.htm"))
		{
			st.setCond(1);
			
		}
		if(event.equalsIgnoreCase("hansen_q10420_08.htm"))
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
					htmltext = "hansen_q10420_01.htm";
				else if (cond == 1)
					htmltext = "hansen_q10420_06.htm";
				else if (cond == 2)
					htmltext = "hansen_q10420_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(ArrayUtils.contains(MOBSW, npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(ZAPAS_STREL, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
		}
		else if(ArrayUtils.contains(MOBSM, npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(ZAPAS_WIZ, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
		}

		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.setCond(2);
		}
		return null;
	}
}