package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10381_TotheSeedofHellfire extends Quest
{
	// NPC'S
	private static final int KEUCEREUS = 32548;
	private static final int KBALDIR = 32733;
	private static final int SIZRAK = 33669;

	// Item's
	private static final int KBALDIRS_LETTER = 34957;

	private static final int EXP_REWARD = 951127800;	private static final int SP_REWARD = 228270; 	public _10381_TotheSeedofHellfire()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, KBALDIR, SIZRAK);
		addQuestItem(KBALDIRS_LETTER);
		addLevelCheck("kserth_q10381_04.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("kserth_q10381_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("kbarldire_q10381_03.htm"))
		{
			st.setCond(2);
			st.giveItems(KBALDIRS_LETTER, 1);
		}
		else if(event.equalsIgnoreCase("sofa_sizraku_q10381_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 3256740, true);
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
		if(npcId == KEUCEREUS)
		{
			if(cond == 0)
				htmltext = "kserth_q10381_01.htm";
			else if(cond > 0)
				htmltext = "kserth_q10381_06.htm";
		}
		else if(npcId == KBALDIR)
		{
			if(cond == 1)
				htmltext = "kbarldire_q10381_01.htm";
			else if(cond == 2)
				htmltext = "kbarldire_q10381_04.htm";
		}
		else if(npcId == SIZRAK)
		{
			if(cond == 2)
				htmltext = "sofa_sizraku_q10381_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == KEUCEREUS)
			htmltext = "kserth_q10381_05.htm";
		return htmltext;
	}
}