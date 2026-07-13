package quests;


import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

public class _465_WeAreFriends extends Quest
{
	
	private static final int Feya_Gorozhanin = 32922;
	
	public static final int FEYA_STARTER = 32921;
	
	public static final int COCON = 32919;
	public static final int HUGE_COCON = 32920;
	
	public static final int SIGN_OF_GRATITUDE = 17377;
	
	
	private static NpcInstance npcFeya = null;

	public _465_WeAreFriends()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(FEYA_STARTER);
		addTalkId(Feya_Gorozhanin);
		addFirstTalkId(Feya_Gorozhanin);
		addKillId(COCON);
		addKillId(HUGE_COCON);
		addQuestItem(SIGN_OF_GRATITUDE);
		addLevelCheck("32921-lvl.htm", 88);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32921-4.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("32922-4.htm"))
		{
			st.setCond(2);
			st.giveItems(SIGN_OF_GRATITUDE, 2);
			if(npcFeya == null)
				return null;
			st.unset("q465feya");	
			npcFeya.deleteMe();
			npcFeya = null;
			return null;	
		}
		
		if(event.equalsIgnoreCase("despawn_task"))
		{
			if(npcFeya == null)
				return null;
			st.unset("q465feya");	
			npcFeya.deleteMe();
			npcFeya = null;
			return null;	
		}	
		
		if(event.equalsIgnoreCase("32921-8.htm"))
		{
			st.takeItems(SIGN_OF_GRATITUDE, 2);
			st.giveItems(17378, 1);
			st.finishQuest();
			//30384 2-4
			if(st.getQuestItemsCount(SIGN_OF_GRATITUDE) > 0)
			{
				st.giveItems(30384, 2);
				return "32921-10.htm";	
			}
			else
			{
				st.giveItems(30384, 4);
				return "32921-8.htm";					
			}
		}

		if(event.equalsIgnoreCase("32921-10.htm"))
		{
			st.giveItems(17378, 1);
			st.finishQuest();
			//30384 2-4
			if(st.getQuestItemsCount(SIGN_OF_GRATITUDE) > 0)
			{
				st.giveItems(30384, 2);
				return "32921-10.htm";	
			}
			else
			{
				st.giveItems(30384, 4);
				return "32921-8.htm";					
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == FEYA_STARTER)
		{
			if(cond == 0)
				return "32921.htm";
			if(cond == 1)
				return "32921-5.htm";
			if(cond == 2)
				return "32921-6.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == FEYA_STARTER)
			htmltext = "32921-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(Rnd.chance(5))
		{
			npcFeya = NpcUtils.spawnSingle(Feya_Gorozhanin, Location.findPointToStay(st.getPlayer(), 50, 100));
			st.set("q465feya", ""+npcFeya.getObjectId()+"");
			st.startQuestTimer("despawn_task", 180000);
		}
		return null;
	}	
	
	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getId());
		if(st == null)
			return "32922.htm";
		if(st.get("q465feya") != null && Integer.parseInt(st.get("q465feya")) != npc.getObjectId())
			return "32922-1.htm";
		if(st.get("q465feya") == null)
			return "32922-1.htm";				
		return "32922-3.htm";
	}	
}