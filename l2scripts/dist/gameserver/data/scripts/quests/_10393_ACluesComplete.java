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

/**
 * @author Iqman
 */
public class _10393_ACluesComplete extends Quest
{
	//npc
	private static final int FLUTTER = 30677;
	private static final int KELIOS = 33862;
	
	private static final int EXP_REWARD = 483840;
	private static final int SP_REWARD = 116;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10393);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 51)
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
			QuestState questState = player.getQuestState(10393);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 51)
			{
				if (!questState.isCompleted())
				{
					questState.abortQuest();
				}
			}
		}
	}

	public _10393_ACluesComplete()
	{
		super(PARTY_NONE, ONETIME, false);
		addTalkId(FLUTTER);
		addTalkId(KELIOS);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 46, 51);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(3);
			st.giveItems(37026, 1);
		}		
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_52, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.takeItems(37026, 1);
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
		
		if(npcId == FLUTTER)
		{
			if(cond == 2)
				htmltext = "1.htm";
		}
		else if(npcId == KELIOS)
		{
			if(cond == 3)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
		CharListenerList.addGlobal(LEVEL_UP_LISTENER);
	}
}