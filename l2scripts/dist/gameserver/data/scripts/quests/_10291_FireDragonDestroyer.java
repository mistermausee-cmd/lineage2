package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public class _10291_FireDragonDestroyer extends Quest
{
	private static final int Klein = 31540;
	private static final int PoorNecklace = 15524;
	private static final int ValorNecklace = 15525;
	private static final int Valakas = 29028;

	private static final int EXP_REWARD = 717291;	private static final int SP_REWARD = 172; 	public _10291_FireDragonDestroyer()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Klein);
		addQuestItem(PoorNecklace, ValorNecklace);
		addKillId(Valakas);
		addLevelCheck("klein_q10291_00.htm", 83);
		addItemHaveCheck("klein_q10291_00a.htm", 7267, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klein_q10291_04.htm"))
		{
			st.setCond(1);
			st.giveItems(PoorNecklace, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Klein)
		{
			if(cond == 0)
				htmltext = "klein_q10291_01.htm";
			else if(cond == 1)
			{	
				if(st.getQuestItemsCount(15524) >= 1)
					htmltext = "klein_q10291_05.htm";
				else if(st.getQuestItemsCount(15524) < 1)
				{
					st.giveItems(PoorNecklace, 1);
					htmltext = "klein_q10291_08.htm";
				}	
			}		
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(ValorNecklace) >= 1)
				{
					htmltext = "klein_q10291_07.htm";
					st.takeAllItems(ValorNecklace);
					st.giveItems(8567, 1);
					st.giveItems(ADENA_ID, 126549);
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
					st.finishQuest();
				}
				else
					htmltext = "klein_q10291_06.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == Valakas)
		{
			st.takeAllItems(PoorNecklace);
			st.giveItems(ValorNecklace, 1);
			st.setCond(2);
		}
		return null;
	}
}