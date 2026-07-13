package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _210_ObtainaWolfPet extends Quest
{
	// Квестовые персонажи
	private static final int LUNDY = 30827;
	private static final int BELLADONA = 30256;
	private static final int BRYNNER = 30335;
	private static final int SYDNEY = 30321;

	public _210_ObtainaWolfPet()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(LUNDY);
		addTalkId(LUNDY, BELLADONA, BRYNNER, SYDNEY);
		addLevelCheck("pet_manager_lundy_q0210_02.htm", 15);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "pet_manager_lundy_q0210_04.htm";
		}
		if(event.equalsIgnoreCase("enquest"))
		{
			st.giveItems(2375, 1);
			st.finishQuest();		
			return "pet_manager_lundy_q0210_10.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();

		int cond = st.getCond();
		if(npcId == LUNDY)
		{
			if(st.getCond() == 0)
				return "pet_manager_lundy_q0210_01.htm";
			if(st.getCond() == 1)
				return "pet_manager_lundy_q0210_05.htm";
			if(st.getCond() == 4)
				return "pet_manager_lundy_q0210_06.htm";
		}		
		else if(npc.getNpcId() == BELLADONA)
		{
			if(st.getCond() == 1)
			{
				st.setCond(2);
				return "gatekeeper_belladonna_q0210_01.htm";			
			}
		}
		else if(npc.getNpcId() == BRYNNER)
		{
			if(st.getCond() == 2)
			{
				st.setCond(3);
				return "guard_brynner_q0210_01.htm";
			}		
		}
		else if(npc.getNpcId() == SYDNEY)
		{
			if(st.getCond() == 3)
			{
				st.setCond(4);
				return "trader_sydney_q0210_01.htm";
			}		
		}		
		return NO_QUEST_DIALOG;
	}
}