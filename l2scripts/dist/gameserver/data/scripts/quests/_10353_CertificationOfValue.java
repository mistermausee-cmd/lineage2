package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;
import services.SupportMagic;

public class _10353_CertificationOfValue extends Quest
{
	//npc
	public static final int LILEJ = 33155;
	public static final int KUORI = 33358;
	
	public static final String A_LIST = "a_list";

	private static final int EXP_REWARD = 3000000;	private static final int SP_REWARD = 720; 	public _10353_CertificationOfValue()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(LILEJ);
		addTalkId(KUORI);

		addLevelCheck("33155-lvl.htm", 48);
		addKillNpcWithLog(2, A_LIST, 10, 23044, 23045, 23046, 23047, 23048, 23049, 23050, 23051, 23052, 23053, 23054, 23055, 23056, 23057, 23058, 23059, 23060, 23061, 23062, 23063, 23064, 23065, 23066, 23067, 23068, 23102, 23103, 23104, 23105, 23106, 23107, 23108, 23109, 23110, 23111, 23112);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("SupportPlayer"))
		{
			SupportMagic.doSupportMagic(npc, player, false);
			return "33155-6.htm";
		}

		if(event.equalsIgnoreCase("SupportPet"))
		{
			SupportMagic.doSupportMagic(npc, player, true);
			return "33155-6.htm";
		}			
		
		if(event.equalsIgnoreCase("Goto"))
		{
			st.setCond(1);
			player.teleToLocation(119656, 16072, -5120);
			return null;
		}				
		if(event.equalsIgnoreCase("33358-3.htm"))
		{	
				st.setCond(2);
		}				
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == LILEJ)
		{
			if(cond == 0)
				return "33155.htm";
			if(cond == 1)
				return "33155-11.htm";
		}
		if(npcId == KUORI)
		{
			if(cond == 1)
				return "33358.htm";
			if(cond == 2)
				return "33358-5.htm";
			if(cond == 3)
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(17624, 1);
				st.finishQuest();		
				return "33358-6.htm";		
			}	
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == LILEJ)
			htmltext = "33155-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 2)
			return null;
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.setCond(3);
		}
		return null;
	}	
}