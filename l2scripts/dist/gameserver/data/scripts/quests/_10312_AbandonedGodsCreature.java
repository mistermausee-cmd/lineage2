package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10312_AbandonedGodsCreature extends Quest
{
	public static final String A_LIST = "a_list";
	//npc
	private static final int GOFINA = 33031;
	private static final int[] AFROS = {25866, 25775};

	private static final int EXP_REWARD = 46847289;	private static final int SP_REWARD = 11243; 	public _10312_AbandonedGodsCreature()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(GOFINA);
		addTalkId(GOFINA);
		addKillNpcWithLog(1, A_LIST, 1, AFROS);
		addLevelCheck("33031-lvl.htm", 90);
		addQuestCompletedCheck("33031-lvl.htm", 10310);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33031-5.htm"))
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
				return "33031-9.htm";
			}
			if(event.equalsIgnoreCase("givescrolls"))
			{
				st.giveItems(17527, 2);
				return "33031-10.htm";
			}			
			if(event.equalsIgnoreCase("givesacks"))
			{
				st.giveItems(34861, 2);
				return "33031-11.htm";
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
		if(npcId == GOFINA)
		{
			if(cond == 0)
				return "33031.htm";
			else if(cond == 1)
				return "33031-6.htm";	
			else if(cond == 2)		
				return "33031-8.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == GOFINA)
			htmltext = "33031-comp.htm";
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