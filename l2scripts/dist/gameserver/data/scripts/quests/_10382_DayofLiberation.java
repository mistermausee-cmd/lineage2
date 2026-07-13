package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10382_DayofLiberation extends Quest
{
	// NPC'S
	private static final int SIZRAK = 33669;
	private static final int TAUTI = 29236;

	// Item's
	private static final int TAUTIS_BRACELET = 35293;

	private static final int EXP_REWARD = 951127800;	private static final int SP_REWARD = 228270; 	public _10382_DayofLiberation()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK);
		addKillNpcWithLog(1, "TAUTI", 1, TAUTI);
		addLevelCheck("sofa_sizraku_q10382_04.htm", 97);
		addQuestCompletedCheck("sofa_sizraku_q10382_05.htm", 10381);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_sizraku_q10382_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("sofa_sizraku_q10382_10.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 3256740, true);
			st.giveItems(TAUTIS_BRACELET, 1);
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
		if(npcId == SIZRAK)
		{
			if(cond == 0)
				htmltext = "sofa_sizraku_q10382_01.htm";
			else if(cond == 1)
				htmltext = "sofa_sizraku_q10382_07.htm";
			else if(cond == 2)
				htmltext = "sofa_sizraku_q10382_08.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == SIZRAK)
			htmltext = "sofa_sizraku_q10382_06.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == TAUTI)
		{
			if(cond == 1)
			{
				st.setCond(2);
			}
		}
		return null;
	}
}