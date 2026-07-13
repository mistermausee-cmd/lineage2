package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10442_TheAnnihilatedPlains1 extends Quest
{
	//npc
	private static final int MATHIAS = 31340;
	private static final int TUSKA = 33839;
	
	private static final int EXP_REWARD = 15436575;	private static final int SP_REWARD = 3704; 	public _10442_TheAnnihilatedPlains1()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(MATHIAS);
		addTalkId(MATHIAS);
		addTalkId(TUSKA);
		
		addLevelCheck("no_level.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}	
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npcId == MATHIAS)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == TUSKA)
		{
			if(cond == 1)
				return "1-1.htm";		
		}			
		return htmltext;
	}
}