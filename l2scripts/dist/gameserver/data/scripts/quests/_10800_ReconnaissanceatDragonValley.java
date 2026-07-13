package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10800_ReconnaissanceatDragonValley extends Quest
{
	// NPC's
	private static final int NAMO = 33973;
	//Mobs
	private static final int[] MONSTERS = { 23430, 23431, 23432, 23433, 23441, 23442, 23443, 23444 };
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23417;
	private static final int ENCHANTWEAPON = 23411;

	private static final String Dragonvally1 = "dragonvally1";

	public _10800_ReconnaissanceatDragonValley()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(NAMO);
		addTalkId(NAMO);
		addKillNpcWithLog(1, 580011, Dragonvally1, 100, MONSTERS);
		addQuestCompletedCheck("33973-0.htm", 10799);
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
		else if(event.equalsIgnoreCase("fire"))
		{
			st.giveItems(DOORCOIN, 235);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(ENCHANTWEAPON, 1);
			st.addExpAndSp(84722400, 20333);
			st.giveItems(9546, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("water"))
		{
			st.giveItems(DOORCOIN, 235);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(ENCHANTWEAPON, 1);
			st.addExpAndSp(84722400, 20333);
			st.giveItems(9547, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("earth"))
		{
			st.giveItems(DOORCOIN, 235);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(ENCHANTWEAPON, 1);
			st.addExpAndSp(84722400, 20333);
			st.giveItems(9548, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("wind"))
		{
			st.giveItems(DOORCOIN, 235);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(ENCHANTWEAPON, 1);
			st.addExpAndSp(84722400, 20333);
			st.giveItems(9549, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("dark"))
		{
			st.giveItems(DOORCOIN, 235);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(ENCHANTWEAPON, 1);
			st.giveItems(9550, 30);
			st.addExpAndSp(84722400, 20333);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("divine"))
		{
			st.giveItems(DOORCOIN, 235);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(ENCHANTWEAPON, 1);
			st.addExpAndSp(84722400, 20333);
			st.giveItems(9551, 30);
			htmltext = "33973-8.htm";
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
			case NAMO:
				if(cond == 0)
					htmltext = "33973-1.htm";
				else if (cond == 1)
					htmltext = "33973-5.htm";
				else if (cond == 2)
					htmltext = "33973-6.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(Dragonvally1);
				st.setCond(2);
			}
		}
		return null;
	}
}