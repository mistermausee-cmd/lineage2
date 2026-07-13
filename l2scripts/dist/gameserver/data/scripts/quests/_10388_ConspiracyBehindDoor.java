package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10388_ConspiracyBehindDoor extends Quest
{
	// NPC's
	private static final int ELIA = 31329;
	private static final int KARGOS = 33821;
	private static final int HICHEN = 33820;
	private static final int RAZDEN = 33803;

	// Items
	private static final int VISITORS_BADGE = 8064;

	private static final int EXP_REWARD = 29638350;	private static final int SP_REWARD = 7113; 	public _10388_ConspiracyBehindDoor()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(ELIA);
		addTalkId(KARGOS);
		addTalkId(HICHEN);
		addTalkId(RAZDEN);
		addLevelCheck("nolvl.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("go.htm"))
		{
			st.setCond(1);
		}
		else if(event.equals("toCond2.htm"))
		{
			st.setCond(2);
		}
		else if(event.equals("toCond3.htm"))
		{
			st.setCond(3);
			st.giveItems(VISITORS_BADGE, 1);
		}	
		else if(event.equals("final.htm"))
		{
			st.finishQuest();
			st.giveItems(ADENA_ID, 65136);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
		}		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();		
		if(npcId == ELIA)
		{
			if(cond == 0)
				htmltext = "start.htm";
		}
		else if(npcId == KARGOS)
		{
			if(cond == 1)
				return "cond1.htm";
		}		
		else if(npcId == HICHEN)
		{
			if(cond == 2)
				return "cond2.htm";
		}	
		else if(npcId == RAZDEN)
		{
			if(cond == 3)
				return "cond3.htm";
		}

		return htmltext;
	}
}