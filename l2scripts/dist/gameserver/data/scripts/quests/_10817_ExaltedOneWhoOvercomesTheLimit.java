package quests;

import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExQuestNpcLogList;

public class _10817_ExaltedOneWhoOvercomesTheLimit extends Quest
{
	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10817);
			if(questState == null)
				return;

			if(questState.getCond() == 1 && questState.get("CUSTOM_LOG") == null)
			{
				player.addListener(LEVEL_CHANGE_LISTENER);
			}
		}
	}

	private static class ChangeLevelListener implements OnLevelChangeListener
	{
		@Override
		public void onLevelChange(Player player, int was, int set)
		{
			QuestState questState = player.getQuestState(10817);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && set >= 100)
			{
				questState.set("CUSTOM_LOG", 1, true);
				player.sendPacket(new ExQuestNpcLogList(questState));
				if(questState.haveQuestItem(45628) && questState.haveQuestItem(45629) && questState.haveQuestItem(45630) && questState.haveQuestItem(45631))
				{
					questState.setCond(2);
					player.removeListener(LEVEL_CHANGE_LISTENER);
				}
			}
		}
	}

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_CHANGE_LISTENER = new ChangeLevelListener();

	// NPC's
	private static final int LEONEL = 33907;

	public _10817_ExaltedOneWhoOvercomesTheLimit()
	{
			super(PARTY_ONE, ONETIME);
			addStartNpc(LEONEL);
			addTalkId(LEONEL);
			addLevelCheck("lionel_hunter_q10817_02.htm", 99);
			addNobleCheck("lionel_hunter_q10817_02.htm", true);
			addQuestCompletedCheck("lionel_hunter_q10817_02.htm", 10811);
			addCustomLog(1, "CUSTOM_LOG", 581711, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("lionel_hunter_q10817_05.htm"))
		{
			if (!st.haveQuestItem(45632))
				st.giveItems(45632, 1, false);
			st.setCond(1);
			st.getPlayer().addListener(LEVEL_CHANGE_LISTENER);
			if (st.getPlayer().isBaseClassActive() && st.getPlayer().getLevel() >= 100)
				st.set("CUSTOM_LOG", 1);
		}
		else if (event.equalsIgnoreCase("lionel_hunter_q10817_08.htm"))
		{
			st.giveItems(45923, 1, false);
			st.giveItems(45925, 1, false);
			st.takeItems(45632, -1);
			st.takeItems(45628, -1);
			st.takeItems(45629, -1);
			st.takeItems(45630, -1);
			st.takeItems(45631, -1);
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
			case LEONEL:
				if (cond == 0)
					htmltext = "lionel_hunter_q10817_01.htm";
				else if (cond == 1 && !checkReward(st))
					htmltext = "lionel_hunter_q10817_06.htm";
				else if (cond == 2)
					htmltext = "lionel_hunter_q10817_07.htm";
				break;
		}
		return htmltext;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getLevel() > 99 && st.haveQuestItem(45628) && st.haveQuestItem(45629) && st.haveQuestItem(45630) && st.haveQuestItem(45631))
		{
			if (st.getCond() == 1)
			{
				st.setCond(2);
				st.getPlayer().removeListener(LEVEL_CHANGE_LISTENER);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
	}
}

