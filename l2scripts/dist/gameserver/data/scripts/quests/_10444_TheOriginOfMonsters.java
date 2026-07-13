package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10444_TheOriginOfMonsters extends Quest
{
	//npc
	private static final int PARAYAN = 33842;
	private static final int KVINSROT = 33838;
	//rewards
	private static final int BLOODY_AIDOS = 35569;
	private static final int CHEST = 37020;
	//mobs
	private static final int[] MOBS = {25927};
	
	private static final int PARTS = 36679;
	
	public _10444_TheOriginOfMonsters()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(PARAYAN);
		addTalkId(PARAYAN);
		addTalkId(KVINSROT);
		addKillId(MOBS);
		addQuestItem(PARTS);
		
		addLevelCheck("no_level.htm", 99);
		addQuestCompletedCheck("no_level.htm", 10443);
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
			st.giveItems(BLOODY_AIDOS, 1);
			st.giveItems(CHEST, 1);		
			st.takeItems(PARTS, 2);
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
		
		if(npcId == PARAYAN)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == KVINSROT)
		{
			if(cond == 2)
				return "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{	
		if(qs.getCond() == 1)
		{
			qs.giveItems(PARTS, 1);
			if(qs.getQuestItemsCount(PARTS) >= 2)
			{
				qs.setCond(2);
			}	
		}
		return null;
	}
}