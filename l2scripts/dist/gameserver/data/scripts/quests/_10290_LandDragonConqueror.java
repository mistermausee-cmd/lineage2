package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */

public class _10290_LandDragonConqueror extends Quest
{
	private static final int Theodric = 30755;
	private static final int ShabbyNecklace = 15522;
	private static final int MiracleNecklace = 15523;
	private static final int UltimateAntharas = 29068;

	private static final int EXP_REWARD = 702557;	private static final int SP_REWARD = 168; 	public _10290_LandDragonConqueror()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Theodric);
		addQuestItem(ShabbyNecklace, MiracleNecklace);
		addKillId(UltimateAntharas);
		addLevelCheck("theodric_q10290_00.htm", 83);
		addItemHaveCheck("theodric_q10290_00a.htm", 3865, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("theodric_q10290_04.htm"))
		{
			st.setCond(1);
			st.giveItems(ShabbyNecklace, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Theodric)
		{
			if(cond == 0)
				htmltext = "theodric_q10290_01.htm";
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(15522) >= 1)
					htmltext = "theodric_q10290_05.htm";
				else if(st.getQuestItemsCount(15522) < 1)
				{
					st.giveItems(ShabbyNecklace, 1);
					htmltext = "theodric_q10290_08.htm";
				}	
			}		
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(MiracleNecklace) >= 1)
				{
					htmltext = "theodric_q10290_07.htm";
					st.takeAllItems(MiracleNecklace);
					st.giveItems(8568, 1);
					st.giveItems(ADENA_ID, 131236);
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
					st.finishQuest();
				}
				else
					htmltext = "theodric_q10290_06.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == UltimateAntharas)
		{
			st.takeAllItems(ShabbyNecklace);
			st.giveItems(MiracleNecklace, 1);
			st.setCond(2);
		}
		return null;
	}
}