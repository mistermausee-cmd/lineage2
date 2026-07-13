package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10402_NowhereToTurn extends Quest
{
	public static final String A_LIST = "A_LIST";
    //Квест НПЦ
    private static final int EBLUNE = 33865;

    //Квест монстры
    private static final int[] MOBS = new int[]{20679, 20680, 21017, 21018, 21019, 21020, 21021, 21022};
    //Награда
    
    private static final int SCROLL_ENCHANT_ARMOR_B_GRADE = 948;

	private static final int EXP_REWARD = 9115741;	private static final int SP_REWARD = 1315; 	public _10402_NowhereToTurn()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(EBLUNE);
		addTalkId(EBLUNE);
		addKillNpcWithLog(1, 540211, A_LIST, 60, MOBS);
		addRaceCheck("Only characters with level above 58 and below 61 can take this quest!", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("Only characters with level above 58 and below 61 can take this quest!", 58/*, 61*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "giants_minion_eblune_q10402_04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();	
			return "giants_minion_eblune_q10402_06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(npcId == EBLUNE)
		{
			if(cond == 0)
				return "giants_minion_eblune_q10402_01.htm";
			else if(cond == 1)
				return "giants_minion_eblune_q10402_04.htm";	
			else if(cond == 2)	
				return "giants_minion_eblune_q10402_05.htm";
		}
		return NO_QUEST_DIALOG;
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