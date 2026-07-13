package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10446_HitandRun extends Quest
{
	public static final String A_LIST = "A_LIST";
	//npc
	private static final int BURINU = 33840;
	//mob
	private static final int NERVA_ORC = 23322;
	//rewards
	private static final int ETERNAL_ENHANCEMENT_STONE = 35569;
	private static final int ELMORE_SUPPORT_BOX = 37020;	
	
	public _10446_HitandRun()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(BURINU);
		addTalkId(BURINU);
		addKillNpcWithLog(1, A_LIST, 10, NERVA_ORC);
		
		addLevelCheck("no_level.htm", 99);
		addQuestCompletedCheck("no_level.htm", 10445);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npcId == BURINU)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
			else if(cond == 2)
			{
				st.giveItems(ETERNAL_ENHANCEMENT_STONE, 1);
				st.giveItems(ELMORE_SUPPORT_BOX, 1);			
				st.finishQuest();			
				htmltext = "endquest.htm";
			}	
		}
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