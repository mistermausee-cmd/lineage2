package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _744_TheAlligatorHunterReturns extends Quest
{
	private static final int BATIS = 30332;
	private static final int ELON = 33860;
	private static final int FLATER = 30677;

	private static final int LETHER = 47046;

	private static final int[] MOBS = {20135, 20804, 20805, 20806, 20807, 20808, 20991, 20992, 20993};

	private static final long EXP_REWARD = 7574218;
	private static final int SP_REWARD = 1380;

	public _744_TheAlligatorHunterReturns()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(BATIS);
		addTalkId(BATIS, ELON, FLATER);
		addKillId(MOBS);
		addQuestItem(LETHER);
		addLevelCheck("captain_bathia_q0744_02.htm", 40, 45);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("captain_bathia_q0744_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("enrone_q0744_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("head_blacksmith_flutter_q0744_03.htm"))
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
			case BATIS:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "captain_bathia_q0744_03.htm";
					else
						htmltext = "captain_bathia_q0744_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_bathia_q0744_07.htm";
				break;

			case ELON:
				if (cond == 1)
					htmltext = "enrone_q0744_01.htm";
				else if (cond == 2)
					htmltext = "enrone_q0744_04.htm";
				else if (cond == 3)
					htmltext = "enrone_q0744_05.htm";
				break;

			case FLATER:
				if (cond == 2)
					htmltext = "head_blacksmith_flutter_q0744_01.htm";
				else if (cond == 3)
					htmltext = "head_blacksmith_flutter_q0744_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(LETHER, 1, 1, 150, 100))
				qs.setCond(3);
		}
		return null;
	}
}
