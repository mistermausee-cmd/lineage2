package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10379_AnUninvitedGuest extends Quest
{
	// NPC's
	private static final int ENDRIGO = 30632;
	private static final int SCALDISECT_THE_FURIOUS = 23212;

	// Item's
	private static final int SOE_GUILLOTINE_FORTRESS = 35292;

	private static final int EXP_REWARD = 934013430;	private static final int SP_REWARD = 224163; 	public _10379_AnUninvitedGuest()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(ENDRIGO);
		addTalkId(ENDRIGO);
		addKillId(SCALDISECT_THE_FURIOUS);
		addLevelCheck("warden_endrigo_q10379_02.htm", 95);
		addQuestCompletedCheck("warden_endrigo_q10379_02.htm", 10377);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("warden_endrigo_q10379_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("warden_endrigo_q10379_09.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 3441680, true);
			st.giveItems(SOE_GUILLOTINE_FORTRESS, 2);
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
				htmltext = "warden_endrigo_q10379_01.htm";
			else if(cond == 1)
				htmltext = "warden_endrigo_q10379_07.htm";
			else if(cond == 2)
				htmltext = "warden_endrigo_q10379_08.htm";

		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == ENDRIGO)
			htmltext = "warden_endrigo_q10379_03.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == SCALDISECT_THE_FURIOUS)
		{
			if(cond == 1)
			{
				st.setCond(2);
			}
		}
		return null;
	}
}