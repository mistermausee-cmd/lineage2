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

/**
 * @author Iqman
 */
public class _10415_WithWisdom extends Quest
{
    //Квестовые персонажи
    private static final int ANDREI = 31292;
    private static final int JANIT = 33851;

	private static final int EXP_REWARD = 1088640;
	private static final int SP_REWARD = 261;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10415);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 75)
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
			QuestState questState = player.getQuestState(10415);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 75)
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

	public _10415_WithWisdom()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(ANDREI);
		addTalkId(JANIT);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 70, 75);
		addClassTypeCheck(NO_QUEST_DIALOG, ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(3);
			st.giveItems(37032, 1);
		}				
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_76, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		
		if(npcId == ANDREI)
		{
			if(cond == 2)
				htmltext = "captain_mathias_q10408_01.htm";
			else if(cond == 3)
				htmltext = "accept.htm";
		}
		else if(npcId == JANIT)
		{
			if(cond == 3)
				htmltext = "tracker_dokara_q10408_01.htm";
		}
		return htmltext;
	}
}