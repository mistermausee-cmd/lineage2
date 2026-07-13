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

public class _10760_LettersfromtheQueenOrcBarracks extends Quest
{
	// NPC's
	private static final int LEVIAN = 30037;
	private static final int PIOTUR = 30597;

	private static final int EXP_REWARD = 242760;
	private static final int SP_REWARD = 58;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10760);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 39)
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
			QuestState questState = player.getQuestState(10760);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 39)
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

	public _10760_LettersfromtheQueenOrcBarracks()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(PIOTUR);
		addTalkId(LEVIAN);
		addLevelCheck(NO_QUEST_DIALOG, 30, 39);
		addRaceCheck(NO_QUEST_DIALOG, Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30037-3.htm"))
		{
			st.giveItems(39487, 1, false);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TRY_USING_THE_TELEPORT_SCROLL_LEVIAN_GAVE_YOU_TO_GO_TO_ORC_BARRACKS, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.setCond(3);
		}

		else if(event.equalsIgnoreCase("30597-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TRY_TALKING_TO_VORBOS_BY_THE_WELLNYOU_CAN_RECEIVE_QUEEN_NAVARIS_NEXT_LETTER_AT_LV_40, 5000, ScreenMessageAlign.TOP_CENTER, false));
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
			case LEVIAN:
				if(cond == 2)
					htmltext = "30037-1.htm";
			break;

			case PIOTUR:
				if (cond == 3)
					htmltext = "30597-1.htm";
			break;
		}
		return htmltext;
	}
}