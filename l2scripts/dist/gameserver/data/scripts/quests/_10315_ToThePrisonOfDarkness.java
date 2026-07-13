package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10315_ToThePrisonOfDarkness extends Quest
{
	//npc
	private static final int SLAKI = 32893;
	private static final int OPERA = 32946;

	private static final int EXP_REWARD = 4038093;	private static final int SP_REWARD = 969; 	public _10315_ToThePrisonOfDarkness()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(SLAKI);
		addTalkId(SLAKI);
		addTalkId(OPERA);

		addLevelCheck("32893-lvl.htm", 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32893-5.htm"))
		{
			st.setCond(1);
		}	
		if(event.equalsIgnoreCase("32946-4.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 279513);
			st.giveItems(17526, 1);
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
		if(npcId == SLAKI)
		{
			if(cond == 0)	
				return "32893.htm";
			else if(cond == 1)
				return "32893-6.htm";		
		}
		if(npcId == OPERA)
		{
			if(cond == 1)
				return "32946.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == SLAKI)
			htmltext = "32893-comp.htm";
		return htmltext;
	}
}