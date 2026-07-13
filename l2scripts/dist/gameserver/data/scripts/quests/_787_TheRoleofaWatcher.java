package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class _787_TheRoleofaWatcher extends Quest
{
	// NPC's
	private static final int NAMO = 33973;

	// Monster's
	private static final int[] MONSTERS = { 23423, 23424, 23425, 23427, 23428, 23429, 23436, 23437, 23438, 23439, 23440, 23430, 23431, 23432, 23433, 23441, 23442, 23443, 23444,
	};

	// Item's
	private static final int BONEPART = 39736;
	private static final int BONEFRAGMENT = 39737;


	public _787_TheRoleofaWatcher()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(NAMO);
		addKillId(MONSTERS);
		addQuestItem(BONEPART, BONEFRAGMENT);
		addLevelCheck("33973-0.htm", 76/*, 85*/);
		addRaceCheck("33973-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33973-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33973-9.htm"))
		{
			st.takeItems(BONEPART, -1);
			if(st.getQuestItemsCount(BONEFRAGMENT) < 100)
			{
				st.giveItems(39728, 1);
				st.addExpAndSp(14140350, 3393);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 100 && st.getQuestItemsCount(BONEFRAGMENT) <= 199)
			{
				st.giveItems(39728, 2);
				st.addExpAndSp(28280700, 6786);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 200 && st.getQuestItemsCount(BONEFRAGMENT) <= 299)
			{
				st.giveItems(39728, 3);
				st.addExpAndSp(42421050, 10179);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 300 && st.getQuestItemsCount(BONEFRAGMENT) <= 399)
			{
				st.giveItems(39728, 4);
				st.addExpAndSp(56561400, 13572);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 400 && st.getQuestItemsCount(BONEFRAGMENT) <= 499)
			{
				st.giveItems(39728, 5);
				st.addExpAndSp(70701750, 16965);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 500 && st.getQuestItemsCount(BONEFRAGMENT) <= 599)
			{
				st.giveItems(39728, 6);
				st.addExpAndSp(84842100, 20358);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 600 && st.getQuestItemsCount(BONEFRAGMENT) <= 699)
			{
				st.giveItems(39728, 7);
				st.addExpAndSp(98982450, 23751);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 700 && st.getQuestItemsCount(BONEFRAGMENT) <= 799)
			{
				st.giveItems(39728, 8);
				st.addExpAndSp(113122800, 27144);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 800 && st.getQuestItemsCount(BONEFRAGMENT) <= 899)
			{
				st.giveItems(39728, 9);
				st.addExpAndSp(127263150, 30537);
			}
			else if(st.getQuestItemsCount(BONEFRAGMENT) >= 900)
			{
				st.giveItems(39728, 10);
				st.addExpAndSp(141403500, 33930);
			}
			st.takeItems(BONEFRAGMENT, -1);
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
			case 33973:
				if(cond == 0)
					htmltext = "33973-1.htm";
				else if(cond == 1)
					htmltext = "33973-5.htm";
				else if(cond == 2 || cond == 3)
					htmltext = "33973-6.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == 33973)
			htmltext = "33973-10.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(BONEPART, 1, 1, 50, 10))
				st.setCond(2);
		}
		else if(cond == 2)
		{
			if(st.rollAndGive(BONEFRAGMENT, 1, 1, 900, 20))
				st.setCond(3);
		}
		return null;
	}
}