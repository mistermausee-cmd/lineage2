package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _764_WeakeningtheVarkaSilenosForces extends Quest
{
	//Npc
	private static final int ANDREY = 31292;
	private static final int HANSEN = 33853;
	private static final int MAXIM = 30120;

	private static final int SOLDAT_SIGN = 36674;
	private static final int GENERAL_SIGN = 36675;

	private static final int[] MOBS = {21350, 21351, 21353, 21354, 21355, 21357, 21358, 21360, 21362, 21364, 21365, 21366, 21368, 21369, 21371, 21373};

	private static final long EXP_REWARD = 1117137434l;
	private static final int SP_REWARD = 9503; 

	public _764_WeakeningtheVarkaSilenosForces()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(ANDREY);
		addTalkId(ANDREY, HANSEN, MAXIM);
		addKillId(MOBS);
		addQuestItem(SOLDAT_SIGN);
		addQuestItem(GENERAL_SIGN);	
		addLevelCheck("captain_andrei_q0764_02.htm", 76, 80);
		addClassTypeCheck("captain_andrei_q0764_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("captain_andrei_q0764_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("hansen_q0764_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("maximilian_q0764_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(SOLDAT_SIGN, -1);
			st.takeItems(GENERAL_SIGN, -1);
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
						htmltext = "captain_andrei_q0764_03.htm";
					else
						htmltext = "captain_andrei_q0764_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_andrei_q0764_07.htm";
				break;

			case HANSEN:
				if (cond == 1)
					htmltext = "hansen_q0764_01.htm";
				else if (cond == 2)
					htmltext = "hansen_q0764_04.htm";
				else if (cond == 3)
					htmltext = "hansen_q0764_05.htm";
				break;

			case MAXIM:
				if (cond == 2)
					htmltext = "maximilian_q0764_01.htm";
				else if (cond == 3)
					htmltext = "maximilian_q0764_02.htm";
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
