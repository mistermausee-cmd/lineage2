package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10409_ASuspiciousVagabondInTheSwamp extends Quest
{
    //Квестовые персонажи
    private static final int TRACKER_DOKARA = 33847;
    private static final int SUSPICIOUS_VAGABOND = 33848;
    //Награда
    private static final int Enchant_Armor_A = 730;
    

	private static final int EXP_REWARD = 7541520;
	private static final int SP_REWARD = 226; 

	public _10409_ASuspiciousVagabondInTheSwamp()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(TRACKER_DOKARA);
		addTalkId(TRACKER_DOKARA, SUSPICIOUS_VAGABOND);
		addRaceCheck("Only 65-75 level may take this quest!", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("Only 65-75 level may take this quest!", 65/*, 70*/);
		addClassTypeCheck(NO_QUEST_DIALOG, ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "4.htm";
		}
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();		
			return "6.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == TRACKER_DOKARA)
		{
			if(st.getCond() == 0)
				return "1.htm";
			if(st.getCond() == 1)
				return "4.htm";
			if(st.getCond() == 2)
				return "5.htm";
		}		
		else if(npc.getNpcId() == SUSPICIOUS_VAGABOND)
		{
			if(st.getCond() == 1)
			{
				st.setCond(2);
				return "2-1.htm";
			}	
		}	
		return NO_QUEST_DIALOG;
	}
}