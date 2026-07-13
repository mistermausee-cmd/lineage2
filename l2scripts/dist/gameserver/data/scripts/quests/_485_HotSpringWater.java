package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _485_HotSpringWater extends Quest
{
	//npc
	public static final int GUIDE = 33463;
	public static final int VALDEMOR = 30844;
	
	//mobs
	private final static int[] Mobs = { 21314, 21315, 21316, 21317, 21318, 21319, 21320, 21321, 21322, 21323 };
	
	//q items
	public static final int WATER = 19497;

	private static final int EXP_REWARD = 9483000;	private static final int SP_REWARD = 2275; 	public _485_HotSpringWater()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(GUIDE);
		addTalkId(VALDEMOR);
		addKillId(Mobs);
		addQuestItem(WATER);
		addLevelCheck("33463-lvl.htm", 70/*, 74*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33463-3.htm"))
		{
			st.setCond(1);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == GUIDE)
		{
			if(cond == 0)
				return "33463.htm";
			if(cond == 1)
				return "33463-4.htm";
			if(cond == 2)
				return "33463-5.htm";
			
		}
		if(npcId == VALDEMOR)
		{
			if(cond == 1)
				return "30844-1.htm";
			else if(cond == 2)
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.takeItems(WATER, -1);
				st.giveItems(57, 247410);
				st.finishQuest();			
				return "30844.htm"; //no further html do here
			}	
		}		
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == GUIDE)
			htmltext = "33463-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		if(ArrayUtils.contains(Mobs, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(WATER, 1);
		}	
		if(st.getQuestItemsCount(WATER) >= 40)
			st.setCond(2);
			
		return null;
	}

	@Override
	public boolean isVisible(Player player)
	{
		if(DailyQuestsManager.isQuestDisabled(getId()))
			return false;
		return true;
	}	
}