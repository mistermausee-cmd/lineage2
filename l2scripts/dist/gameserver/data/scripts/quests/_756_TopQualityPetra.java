package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author KilRoy & Mangol
 * @name 756 - Top Quality Petra
 * @category Daily quest. Party
 * @see http://l2wiki.com/Top_Quality_Petra
 */
public class _756_TopQualityPetra extends Quest
{
	private int AKU_MARK = 34910;
	private int TOP_QUALITY_PETRA = 35703;

	private int AKU = 33671;

	private static final int EXP_REWARD = 570676680;	private static final int SP_REWARD = 136962; 	public _756_TopQualityPetra()
	{
		super(PARTY_NONE, DAILY);
		addTalkId(AKU);
		addQuestItem(TOP_QUALITY_PETRA);

		addLevelCheck("This quest can be started only when you have reached level 97", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("sofa_aku_q0756_02.htm"))
		{
			st.takeAllItems(TOP_QUALITY_PETRA);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(AKU_MARK, 1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == AKU)
		{
			if(cond == 1)
				htmltext = "sofa_aku_q0756_01.htm";
			else
				htmltext = "sofa_aku_q0756_03.htm";
		}
		return htmltext;
	}
}