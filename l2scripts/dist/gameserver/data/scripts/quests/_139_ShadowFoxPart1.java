package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _139_ShadowFoxPart1 extends Quest
{
	// NPC
	private final static int MIA = 30896;

	// Items
	private final static int FRAGMENT = 10345;
	private final static int CHEST = 10346;

	// Monsters
	private final static int TasabaLizardman1 = 20784;
	private final static int TasabaLizardman2 = 21639;
	private final static int TasabaLizardmanShaman1 = 20785;
	private final static int TasabaLizardmanShaman2 = 21640;

	private static final int EXP_REWARD = 30000;	private static final int SP_REWARD = 7; 				public _139_ShadowFoxPart1()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(MIA);
		addTalkId(MIA);
		addQuestItem(FRAGMENT, CHEST);
		addKillId(TasabaLizardman1, TasabaLizardman2, TasabaLizardmanShaman1, TasabaLizardmanShaman2);
		addLevelCheck("30896-00.htm", 37);
		addQuestCompletedCheck("30896-00.htm", 138);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("30896-03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30896-11.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("30896-14.htm"))
		{
			st.takeItems(FRAGMENT, -1);
			st.takeItems(CHEST, -1);
			st.set("talk", "1");
		}
		else if(event.equalsIgnoreCase("30896-16.htm"))
		{
			st.giveItems(ADENA_ID, 14050);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if(npcId == MIA)
			if(cond == 0)
				htmltext = "30896-01.htm";
			else if(cond == 1)
				htmltext = "30896-03.htm";
			else if(cond == 2)
				if(st.getQuestItemsCount(FRAGMENT) >= 10 && st.getQuestItemsCount(CHEST) >= 1)
					htmltext = "30896-13.htm";
				else if(st.getInt("talk") == 1)
					htmltext = "30896-14.htm";
				else
					htmltext = "30896-12.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 2)
		{
			st.giveItems(FRAGMENT, 1);
			st.playSound(SOUND_ITEMGET);
			if(Rnd.chance(10))
				st.giveItems(CHEST, 1);
		}
		return null;
	}
}