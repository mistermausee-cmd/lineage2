package quests;

import l2s.gameserver.listener.actor.player.OnPickupItemListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10373_ExploringTheDimensionSealingTheDimension extends Quest
{
	private static final int BELORA = 34227;

	private static final int DIMENSION = 46787;

	private static final OnPickupItemListener ITEM_GET_LISTENER = new PickupItemListener();

	private static class PickupItemListener implements OnPickupItemListener
	{
		@Override
		public void onPickupItem(Player player, ItemInstance item)
		{
			QuestState questState = player.getQuestState(10373);
			if(questState == null)
				return;

			else if(questState.getCond() == 1)
			{
				if(questState.getQuestItemsCount(DIMENSION) >= 30)
					questState.setCond(2);
			}
		}
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(ITEM_GET_LISTENER);
	}

	public _10373_ExploringTheDimensionSealingTheDimension()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(BELORA);
		addTalkId(BELORA);
		addQuestItem(DIMENSION);
		addQuestItemWithLog(1, 46787, 30, DIMENSION);
		addLevelCheck("cod_inner_officer_a_q10373_02.htm", 95, 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (event.equalsIgnoreCase("cod_inner_officer_a_q10373_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("cod_inner_officer_a_q10373_08.htm"))
		{
			st.giveItems(47044, 1, false);
			st.giveItems(45572, 1, false);
			st.giveItems(39738, 1);
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
		switch (npcId)
		{
			case BELORA:
				if (cond == 0)
					htmltext = "cod_inner_officer_a_q10373_01.htm";
				else if (cond == 1)
					htmltext = "cod_inner_officer_a_q10373_06.htm";
				else if (cond == 2)
					htmltext = "cod_inner_officer_a_q10373_07.htm";
				break;
		}
		return htmltext;
	}
}
