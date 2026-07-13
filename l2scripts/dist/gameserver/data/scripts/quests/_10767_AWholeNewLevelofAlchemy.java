package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;

//By Evil_dnk

public class _10767_AWholeNewLevelofAlchemy extends Quest
{
	// NPC's
	private static final int BEROYA = 33977;

	private static final int EXP_REWARD = 14819175;	private static final int SP_REWARD = 3556; 	public _10767_AWholeNewLevelofAlchemy()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BEROYA);
		addLevelCheck("beroni_de_khan_q10767_02.htm", 97);
		addRaceCheck("beroni_de_khan_q10767_02a.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("beroni_de_khan_q10767_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("beroni_de_khan_q10767_08.htm"))
		{
			st.takeItems(39469, 1000);
			st.takeItems(39474, 1000);
			st.takeItems(39479, 1000);
			st.giveItems(39482, 3);
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
			case BEROYA:
				if(cond == 0)
					htmltext = "beroni_de_khan_q10767_01.htm";
				else if (cond == 1)
				{
					if(st.getQuestItemsCount(39469) >= 1000 && st.getQuestItemsCount(39474) >= 1000 && st.getQuestItemsCount(39479) >= 1000)
						htmltext = "beroni_de_khan_q10767_07.htm";
					else
						htmltext = "beroni_de_khan_q10767_06.htm";
				}

			break;

		}
		return htmltext;
	}
}