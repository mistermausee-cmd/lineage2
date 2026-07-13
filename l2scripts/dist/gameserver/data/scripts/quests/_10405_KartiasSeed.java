package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10405_KartiasSeed extends Quest
{
    //Квестовые персонажи
    private static final int SUBAN = 33867;
    
    //Монстры
    private static final int[] MOBS = new int[] {20974, 20975, 20976, 21001, 21002, 21003, 21004, 21005};
    
    //Квест итем
    private static final int KARTIAS_MUTATED_SEED = 36714;
    
    //Награда за квест
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;

	private static final int EXP_REWARD = 31303665;
	private static final int SP_REWARD = 1500; 

	public _10405_KartiasSeed()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(SUBAN);
		addTalkId(SUBAN);
		addKillId(MOBS);
		addQuestItem(KARTIAS_MUTATED_SEED);
		addRaceCheck("Can taken only by characters level's above 61 and bellow 65!(Not for Ertheia race)", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("Can taken only by characters level's above 61 and bellow 65!(Not for Ertheia race)", 61/*, 65*/);
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
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 5);
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

		if(npcId == SUBAN)
		{
			if(st.getCond() == 0)
				return "1.htm";
			else if(st.getCond() == 1)
				return "4.htm";
			else if(st.getCond() == 2)
				return "5.htm";
		}		
		return NO_QUEST_DIALOG;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			if(st.rollAndGive(KARTIAS_MUTATED_SEED, 1, 1, 100, 60))
				st.setCond(2);
		}
		return null;
	}	
}