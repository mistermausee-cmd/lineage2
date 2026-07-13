package quests;

import l2s.gameserver.listener.actor.player.OnChaosFestivalFinishBattleListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10749_MysteriousSuggestion2 extends Quest
{
	private static class QuestListeners implements OnChaosFestivalFinishBattleListener
	{
		@Override
		public void onChaosFestivalFinishBattle(Player player, boolean winner)
		{
			if(player != null)
			{
				QuestState st = player.getQuestState(10748);
				if((st != null) && (st.isStarted()))
				{
					if(st.getQuestItemsCount(TOURNAMENT_REMNANTS_II) < 5L)
					{
						st.giveItems(TOURNAMENT_REMNANTS_II, 1);
						st.playSound(SOUND_ITEMGET);
						if(st.getQuestItemsCount(TOURNAMENT_REMNANTS_II) == 5L)
						{
							st.setCond(2);
						}
					}
				}
			}
		}
	}

	private static final QuestListeners QUEST_LISTENERS = new QuestListeners();

	// NPC'S
	private static final int MUSTERIOUS_BUTLER = 33685;

	// Item's
	private static final int TOURNAMENT_REMNANTS_II = 35551;
	private static final int MYSTERIOUS_MARK = 34903;
	private static final int MYSTERIOUS_MARK2 = 34904;
	private static final int MYSTERIOUS_SPEL = 36037;
	private static final int MYSTERIOUS_DEF = 36038;

	private static final int EXP_REWARD = 0;	private static final int SP_REWARD = 5640496; 						public _10749_MysteriousSuggestion2()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(MUSTERIOUS_BUTLER);
		addTalkId(MUSTERIOUS_BUTLER);
		addQuestItem(TOURNAMENT_REMNANTS_II);
		addLevelCheck("grankain_lumiere_q10749_04.htm", 76);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("grankain_lumiere_q10749_03.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == MUSTERIOUS_BUTLER)
		{
			if(cond == 0)
				htmltext = "grankain_lumiere_q10749_01.htm";
			else if(cond == 1)
				htmltext = "grankain_lumiere_q10749_06.htm";
			else if(cond == 2)
			{
				st.takeItems(TOURNAMENT_REMNANTS_II, 5);
				st.giveItems(MYSTERIOUS_MARK, 1);
				st.giveItems(MYSTERIOUS_MARK2, 1);
				st.giveItems(MYSTERIOUS_SPEL, 1);
				st.giveItems(MYSTERIOUS_DEF, 1);
				st.getPlayer().setFame(st.getPlayer().getFame() + 3000, "10748", true);
				if(st.getPlayer().getLevel() >= 99)
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
				htmltext = "grankain_lumiere_q10749_07.htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == MUSTERIOUS_BUTLER)
			htmltext = "grankain_lumiere_q10749_05.htm";
		return htmltext;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player == null || player.getClan() == null || player.getClan().getLevel() <= 3)
			return "grankain_lumiere_q10749_04.htm";
		return super.checkStartCondition(npc, player);
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(QUEST_LISTENERS);
	}
}