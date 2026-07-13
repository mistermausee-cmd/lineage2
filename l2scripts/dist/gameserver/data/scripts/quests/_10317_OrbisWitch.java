package quests;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10317_OrbisWitch extends Quest
{
	//npc
	public static final int OPERA = 32946;
	public static final int TIPIA = 32892;

	private static final int EXP_REWARD = 7412805;	private static final int SP_REWARD = 1779; 	public _10317_OrbisWitch()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(OPERA);
		addTalkId(TIPIA);
		
		addQuestCompletedCheck("32946-lvl.htm", 10316);
		addLevelCheck("32946-lvl.htm", 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32946-7.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("32892-1.htm"))
		{
			st.giveItems(57, 506760);
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
		if(npcId == OPERA)
		{
			if(cond == 0)
				return "32946.htm";
			if(cond == 1)
				return "32946-8.htm";		
		}
		if(npcId == TIPIA)
		{
			if(cond == 1)
				return "32892.htm";
		}
		return NO_QUEST_DIALOG;
	}
}