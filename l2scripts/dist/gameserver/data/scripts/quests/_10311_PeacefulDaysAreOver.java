package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10311_PeacefulDaysAreOver extends Quest
{
	//npc
	private static final int SELINA = 33032;
	private static final int SLAKI = 32893;

	private static final int EXP_REWARD = 7168395;	private static final int SP_REWARD = 1720; 	public _10311_PeacefulDaysAreOver()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addTalkId(SLAKI);
		addLevelCheck("33032-lvl.htm", 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33032-5.htm"))
		{
			st.setCond(1);
		}	
		if(event.equalsIgnoreCase("32893-4.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 489220);
			st.finishQuest();	
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == SELINA)
		{
			if(cond == 0)
				return "33032.htm";
			else if(cond == 1)
				return "33032-6.htm";		
		}
		if(npcId == SLAKI)
		{
			if(cond == 1)
				return "32893.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == SELINA)
			htmltext = "33032-comp.htm";
		return htmltext;
	}
}