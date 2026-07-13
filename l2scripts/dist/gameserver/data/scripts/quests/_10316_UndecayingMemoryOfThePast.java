package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10316_UndecayingMemoryOfThePast extends Quest
{
	public static final String A_LIST = "a_list";
	//npc
	private static final int OPERA = 32946;

	private static final int EXP_REWARD = 54093924;	private static final int SP_REWARD = 12982; 	public _10316_UndecayingMemoryOfThePast()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(OPERA);
		addTalkId(OPERA);
		addKillNpcWithLog(1, A_LIST, 1, 25779);
		addLevelCheck("32946-lvl.htm", 90);
		addQuestCompletedCheck("32946-lvl.htm", 10315);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32946-5.htm"))
		{
			st.setCond(1);
		}		
		if(event.startsWith("give"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();				
			if(event.equalsIgnoreCase("givegiants"))
			{
				st.giveItems(19305, 1);
				st.giveItems(19306, 1);
				st.giveItems(19307, 1);
				st.giveItems(19308, 1);
				return "32946-9.htm";
			}
			if(event.equalsIgnoreCase("givescrolls"))
			{
				st.giveItems(17527, 2);
				return "32946-10.htm";
			}			
			if(event.equalsIgnoreCase("givesacks"))
			{
				st.giveItems(34861, 2);
				return "32946-11.htm";
			}				
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
			else if(cond == 1)
				return "32946-6.htm";	
			else if(cond == 2)		
				return "32946-7.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == OPERA)
			htmltext = "32946-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}

		return null;
	}	
}