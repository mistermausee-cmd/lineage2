package quests;

import l2s.gameserver.model.base.ClassType;
import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
public class _761_AssistingTheGoldenRamArmy extends Quest
{
    private static final int MATIAS = 31340;
    private static final int DOKARA = 33847;
    private static final int ANDREY = 31292;
    private static final int FIRST_QUEST_ITEM_ID = 36668;
    private static final int SECOND_QUEST_ITEM_ID = 36669;
    private static final int [] MOBS = new int[]{21508, 21509, 21510, 21511, 21512, 21513, 21514, 21515, 21516, 21517, 21518, 21519};

	private static final int EXP_REWARD = 157490428;
	private static final int SP_REWARD = 4524; 

	public _761_AssistingTheGoldenRamArmy()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(MATIAS);
		addTalkId(MATIAS, DOKARA, ANDREY);
		addQuestItem(FIRST_QUEST_ITEM_ID);
		addQuestItem(SECOND_QUEST_ITEM_ID);
		
		addKillId(MOBS);
		addLevelCheck("captain_mathias_q0761_02", 65, 70);
		addClassTypeCheck("captain_mathias_q0761_02", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("captain_mathias_q0761_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("chaser_dokara_q0761_03.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("chaser_dokara_q0761_05.htm"))
		{
			return null;
		}
		if(event.equalsIgnoreCase("captain_andrei_q0761_03.htm"))
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
			case MATIAS:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "captain_mathias_q0761_03.htm";
					else
						htmltext = "captain_mathias_q0761_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_mathias_q0761_07.htm";
				break;

			case DOKARA:
				if (cond == 1)
					htmltext = "chaser_dokara_q0761_01.htm";
				else if (cond == 2)
					htmltext = "chaser_dokara_q0761_04.htm";
				else if (cond == 3)
					htmltext = "chaser_dokara_q0761_05.htm";
				break;

			case ANDREY:
				if (cond == 2)
					htmltext = "captain_andrei_q0761_01.htm";
				else if (cond == 3)
					htmltext = "captain_andrei_q0761_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			qs.rollAndGive(FIRST_QUEST_ITEM_ID, 1, 1, 200, 40);
			qs.rollAndGive(SECOND_QUEST_ITEM_ID, 1, 1, 200, 40);
			if(qs.getQuestItemsCount(FIRST_QUEST_ITEM_ID) >= 200 && qs.getQuestItemsCount(SECOND_QUEST_ITEM_ID) >= 200)
				qs.setCond(3);
		}
		return null;
	}
}