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

public class _10798_LettersfromtheQueenDragonValley extends Quest
{
	// NPC's
	private static final int MAXIMIL = 30120;
	private static final int NAMO = 33973;

	private static final int EXP_REWARD = 1277640;
	private static final int SP_REWARD = 306;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10798);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 84)
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
			QuestState questState = player.getQuestState(10798);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 84)
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

	public _10798_LettersfromtheQueenDragonValley()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(MAXIMIL);
		addTalkId(NAMO);
		addLevelCheck(NO_QUEST_DIALOG, 81, 84);
		addRaceCheck(NO_QUEST_DIALOG, Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30120-3.htm"))
		{
			st.giveItems(39587, 1, false);
			st.setCond(3);
		}

		else if(event.equalsIgnoreCase("33973-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_FINISHED_ALL_OF_QUEEN_NAVARIS_LETTERS_GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_LETTERS_FROM_A_MINSTREL_AT_LV_85, 5000, ScreenMessageAlign.TOP_CENTER, false));
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
			case MAXIMIL:
				if(cond == 2)
					htmltext = "30120-1.htm";
				else if(cond == 3)
					htmltext = "30120-4.htm";
			break;

			case NAMO:
				if (cond == 3)
					htmltext = "33973-1.htm";
			break;
		}
		return htmltext;
	}
}