package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _763_ADauntingTask extends Quest
{
    private static final int ANDREI = 31292;
    private static final int YANIT = 33851;
    private static final int[] Mobs = new int[] {21294, 21295, 21296, 21297,21298, 21299, 21300, 21301, 21302, 21303, 21304,
			21305, 21307};
    private static final int EYEOFDARKNESS = 36672;
    private static final int MALICE= 36673;

	private static final long EXP_REWARD = 474767890l;
	private static final int SP_REWARD = 5026; 

	public _763_ADauntingTask()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(ANDREI);
		addTalkId(YANIT, ANDREI);
		addQuestItem(EYEOFDARKNESS);
		addQuestItem(MALICE);
		
		addKillId(Mobs);
		addLevelCheck("captain_andrei_q0763_02.htm", 70, 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("captain_andrei_q0763_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("yanit_q0763_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("captain_andrei_q0763_10.htm"))
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
			case ANDREI:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "captain_andrei_q0763_03.htm";
					else
						htmltext = "captain_andrei_q0763_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_andrei_q0763_07.htm";
				else if (cond == 2)
					htmltext = "captain_andrei_q0763_08.htm";
				else if (cond == 3)
					htmltext = "captain_andrei_q0763_09.htm";
				break;

			case YANIT:
				if (cond == 1)
					htmltext = "yanit_q0763_01.htm";
				else if (cond == 2)
					htmltext = "yanit_q0763_04.htm";
				else if (cond == 3)
					htmltext = "yanit_q0763_05.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			qs.rollAndGive(EYEOFDARKNESS, 1, 1, 200, 40);
			qs.rollAndGive(MALICE, 1, 1, 200, 40);
			if(qs.getQuestItemsCount(EYEOFDARKNESS) >= 200 && qs.getQuestItemsCount(MALICE) >= 200)
				qs.setCond(3);
		}
		return null;
	}
}