package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Bonux
**/
public final class _789_WaitingForPaagrio extends Quest
{
	// NPC's
	private static final int HARP_ZU_HESTUI = 34014; // Хариф Зу Хестуи

	// Item's
	private static final int HARPS_REWARD_BOX = 46431; // Наградной Сундук Харифа

	// Quest Item's
	private static final int MAGMA_ORE = 45449; // Магмовый Кристалл

	// Monster's
	private static final int[] HUNT_LIST = {
		23487,	// Магмовый Айрис
		23488,	// Магмовый Апофис
		// Лавовый Змей
		23490,	// Лавовый Дрейк
		23491,	// Лавовый Вендиго
		// Голем из Лавы
		23493,	// Лавовый Ривиа
		23494,	// Магмовая Саламандра
		23495,	// Магмовый Дре Ванул
		23496,	// Магмовый Ифрит
		23497,	// Пылающий Лавазавр
		23498,	// Пылающий Чародей
		23499,	// Пылающий Фрета
		23500,	// Пылающий Коготь
		23501,	// Пылающий Раэль
		23502,	// Пылающая Саламандра
		23503,	// Пылающий Дрейк
		23504	// Пылающий Ботис
	};

	// Other
	private static final double MAGMA_ORE_DROP_CHANCE = 50;

	public _789_WaitingForPaagrio()
	{
		super(PARTY_NONE, DAILY);

		addStartNpc(HARP_ZU_HESTUI);
		addKillId(HUNT_LIST);
		addQuestItem(MAGMA_ORE);
		addLevelCheck("34014_00.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("34014_02.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("34014_05.htm"))
		{
			long magmaOreCount = st.getQuestItemsCount(MAGMA_ORE);
			st.takeItems(MAGMA_ORE, -1);
			if(magmaOreCount >= 100 && magmaOreCount < 200)
			{
				st.giveItems(HARPS_REWARD_BOX, 1);
				st.addExpAndSp(3015185490L, 7236360L);
			}
			else if(magmaOreCount >= 200 && magmaOreCount < 300)
			{
				st.giveItems(HARPS_REWARD_BOX, 2);
				st.addExpAndSp(6030370980L, 14472720L);
			}
			else if(magmaOreCount >= 300 && magmaOreCount < 400)
			{
				st.giveItems(HARPS_REWARD_BOX, 3);
				st.addExpAndSp(9045556470L, 21709080L);
			}
			else if(magmaOreCount >= 400)
			{
				st.giveItems(HARPS_REWARD_BOX, 4);
				st.addExpAndSp(12060741960L, 28945440L);
			}
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("34014_06.htm"))
		{
			if(st.getCond() == 1)
				st.setCond(2);
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
			case HARP_ZU_HESTUI:
				if(cond == 0)
					htmltext = "34014_01.htm";
				else if(cond == 1)
				{
					if(st.haveQuestItem(MAGMA_ORE, 100))
						htmltext = "34014_04.htm";
					else
						htmltext = "34014_03.htm";
				}
				else if(cond == 2)
					htmltext = "34014_04.htm";
				else if(cond == 3)
					htmltext = "34014_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == HARP_ZU_HESTUI)
			htmltext = "34014_00a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(MAGMA_ORE, 20, 20, 100, MAGMA_ORE_DROP_CHANCE))
				st.playSound(SOUND_MIDDLE);
		}
		else if(cond == 2)
		{
			if(st.rollAndGive(MAGMA_ORE, 100, 100, 400, MAGMA_ORE_DROP_CHANCE))
				st.setCond(3);
		}
		return null;
	}
}