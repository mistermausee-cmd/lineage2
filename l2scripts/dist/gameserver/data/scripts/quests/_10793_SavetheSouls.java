package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10793_SavetheSouls extends Quest
{
	// NPC's
	private static final int HATUVA = 33849;

	// Monster's
	private static final int[] MONSTERS = {21547, 21547, 21549, 21550, 21553, 21554, 21555, 21556, 18119, 21548, 21551, 21552, 21557, 21558, 21559, 21560, 21561};

	// Item's
	
	private static final int ENCHANTARMOR = 23418;

	private static final String Forestofdead = "forestofdead";

	private static final int EXP_REWARD = 86636593;
	private static final int SP_REWARD = 226; 

	public _10793_SavetheSouls()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(HATUVA);
		addTalkId(HATUVA);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 579311, Forestofdead, 50, MONSTERS);
		addLevelCheck("33849-0.htm", 65/*, 70*/);
		addClassIdCheck("33849-0.htm", 183, 185, 187, 189, 191);
		addRaceCheck("33849-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33849-4.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33849-7.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case HATUVA:
				if(cond == 0)
					htmltext = "33849-1.htm";
				else if (cond == 1)
					htmltext = "33849-5.htm";
				else if (cond == 2)
					htmltext = "33849-6.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(Forestofdead);
				st.setCond(2);
			}
		}
		return null;
	}
}