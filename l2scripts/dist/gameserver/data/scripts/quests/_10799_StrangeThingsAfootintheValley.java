package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10799_StrangeThingsAfootintheValley extends Quest
{
	// NPC's
	private static final int NAMO = 33973;
	//Mobs
	private static final int[] MONSTERS = { 23423, 23424, 23425, 23427, 23428, 23429, 23436, 23437, 23438, 23439, 23440};
	// Item's
	
	private static final int ENCHANTARMOR = 23417;

	private static final String Dragonvally = "dragonvally";

	private static final int EXP_REWARD = 543080087;	private static final int SP_REWARD = 23435; 	public _10799_StrangeThingsAfootintheValley()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(NAMO);
		addTalkId(NAMO);
		addKillNpcWithLog(1, 579911, Dragonvally, 100, MONSTERS);
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
			st.giveItems(ENCHANTARMOR, 10);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9546, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("water"))
		{
			st.giveItems(ENCHANTARMOR, 10);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9547, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("earth"))
		{
			st.giveItems(ENCHANTARMOR, 10);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9548, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("wind"))
		{
			st.giveItems(ENCHANTARMOR, 10);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9549, 30);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("dark"))
		{
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(9550, 30);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			htmltext = "33973-8.htm";
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("divine"))
		{
			st.giveItems(ENCHANTARMOR, 10);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
				st.unset(Dragonvally);
				st.setCond(2);
			}
		}
		return null;
	}
}