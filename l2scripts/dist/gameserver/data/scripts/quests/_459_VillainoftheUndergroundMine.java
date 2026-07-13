package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _459_VillainoftheUndergroundMine extends Quest
{
	private static final int Filaur = 30535; //ok
	private static final int Teredor = 25785; //ok
	private static final int POF = 19450;

	public _459_VillainoftheUndergroundMine() 
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(Filaur);
		addTalkId(Filaur);
		addKillId(Teredor);
		addLevelCheck("nolvl.htm", 85);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		int cond = st.getCond();
		if(event.equalsIgnoreCase("30535-04.htm")) 
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("30535-07.htm")) 
		{
			if(cond == 2) 
			{
				st.giveItems(POF, 6);
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
		if(npcId == Filaur) 
		{
			if(cond == 0) 
				htmltext = "30535-00.htm";
			else if(cond == 1) 
				htmltext = "30535-04.htm";
			else if(cond == 2) 
				htmltext = "30535-06.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Filaur)
			htmltext = "notnow.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == Teredor) 
		{
			st.setCond(2);
		}
		return null;
	}
}