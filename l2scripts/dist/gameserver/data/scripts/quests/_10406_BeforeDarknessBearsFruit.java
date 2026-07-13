package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

public class _10406_BeforeDarknessBearsFruit extends Quest
{
	public static final String A_LIST = "A_LIST";
    //Квестовые персонажи
    private static final int SUBAN = 33867;

    //Монстры
    private static final int[] MOBS = new int[]{19470};
    private static final int BEARS_FRUIT_DEFENDER = 27517 ;

    //Награда за квест
    
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;

	private static final int EXP_REWARD = 13561681;	private static final int SP_REWARD = 750; 	public _10406_BeforeDarknessBearsFruit()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(SUBAN);
		addTalkId(SUBAN);

		addKillNpcWithLog(1, 1019470, A_LIST, 10, BEARS_FRUIT_DEFENDER);
		addKillId(MOBS);

		addQuestCompletedCheck("Only characters with level above 61 and below 65, with completed Kartia's seed quest can take this quest!(Not for Ertheia race)", 10405);
		addRaceCheck("Only characters with level above 61 and below 65, with completed Kartia's seed quest can take this quest!(Not for Ertheia race)", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("Only characters with level above 61 and below 65, with completed Kartia's seed quest can take this quest!(Not for Ertheia race)", 61/*, 65*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "shuvann_q10406_04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();	
			return "shuvann_q10406_06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == SUBAN)
		{
			if(st.getCond() == 0)
				return "shuvann_q10406_01.htm";
			else if(cond == 1)
				return "shuvann_q10406_04.htm";	
			else if(cond == 2)	
				return "shuvann_q10406_05.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		if(ArrayUtils.contains(MOBS,npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(BEARS_FRUIT_DEFENDER, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			Functions.npcSay(scout, getRndString());
		}
		
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}	
	
	private static NpcString getRndString()
	{
		switch(Rnd.get(1, 5))
		{
			case 1:
				return NpcString.THERE_IS_ONLY_DEATH_FOR_INTRUDERS;
			case 2:
				return NpcString.YOU_DIG_YOUR_OWN_GRAVE_COMING_HERE; 
			case 3:
				return NpcString.DIE_2; 			
			case 4:
				return NpcString.DO_NOT_TOUCH_THAT_FLOWER; 				
			case 5:
				return NpcString.HAH_YOU_BELIEVE_THAT_IS_ENOUGH_TO_STAND_IN_THE_PATH_OF_DARKNESS; 					
		}
		return null;
	}
}