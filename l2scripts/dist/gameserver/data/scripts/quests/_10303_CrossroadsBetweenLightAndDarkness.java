package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.event.OnStartStopListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.PlayerListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.commons.util.Rnd;

/**
 * @author pchayka
 */
public abstract class _10303_CrossroadsBetweenLightAndDarkness extends Quest
{
	private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10303);
			if(player.getLevel() >= 90 && questState == null)
				questState = newQuestState(player);
		}
	}
	
	private static final int YONA = 32909;
	private static final int SECRET_ZHREC = 33343;
	private static final int DARKSTONE = 17747;
	private final int[] locMobs = {22895, 22887, 22879, 22871, 22863};

	private static final int EXP_REWARD = 6730155;	private static final int SP_REWARD = 1615; 	public _10303_CrossroadsBetweenLightAndDarkness()
	{
		super(PARTY_NONE, ONETIME);
		addKillId(locMobs);
		addQuestItem(DARKSTONE);
		addTalkId(YONA);
		addTalkId(SECRET_ZHREC);
		addLevelCheck("32909-lvl.htm", YONA, 90);
		addLevelCheck("33343-lvl.htm", SECRET_ZHREC, 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32909-5.htm"))
		{
			st.takeItems(57, 465855);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(DARKSTONE, -1);
			st.giveItems(getRndRewardYona(), 1);
			st.finishQuest();
		}
		
		if(event.equalsIgnoreCase("33343-5.htm"))
		{
			st.takeItems(57, 465855);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(getRndRewardZhrec(), 1);
		}		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{	
			if(st.getQuestItemsCount(DARKSTONE) == 0 && Rnd.chance(5))
				st.giveItems(DARKSTONE, 1);
		}
		return null;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == YONA)
		{
			if(cond == 1 && st.getQuestItemsCount(DARKSTONE) >= 1)
				return "32909.htm";
		}
		else if(npcId == SECRET_ZHREC)
		{
			if(cond == 1 && st.getQuestItemsCount(DARKSTONE) >= 1)
				return "33343.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == YONA)
			htmltext = "32909-comp.htm";
		else if(npcId == SECRET_ZHREC)
			htmltext = "33343-comp.htm";
		return htmltext;
	}
	
	@Override
	public boolean isVisible(Player player)
	{
		if(player.getLevel() < 90)
			return false;
		QuestState questState = player.getQuestState(10303);	
		if(questState == null || !questState.isStarted())
			return false;
		return true;	
	}	
	
	private static int getRndRewardYona()
	{
		switch(Rnd.get(12))
		{
			case 1:
			case 2:
			case 3:
				return 13505;
			case 4:
			case 5:
			case 6:	
				return 16108;
			case 7:
			case 8:
			case 9:	
				return 16102;
			case 10:
			case 11:
			case 12:	
				return 16105;
		}
		return 57;
	}

	private static int getRndRewardZhrec()
	{
		switch(Rnd.get(12))
		{
			case 1:
			case 2:
			case 3:
				return 16101;
			case 4:
			case 5:
			case 6:	
				return 16100;
			case 7:
			case 8:
			case 9:	
				return 16099;
			case 10:
			case 11:
			case 12:	
				return 16098;
		}
		return 57;
	}	
}
