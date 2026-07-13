package quests;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10351_OwnerOfHall extends Quest
{
	//npc
	private static final int TIPIA_NORMAL = 32892;

	private static final int EXP_REWARD = 897850000;	private static final int SP_REWARD = 215484; 	public _10351_OwnerOfHall()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(TIPIA_NORMAL);
		addTalkId(TIPIA_NORMAL);
		addKillId(29194, 29212); //octavius
		
		addQuestCompletedCheck("32892-lvl.htm", 10318);
		addLevelCheck("32892-lvl.htm", 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32892-7.htm"))
		{
			st.setCond(1);
		}	
		if(event.equalsIgnoreCase("32892-10.htm"))
		{
			st.giveItems(57, 23655000);
			st.giveItems(19461, 1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		if(npcId == TIPIA_NORMAL)
		{
			if(cond == 0)
				return "32892.htm";
			if(cond == 1)
				return "32892-8.htm";	
			if(cond == 2)				
				return "32892-9.htm";
		}
		return NO_QUEST_DIALOG;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		switch(npc.getNpcId())
		{
			case 29194:
			case 29212:
				if(st.getCond() == 1)
					st.setCond(2);
				break;
		}
		return null;
	}	
}