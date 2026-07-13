package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10380_TheExecutionersExecution extends Quest
{
	// NPC's
	private static final int ENDRIGO = 30632;
	private static final int GUILLOTINE_OF_DEATH = 25892;

	// Item's
	private static final int GLORIOUS_T_SHIRT = 35291;

	private static final int EXP_REWARD = 1022967090;	private static final int SP_REWARD = 245512; 	public _10380_TheExecutionersExecution()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(ENDRIGO);
		addTalkId(ENDRIGO);
		addKillId(GUILLOTINE_OF_DEATH);
		addKillNpcWithLog(1, "GUILLOTINE_OF_DEATH", 1, GUILLOTINE_OF_DEATH);
		addLevelCheck("warden_endrigo_q10380_02.htm", 95);
		addQuestCompletedCheck("warden_endrigo_q10380_02.htm", 10379);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("warden_endrigo_q10380_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("warden_endrigo_q10380_10.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(GLORIOUS_T_SHIRT, 1);
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
		if(npcId == ENDRIGO)
		{
			if(cond == 0)
				htmltext = "warden_endrigo_q10380_01.htm";
			else if(cond == 1)
				htmltext = "warden_endrigo_q10380_07.htm";
			else if(cond == 2)
				htmltext = "warden_endrigo_q10380_08.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == ENDRIGO)
			htmltext = "warden_endrigo_q10380_03.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == GUILLOTINE_OF_DEATH)
		{
			if(cond == 1)
			{
				st.setCond(2);
			}
		}
		return null;
	}
}