package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10443_TheAnnihilatedPlains2 extends Quest
{
	//npc
	private static final int TUSKA = 33839;
	private static final int TRUP = 33837;
	private static final int FOLK = 33843;
	
	private static final int NECK = 36678;
	
	private static final int EXP_REWARD = 308731500;	private static final int SP_REWARD = 74095; 	public _10443_TheAnnihilatedPlains2()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(TUSKA);
		addTalkId(TUSKA);
		addTalkId(TRUP);
		addTalkId(FOLK);
		addQuestItem(NECK);
		
		addLevelCheck("no_level.htm", 99);
		addQuestCompletedCheck("no_level.htm", 10442);
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
			st.takeItems(NECK, -1);
			st.giveItems(30357, 50);
			st.giveItems(30358, 50);
			st.giveItems(34609, 10000);
			st.giveItems(34616, 10000);
			st.giveItems(37018, 1);
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
		
		if(npcId == TUSKA)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "3.htm";
		}
		else if(npcId == TRUP)
		{
			if(cond == 1)
			{
				st.giveItems(NECK, 1);
				st.setCond(2);
				return "1-1.htm";	
			}	
		}	
		else if(npcId == FOLK)
		{
			if(cond == 2)
				return "2-1.htm";
		}	
		return htmltext;
	}
}