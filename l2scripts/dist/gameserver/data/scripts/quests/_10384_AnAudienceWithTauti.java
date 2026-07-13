package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10384_AnAudienceWithTauti extends Quest
{
	// NPC'S
	private static final int FERGASON = 33681;
	private static final int AKU = 33671;

	// Monster's
	private static final int TAUTI = 29237;

	// Item's
	private static final int TAUTIS_FRAGMENT = 34960;
	private static final int BOTTLE_OF_TAUTIS_SOUL = 35295;

	private static final int EXP_REWARD = 951127800;	private static final int SP_REWARD = 228270;	public _10384_AnAudienceWithTauti()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(FERGASON);
		addTalkId(FERGASON, AKU);
		addKillId(TAUTI);
		addQuestItem(TAUTIS_FRAGMENT);
		addLevelCheck("maestro_ferguson_q10384_05.htm", 97);
		addQuestCompletedCheck("maestro_ferguson_q10384_06.htm", 10383);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("maestro_ferguson_q10384_04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("sofa_aku_q10384_02.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("maestro_ferguson_q10384_11.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 3256740, true);
			st.giveItems(BOTTLE_OF_TAUTIS_SOUL, 1);
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
		if(npcId == FERGASON)
		{
			if(cond == 0)
				htmltext = "maestro_ferguson_q10384_01.htm";
			if(cond == 1 || cond == 2)
				htmltext = "maestro_ferguson_q10384_08.htm";
			else if(cond == 3 && st.haveQuestItem(TAUTIS_FRAGMENT))
				htmltext = "maestro_ferguson_q10384_09.htm";
		}
		else if(npcId == AKU)
		{
			if(st.isStarted())
				htmltext = "sofa_aku_q10384_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == FERGASON)
			htmltext = "maestro_ferguson_q10384_07.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == TAUTI)
		{
			if(cond == 2)
			{
				st.setCond(3);
				st.giveItems(TAUTIS_FRAGMENT, 1);
			}
		}
		return null;
	}
}