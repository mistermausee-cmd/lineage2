package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _474_WaitingForTheSummer extends Quest
{
	//npc
	public static final int GUIDE = 33463;
	public static final int VUSOTSKII = 31981;
	
	//mobs
	private final int[] Byval = {22093, 22094};
	private final int[] Yryna = {22095, 22096};
	private final int[] Yeti = {22097, 22097};
	
	//q items
	public static final int MEAT_BYVAL = 19490;
	public static final int MEAT_YRYS = 19491;
	public static final int MEAT_YETI = 19492;

	private static final int EXP_REWARD = 1879400;	private static final int SP_REWARD = 451; 	public _474_WaitingForTheSummer()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(GUIDE);
		addTalkId(VUSOTSKII);
		addKillId(Byval);
		addKillId(Yryna);
		addKillId(Yeti);
		addQuestItem(MEAT_BYVAL);
		addQuestItem(MEAT_YRYS);
		addQuestItem(MEAT_YETI);
		addLevelCheck("33463-lvl.htm", 60/*, 64*/);
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
		if(npcId == VUSOTSKII)
		{
			if(cond == 1)
				return "31981-1.htm";
			if(cond == 2)
			{
				st.giveItems(57,194000);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.takeItems(MEAT_BYVAL, -1);
				st.takeItems(MEAT_YRYS, -1);
				st.takeItems(MEAT_YETI, -1);
				st.finishQuest();		
				return "31981.htm"; //no further html do here
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
		if(ArrayUtils.contains(Byval, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(MEAT_BYVAL, 1);
		}
		if(ArrayUtils.contains(Yryna, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(MEAT_YRYS, 1);
		}
		if(ArrayUtils.contains(Yeti, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(MEAT_YETI, 1);
		}		
		if(st.getQuestItemsCount(MEAT_BYVAL) >= 30 && st.getQuestItemsCount(MEAT_YRYS) >= 30 && st.getQuestItemsCount(MEAT_YETI) >= 30)
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