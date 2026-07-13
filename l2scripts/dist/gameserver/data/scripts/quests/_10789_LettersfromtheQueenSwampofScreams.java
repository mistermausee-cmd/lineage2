package quests;

import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

//By Evil_dnk

public class _10789_LettersfromtheQueenSwampofScreams extends Quest
{
	// NPC's
	private static final int INNOSENTIN = 31328;
	private static final int TAKARA = 33847;

	private static final int EXP_REWARD = 942690;
	private static final int SP_REWARD = 226;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10789);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 69)
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
			QuestState questState = player.getQuestState(10789);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 69)
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

	public _10789_LettersfromtheQueenSwampofScreams()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(INNOSENTIN);
		addTalkId(TAKARA);
		addClassIdCheck(NO_QUEST_DIALOG, 182, 184, 186, 188, 190 );
		addLevelCheck(NO_QUEST_DIALOG, 65, 69);
		addRaceCheck(NO_QUEST_DIALOG, Race.ERTHEIA);
		addClassTypeCheck(NO_QUEST_DIALOG, ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("highpriest_innocentin_q10789_03.htm"))
		{
			st.giveItems(39581, 1, false);
			st.setCond(3);
		}

		else if(event.equalsIgnoreCase("chaser_dokara_q10789_03.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_70, 5000, ScreenMessageAlign.TOP_CENTER, false));
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
			case INNOSENTIN:
				if(cond == 2)
					htmltext = "highpriest_innocentin_q10789_01.htm";
				else if(cond == 3)
					htmltext = "highpriest_innocentin_q10789_04.htm";
			break;

			case TAKARA:
				if (cond == 3)
					htmltext = "chaser_dokara_q10789_01.htm";
			break;
		}
		return htmltext;
	}
}