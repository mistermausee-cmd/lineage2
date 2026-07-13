package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10375_SuccubusDisciples extends Quest
{
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	//npc
	private static final int ZANIJA = 32140;

	private static final int EXP_REWARD = 24782300;	private static final int SP_REWARD = 5947; 	public _10375_SuccubusDisciples()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(ZANIJA);
		addTalkId(ZANIJA);
		
		addClassLevelCheck("32140-prof.htm", false, ClassLevel.THIRD);
		addClassLevelCheck("32140-prof.htm", true, ClassLevel.SECOND); // Ertheia
		addLevelCheck("32140-lvl.htm", 80);
		addQuestCompletedCheck("32140-prof.htm", 10374);
		addKillNpcWithLog(1, A_LIST, 5, 23191);
		addKillNpcWithLog(1, B_LIST, 5, 23192);
		
		addKillNpcWithLog(3, A_LIST, 5, 23197);
		addKillNpcWithLog(3, B_LIST, 5, 23198);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32140-5.htm"))
		{
			st.setCond(1);
		}		

		if(event.equalsIgnoreCase("32140-8.htm"))
		{
			st.setCond(3);
		}			
		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == ZANIJA)
		{
			if(cond == 0)
				return "32140.htm";
			else if(cond == 1)
				return "32140-6.htm";	
			else if(cond == 2)		
				return "32140-7.htm";
			else if(cond == 3)		
				return "32140-9.htm";
			else if(cond == 4)	
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(57, 498700);
				st.finishQuest();					
				return "32140-10.htm";		
			}		
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == ZANIJA)
			htmltext = "32140-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.setCond(qs.getCond() + 1);
		}

		return null;
	}	
}