package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10392_FailureAndItsConsequences extends Quest
{
	//npc
	private static final int IASON_HEINE = 33859;
	private static final int ELLI = 33858;
	//mob
	private static final int[] MOBS = { 20991, 20992, 20993 };
	//rewards
	
	//q items
	private static final int SUSPICIOUS_FRAGMENT = 36709;
	
	private static final int EXP_REWARD = 4175045;	private static final int SP_REWARD = 559; 	public _10392_FailureAndItsConsequences()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(IASON_HEINE);
		addTalkId(IASON_HEINE);
		addTalkId(ELLI);
		addKillId(MOBS);
		addQuestItem(SUSPICIOUS_FRAGMENT);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("no_level.htm", 40/*, 46*/);
		addQuestCompletedCheck("no_level.htm", 10391);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("advance1.htm"))
		{
			st.setCond(3);
			st.takeItems(SUSPICIOUS_FRAGMENT, -1);
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
		
		if(npcId == IASON_HEINE)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 2)
				htmltext = "4.htm";		
		}
		else if(npcId == ELLI)
		{
			if(cond == 3)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st == null)
			return null;
		if(!st.isStarted())
			return null;
		if(st.getCond() != 1)
			return null;
		st.giveItems(SUSPICIOUS_FRAGMENT, 1);
		if(st.getQuestItemsCount(SUSPICIOUS_FRAGMENT) >= 30)
		{
			st.setCond(2);			
		}
		else
			st.playSound(SOUND_ITEMGET);
		return null;
	}
}