package quests;

import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

//By Evil_dnk

public class _10785_LettersfromtheQueenFieldsofMassacre extends Quest
{
	// NPC's
	private static final int ORVEN = 30857;
	private static final int ZUBAN = 33867;

	private static final int EXP_REWARD = 807240;
	private static final int SP_REWARD = 193;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10785);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 64)
			{
				if (!questState.isCompleted())
				{
					questState.abortQuest();
				}
			}
		}
	}

	private static class ChangeLevelListener implements OnLevelChangeListener
	{
		@Override
		public void onLevelChange(Player player, int was, int set)
		{
			QuestState questState = player.getQuestState(10785);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 64)
			{
				if (!questState.isCompleted())
				{
					questState.abortQuest();
				}
			}
		}
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
		CharListenerList.addGlobal(LEVEL_UP_LISTENER);
	}

	public _10785_LettersfromtheQueenFieldsofMassacre()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(ORVEN);
		addTalkId(ZUBAN);
		addLevelCheck(NO_QUEST_DIALOG, 61, 64);
		addRaceCheck(NO_QUEST_DIALOG, Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30857-3.htm"))
		{
			st.giveItems(39579, 1, false);
			st.setCond(3);
		}

		else if(event.equalsIgnoreCase("33867-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_65, 5000, ScreenMessageAlign.TOP_CENTER, false));
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
			case ORVEN:
				if(cond == 2)
					htmltext = "30857-1.htm";
				else if(cond == 3)
					htmltext = "30857-4.htm";
			break;

			case ZUBAN:
				if (cond == 3)
					htmltext = "33867-1.htm";
			break;
		}
		return htmltext;
	}
}