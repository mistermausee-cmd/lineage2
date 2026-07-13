package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class _10522_TheDarkSecretOfVarkaSilenos extends Quest
{
	// NPC's
	private static final int HANSEN = 33853;

	//Монстры
	private static final int[] MOBSW = new int[]{21350, 21351, 21353, 21354, 21358, 21362, 21366, 21369, 21370,  21372,  21374, 21375};
	private static final int[] MOBSM = new int[]{21355, 21357, 21360, 21361, 21364, 21365,  21368, 21371, 21373};

	private static final int ZAPAS_STREL = 27514;
	private static final int ZAPAS_WIZ = 27515;

	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";


	private static final int EXP_REWARD = 492760460;
	private static final int SP_REWARD = 5519;

	public _10522_TheDarkSecretOfVarkaSilenos()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(MOBSW);
		addKillId(MOBSM);
		addKillNpcWithLog(1, 1027514, A_LIST, 100, ZAPAS_STREL);
		addKillNpcWithLog(1, 1027515, B_LIST, 100, ZAPAS_WIZ);
		addRaceCheck("hansen_q10522_02a.htm", Race.ERTHEIA);
		addLevelCheck("hansen_q10522_02a.htm", 76/*, 80*/);
		addClassTypeCheck("hansen_q10522_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("hansen_q10522_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("hansen_q10522_08.htm"))
		{
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
			case HANSEN:
				if(cond == 0)
					htmltext = "hansen_q10522_01.htm";
				else if (cond == 1)
					htmltext = "hansen_q10522_06.htm";
				else if (cond == 2)
					htmltext = "hansen_q10522_07.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if (ArrayUtils.contains(MOBSW, npc.getNpcId()))
			{
				NpcInstance scout = qs.addSpawn(ZAPAS_STREL, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 360000);
				scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
			}
			else if (ArrayUtils.contains(MOBSM, npc.getNpcId()))
			{
				NpcInstance scout = qs.addSpawn(ZAPAS_WIZ, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 360000);
				scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
			}

			else if (updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.unset(B_LIST);
				qs.setCond(2);
			}
		}
		return null;
	}
}