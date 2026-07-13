package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

//By Evil_dnk

public class _10410_EmbryoInTheSwampOfScreams extends Quest
{
	public static final String A_LIST = "A_LIST";
    //Квестовые персонажи
    private static final int TRACKER_DOKARA = 33847;

    //Монстры
    private static final int[] MOBS = new int[]{21508, 21509, 21510, 21511, 21512, 21513, 21514, 21515, 21516, 21517, 21518, 21519};
    private static final int SWAMP_OF_SCREAMS_SCOUT = 27508;

    //Награда за квест
    
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;

	private static final int EXP_REWARD = 161046201;
	private static final int SP_REWARD = 4072; 

	public _10410_EmbryoInTheSwampOfScreams()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(TRACKER_DOKARA);
		addTalkId(TRACKER_DOKARA);
		addKillNpcWithLog(1, A_LIST, 300, SWAMP_OF_SCREAMS_SCOUT);
		addKillId(MOBS);
		addRaceCheck("Only characters with level above 65 and below 70  with completed Suspissious Vagabond quest can take this quest!(Not for Ertheia race)", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addQuestCompletedCheck("Only characters with level above 65 and below 70  with completed Suspissious Vagabond quest can take this quest!(Not for Ertheia race)", 10409);
		addLevelCheck("Only characters with level above 65 and below 70  with completed Suspissious Vagabond quest can take this quest!(Not for Ertheia race)", 65/*, 70*/);
		addClassTypeCheck(NO_QUEST_DIALOG, ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "tracker_dokara_q10410_04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();	
			return "tracker_dokara_q10410_06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == TRACKER_DOKARA)
		{
			if(st.getCond() == 0)
				return "tracker_dokara_q10410_01.htm";
			else if(cond == 1)
				return "tracker_dokara_q10410_04.htm";	
			else if(cond == 2)	
				return "tracker_dokara_q10410_05.htm";
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
			NpcInstance scout = qs.addSpawn(SWAMP_OF_SCREAMS_SCOUT, qs.getPlayer().getX() + 50, qs.getPlayer().getY() + 50, qs.getPlayer().getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			Functions.npcSay(scout, NpcString.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
		}
		
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}
}