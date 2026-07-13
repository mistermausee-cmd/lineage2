package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10389_TheVoiceOfAuthority extends Quest
{
	//reward items
	private static final int SIGN = 36229;

	private static final int RADZEN = 33803;
	
	private static final String KILL = "kill";

	private static final int EXP_REWARD = 592767000;	private static final int SP_REWARD = 142264; 	public _10389_TheVoiceOfAuthority()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(RADZEN);
		addTalkId(RADZEN);
		
		addKillNpcWithLog(1, 538951, KILL, 30, 22139, 22140, 22141, 22147, 22154, 22144, 22145, 22148, 22142, 22155);
		
		addLevelCheck("you cannot procceed with this quest until you have completed the Conspiracy Behind Door quest", 97);
		addQuestCompletedCheck("you cannot procceed with this quest until you have completed the Conspiracy Behind Door quest", 10388);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(SIGN, 1);
			st.giveItems(57, 1302720);
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
		if(npcId == RADZEN)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "notcollected.htm";
			else if(cond == 2)
				htmltext = "collected.htm";
		}
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
			
		boolean doneKill = updateKill(npc, qs);
		if(doneKill) 
		{
			qs.unset(KILL);
			qs.setCond(2);			
		}
		return null;
	}
}