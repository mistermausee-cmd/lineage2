package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10417_DaimonTheWhiteEyed extends Quest
{
    private static final int Eye = 31683;
	private static final int Janitt = 33851;
    //Награда
    private static final int Enchant_Armor_A = 730;
    
	//mob
	private static final int DAEMON = 27499;

	private static final int EXP_REWARD = 306167814;	private static final int SP_REWARD = 3265; 	public _10417_DaimonTheWhiteEyed()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Eye);
		addTalkId(Eye);
		addTalkId(Janitt);
		addKillId(DAEMON);
		addLevelCheck("no_level.htm", 70/*, 75*/);
		addQuestCompletedCheck("no_level.htm", 10416);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "accept.htm";
		}
		else if(event.equalsIgnoreCase("cod3"))
		{
			st.setCond(3);
			return "gl1.htm";
		}			
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();		
			return "endquest.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == Eye)
		{
			if(st.getCond() == 0)
				return "1.htm";
			else if(st.getCond() == 1)
				return "4.htm";
			else if(st.getCond() == 2)
				return "5.htm";
		}			
		else if(npcId == Janitt)
		{
			if(st.getCond() < 3)
				return "1-1.htm";
			if(st.getCond() == 3)
				return "1-2.htm";
		}
		return NO_QUEST_DIALOG;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.setCond(2);
		}
		return null;
	}	
}