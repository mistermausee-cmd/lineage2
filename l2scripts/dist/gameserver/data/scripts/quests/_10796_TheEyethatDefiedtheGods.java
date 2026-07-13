package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By SanyaDC
public class _10796_TheEyethatDefiedtheGods extends Quest
{
	// NPC's
	private static final int HERMIT = 31616;
	private static final int EYEAGROS = 31683;	
	private static final int[] MOBS = new int[]{21294,21295,21296,21297,21299,23311,21304,23312};

	// Item's
	private static final int ENCHANTARMOR = 23418;
	public static final String A_LIST = "a_list";
	private static final int EXP_REWARD = 178732196;
	private static final int SP_REWARD = 261; 
	

	public _10796_TheEyethatDefiedtheGods()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(HERMIT);
		addTalkId(EYEAGROS);
		addKillId(MOBS);
		addLevelCheck("31616-0.htm", 70, 75);
		addRaceCheck("31616-0.htm", Race.ERTHEIA);
		addKillNpcWithLog(1, 579611, A_LIST, 200, MOBS);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31616-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("31683-4.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case HERMIT:
			{
				if(cond == 0)
					htmltext = "31616-1.htm";
				else if(cond == 1)
					htmltext = "31616-5.htm";
				break;
			}
			case EYEAGROS:
			{	
				if(cond == 1)
					htmltext = "31683n.htm";
				else if(cond == 2)
					htmltext = "31683-1.htm";
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{	
		if(qs.getCond() == 1)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);				
				qs.setCond(2);			
			}
		}
		return null;		
	}
}