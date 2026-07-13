package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _460_PreciousResearchMaterial extends Quest
{
	private static final int Amer = 33092; // ok
	private static final int Filar = 30535;
	private static final int Egg = 18997; //
	private static final int POL = 19450; //
	private static final int PART_EGG = 17735;

	public _460_PreciousResearchMaterial() 
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(Amer);
		addTalkId(Filar);
		addQuestItem(PART_EGG);
		addKillId(Egg);
		addLevelCheck("no-lvl.htm", 85);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		int cond = st.getCond();
		if(event.equalsIgnoreCase("4.htm")) 
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("reward")) 
		{
			if(cond == 2) 
			{
				htmltext = "finish.htm";
				st.takeItems(PART_EGG, -1);
				st.giveItems(POL, 3);
				st.finishQuest();
			}	
		}	
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Amer) 
		{
			if(cond == 0) 
				htmltext = "started.htm";
			else if(cond == 1) 
				htmltext = "taken.htm";
			else if(cond == 2)
				return "cond2.htm";
		}
		else if(npcId == Filar)
		{
			if(cond == 2) 
				htmltext = "con_quest.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Amer)
			htmltext = "no_avaliable.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();

		if(cond == 1 && st.getQuestItemsCount(PART_EGG) < 20)
		{
			st.giveItems(PART_EGG, 1);
		}
		else if(cond == 1 && st.getQuestItemsCount(PART_EGG) >= 20)
		{
			st.giveItems(PART_EGG, 1);
			st.setCond(2);
		}
		return null;
	}
}