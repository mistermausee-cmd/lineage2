package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

/** 
 * @author coldy
 * @date 24.07.2012
 * TODO: offlike EN HTMLs
*/
public class _10376_BloodyGoodTime extends Quest
{
	private static final int NPC_ZENYA = 32140;
	private static final int NPC_CASCA = 32139;
	private static final int NPC_AGNES = 31588;
	private static final int NPC_ANDREI = 31292;

	private static final int MOB_BLOODY_VEIN = 27481;

	private static final int REWARD_MAGIC_RUNE_CLIP = 32700;
	
	public static final String _bloodyVein = "NightmareDeath";
	private static Map<Integer, Integer> spawns = new HashMap<Integer, Integer>();

	private static final int EXP_REWARD = 121297500;	private static final int SP_REWARD = 29111; 	public _10376_BloodyGoodTime() 
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(NPC_ZENYA);
		addTalkId(NPC_CASCA, NPC_AGNES, NPC_ANDREI);
		
		addKillNpcWithLog(3, _bloodyVein, 1, MOB_BLOODY_VEIN);

		addLevelCheck("32140-04.htm", 80);
		addQuestCompletedCheck("32140-03.htm", 10375);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		if(event.equalsIgnoreCase("32140-06.htm")) 
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("32139-03.htm")) 
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("enterInstance")) 
		{
			st.setCond(3);
			NpcInstance BloodyVein = st.addSpawn(MOB_BLOODY_VEIN, st.getPlayer().getX() + 50, st.getPlayer().getY() + 50, st.getPlayer().getZ(), 0, 0, 180000);
			spawns.put(st.getPlayer().getObjectId(), BloodyVein.getObjectId());
			BloodyVein.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 100000);
			return "";
		}
		if(event.equalsIgnoreCase("32139-08.htm")) 
		{
			st.setCond(5);
		}
		if(event.equalsIgnoreCase("teleport_goddard"))
		{
			st.getPlayer().teleToLocation(149597, -57249, -2976);
			return "";
		}
		if(event.equalsIgnoreCase("31588-03.htm")) 
		{
			st.setCond(6);
		}
		if(event.equalsIgnoreCase("31292-03.htm")) 
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(REWARD_MAGIC_RUNE_CLIP,1);
			st.finishQuest();
		}
		return event;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if(npcId == NPC_ZENYA)
		{
			if(cond == 0)
				htmltext = "32140-01.htm";
			else if(cond > 0)
				htmltext = "32140-07.htm";
		}
		else if(npcId == NPC_CASCA)
		{
			switch(cond)
			{
				case 1:
					htmltext = "32139-02.htm";
					break;
				case 2:
				case 3:
					htmltext = "32139-03.htm";
					Integer obj_id = spawns.get(st.getPlayer().getObjectId());
					NpcInstance mob = obj_id != null ? GameObjectsStorage.getNpc(obj_id) : null;
					if(mob == null || mob.isDead())
						htmltext = "32139-03.htm";
					else
						htmltext = NO_QUEST_DIALOG;//TODO
					break;
				case 4:
					htmltext = "32139-04.htm";
					break;
				case 5:
					htmltext = "32139-08.htm";
			}
		}
		else if(npcId == NPC_AGNES)
		{
			if(cond == 5)
				htmltext = "31588-01.htm";
			else if(cond == 6)
				htmltext = "31588-03.htm";
		}
		else if(npcId == NPC_ANDREI)
		{
			if(cond == 6)
				htmltext = "31292-01.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == NPC_ZENYA)
			htmltext = "32140-05.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		if(st.getCond() != 3)
			return null;

		if(updateKill(npc, st))
		{
			st.unset(_bloodyVein);
			st.setCond(4);
		}
		
		return null;
	}
}
