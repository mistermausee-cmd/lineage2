package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10378_WeedingWork extends Quest
{
	//q items
	private static final int STEBEL = 34974;
	private static final int KOREN = 34975;
	//reward items
	private static final int SCROLL = 35292;

	private static final int DADFENA = 33697;

	private static final int EXP_REWARD = 845059770;	private static final int SP_REWARD = 202814; 	public _10378_WeedingWork()
	{
		super(PARTY_ALL, ONETIME);
		addTalkId(DADFENA);
		addQuestItem(STEBEL);
		addQuestItem(KOREN);
		
		addKillId(23210, 23211);
		
		addLevelCheck(NO_QUEST_DIALOG, 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.takeAllItems(STEBEL);
			st.takeAllItems(KOREN);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(SCROLL, 2);
			st.giveItems(57, 3000000);
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
		if(npcId == DADFENA)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "notcollected.htm";
			else if(cond == 2)
				htmltext = "collected.htm";
		}
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		if(qs.getQuestItemsCount(STEBEL) < 5 && Rnd.chance(7))
			qs.giveItems(STEBEL, 1);
		if(qs.getQuestItemsCount(KOREN) < 5 && Rnd.chance(7))
			qs.giveItems(KOREN, 1);
		if(qs.getQuestItemsCount(KOREN) >= 5 && qs.getQuestItemsCount(STEBEL) >= 5)
			qs.setCond(2);
		return null;
	}
}