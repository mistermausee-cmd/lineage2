package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _642_APowerfulPrimevalCreature extends Quest
{
	// NPCs
	private static final int Dinn = 32105;
	// Mobs
	private static final int Ancient_Egg = 18344;
	private static final int[] Dino = {
			22196,
			22197,
			22198,
			22199,
			22200,
			22201,
			22202,
			22203,
			22204,
			22205,
			22218,
			22219,
			22220,
			22223,
			22224,
			22225,
			22226,
			22227,
			22742,
			22743,
			22744,
			22745
	};
	// Items
	private static final int[] Rewards = {
			8690,
			8692,
			8694,
			8696,
			8698,
			8700,
			8702,
			8704,
			8706,
			8708,
			8710
	};
	// Quest Items
	private static final int Dinosaur_Tissue = 8774;
	private static final int Dinosaur_Egg = 8775;
	// Chances
	private static final int Dinosaur_Tissue_Chance = 33;
	private static final int Dinosaur_Egg_Chance = 1;

	public _642_APowerfulPrimevalCreature()
	{
		super(PARTY_ALL, REPEATABLE);
		addStartNpc(Dinn);
		addKillId(Ancient_Egg);
		for(int dino_id : Dino)
			addKillId(dino_id);
		addQuestItem(Dinosaur_Tissue);
		addQuestItem(Dinosaur_Egg);
		addLevelCheck("dindin_q0642_01a.htm", 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		long Dinosaur_Tissue_Count = st.getQuestItemsCount(Dinosaur_Tissue);
		if(event.equalsIgnoreCase("dindin_q0642_04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("dindin_q0642_12.htm"))
		{
			if(Dinosaur_Tissue_Count == 0)
				return "dindin_q0642_08a.htm";
			st.takeItems(Dinosaur_Tissue, -1);
			st.giveItems(ADENA_ID, Dinosaur_Tissue_Count * 3000, false);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("0"))
			return null;
		else
		{
			try
			{
				int rew_id = Integer.valueOf(event);
				if(Dinosaur_Tissue_Count < 150 || st.getQuestItemsCount(Dinosaur_Egg) == 0)
					return "dindin_q0642_08a.htm";
				for(int reward : Rewards)
					if(reward == rew_id)
					{
						st.takeItems(Dinosaur_Tissue, 150);
						st.takeItems(Dinosaur_Egg, 1);
						st.giveItems(reward, 1, false);
						st.giveItems(ADENA_ID, 44000, false);
						st.playSound(SOUND_MIDDLE);
						return "dindin_q0642_12.htm";
					}
				return null;
			}
			catch(Exception E)
			{
			}
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case Dinn:
				if(cond == 0)
					htmltext = "dindin_q0642_01.htm";
				else if(cond == 1)
				{
					long Dinosaur_Tissue_Count = st.getQuestItemsCount(Dinosaur_Tissue);
					if(Dinosaur_Tissue_Count == 0)
						htmltext = "dindin_q0642_08a.htm";
					else if(Dinosaur_Tissue_Count < 150 || st.getQuestItemsCount(Dinosaur_Egg) == 0)
						htmltext = "dindin_q0642_07.htm";
					else
						htmltext = "dindin_q0642_07a.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(!st.isStarted() || st.getCond() != 1)
			return null;
		if(npc.getNpcId() == Ancient_Egg)
			st.rollAndGive(Dinosaur_Egg, 1, Dinosaur_Egg_Chance);
		else
			st.rollAndGive(Dinosaur_Tissue, 1, Dinosaur_Tissue_Chance);
		return null;
	}

}