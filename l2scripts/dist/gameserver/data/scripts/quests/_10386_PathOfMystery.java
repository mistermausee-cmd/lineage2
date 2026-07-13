package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10386_PathOfMystery extends Quest
{

	private static final int TOPOI = 30499;
	private static final int HESET = 33780;
	private static final int BERNA = 33796;
	

	private static final int EXP_REWARD = 27244350;
	private static final int SP_REWARD = 6538; 

	public _10386_PathOfMystery()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(TOPOI);
		addTalkId(TOPOI);
		addTalkId(HESET);
		addTalkId(BERNA);
		
		addLevelCheck("You cannot pass this quest until you have reached level 93.", 93);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("acceptedHeset.htm"))
		{
			st.setCond(3);
		}	
		if(event.equalsIgnoreCase("acceptedBerma.htm"))
		{
			st.setCond(4);
		}			
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 58707);
			st.giveItems(17526, 1);
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
		if(npcId == TOPOI)
		{
			if(cond == 0)
				htmltext = "start.htm";
		}
		else if(npcId == HESET)
		{
			if(cond == 1)
				htmltext = "hesetCond1.htm";
			if(cond == 4)
				htmltext = "collected.htm";
		}
		else if(npcId == BERNA)
		{
			if(cond == 3)
				htmltext = "berna.htm";
				
		}	
		return htmltext;
	}
}