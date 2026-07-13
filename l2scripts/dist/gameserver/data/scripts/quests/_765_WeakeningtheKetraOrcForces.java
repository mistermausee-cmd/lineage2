package quests;

import l2s.gameserver.model.base.ClassType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.HashMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _765_WeakeningtheKetraOrcForces extends Quest
{
	//npc
	private static final int ANDREY = 31292;
	private static final int RUGONESS = 33852;
	private static final int MAXIM = 30120;
	//q items
	private static final int SOLDAT_SIGN = 36676;
	private static final int GENERAL_SIGN = 36677;

	private static final int[] MOBS = {21324, 21327, 21328, 21329, 21331, 21332, 21334, 21336, 21338, 21339, 21340, 21342, 21343, 21345, 21347};

	private static final long EXP_REWARD = 1117137434l;
	private static final int SP_REWARD = 9503;

	public _765_WeakeningtheKetraOrcForces()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(ANDREY);
		addTalkId(ANDREY, RUGONESS, MAXIM);
		addKillId(MOBS);
		addQuestItem(SOLDAT_SIGN);
		addQuestItem(GENERAL_SIGN);
		addLevelCheck("captain_andrei_q0765_02.htm", 76, 80);
		addClassTypeCheck("captain_andrei_q0765_02.htm", ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("captain_andrei_q0765_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("rugoness_q0765_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("maximilian_q0765_03.htm"))
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
		switch (npcId)
		{
			case ANDREY:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "captain_andrei_q0765_03.htm";
					else
						htmltext = "captain_andrei_q0765_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_andrei_q0765_07.htm";
				break;

			case RUGONESS:
				if (cond == 1)
					htmltext = "rugoness_q0765_01.htm";
				else if (cond == 2)
					htmltext = "rugoness_q0765_04.htm";
				else if (cond == 3)
					htmltext = "rugoness_q0765_05.htm";
				break;

			case MAXIM:
				if (cond == 2)
					htmltext = "maximilian_q0765_01.htm";
				else if (cond == 3)
					htmltext = "maximilian_q0765_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			qs.rollAndGive(SOLDAT_SIGN, 1, 1, 200, 40);
			qs.rollAndGive(GENERAL_SIGN, 1, 1, 200, 40);
			if(qs.getQuestItemsCount(SOLDAT_SIGN) >= 200 && qs.getQuestItemsCount(GENERAL_SIGN) >= 200)
				qs.setCond(3);
		}
		return null;
	}
}
