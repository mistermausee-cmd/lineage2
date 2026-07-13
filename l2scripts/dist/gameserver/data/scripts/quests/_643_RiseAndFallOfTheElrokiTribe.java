package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _643_RiseAndFallOfTheElrokiTribe extends Quest
{
	private static int DROP_CHANCE = 75;
	private static int BONES_OF_A_PLAINS_DINOSAUR = 8776;

	private static int[] PLAIN_DINOSAURS = {
			22208,
			22209,
			22210,
			22211,
			22212,
			22213,
			22221,
			22222,
			22226,
			22227,
			22742,
			22743,
			22744,
			22745
	};
	private static int[] REWARDS = {
			8712,
			8713,
			8714,
			8715,
			8716,
			8717,
			8718,
			8719,
			8720,
			8721,
			8722
	};

	public _643_RiseAndFallOfTheElrokiTribe()
	{
		super(PARTY_ALL, REPEATABLE);

		addStartNpc(32106);
		addTalkId(32117);

		for(int npc : PLAIN_DINOSAURS)
			addKillId(npc);

		addQuestItem(BONES_OF_A_PLAINS_DINOSAUR);
		addLevelCheck("singsing_q0643_04.htm", 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		long count = st.getQuestItemsCount(BONES_OF_A_PLAINS_DINOSAUR);
		if(event.equalsIgnoreCase("singsing_q0643_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("shaman_caracawe_q0643_06.htm"))
		{
			if(count >= 300)
			{
				st.takeItems(BONES_OF_A_PLAINS_DINOSAUR, 300);
				st.giveItems(REWARDS[Rnd.get(REWARDS.length)], 5, false);
			}
			else
				htmltext = "shaman_caracawe_q0643_05.htm";
		}
		else if(event.equalsIgnoreCase("None"))
			return null;
		else if(event.equalsIgnoreCase("Quit"))
		{
			st.finishQuest();
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case 32106:
				if(cond == 0)
					htmltext = "singsing_q0643_01.htm";
				else if(cond == 1)
				{
					long count = st.getQuestItemsCount(BONES_OF_A_PLAINS_DINOSAUR);
					if(count == 0)
						htmltext = "singsing_q0643_08.htm";
					else
					{
						htmltext = "singsing_q0643_08.htm";
						st.takeItems(BONES_OF_A_PLAINS_DINOSAUR, -1);
						st.giveItems(ADENA_ID, count * 1374, false);
					}
				}
				break;
			case 32117:
				if(cond == 1)
					htmltext = "shaman_caracawe_q0643_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.rollAndGive(BONES_OF_A_PLAINS_DINOSAUR, 1, DROP_CHANCE);
		return null;
	}
}