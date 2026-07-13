package quests;

import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10530_KekropusLetterTheDragonsTransition extends Quest
{
	// NPC's
	private static final int JERONIN = 30121;
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
			QuestState questState = player.getQuestState(10530);
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
			QuestState questState = player.getQuestState(10530);
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

	public _10530_KekropusLetterTheDragonsTransition()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(JERONIN, NAMO);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 81, 84);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("jeronin_q10530_03.htm"))
		{
			st.giveItems(46732, 1, false);
			st.setCond(3);
		}

		else if(event.equalsIgnoreCase("dv_guide_namoo_q10530_03.htm"))
		{
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
			case JERONIN:
				if(cond == 2)
					htmltext = "jeronin_q10530_01.htm";
				else if(cond == 3)
					htmltext = "jeronin_q10530_04.htm";
				break;

			case NAMO:
				if (cond == 3)
					htmltext = "dv_guide_namoo_q10530_01.htm";
				break;
		}
		return htmltext;
	}
}