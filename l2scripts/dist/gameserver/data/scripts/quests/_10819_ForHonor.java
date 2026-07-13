package quests;

import l2s.gameserver.listener.actor.player.OnChaosFestivalFinishBattleListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10819_ForHonor extends Quest
{
	// NPC's
	private static final int OLYMPMANAGER = 31688;

	// Item's
	private static final int PROOF = 45873;
	private static final int CERTIF2 = 45629;

	private static final QuestListeners QUEST_LISTENERS = new QuestListeners();

	public _10819_ForHonor()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(OLYMPMANAGER);
		addTalkId(OLYMPMANAGER);
		addQuestItem(PROOF);

		addLevelCheck("olympiad_operator_q10819_02.htm", 99);
		addNobleCheck("olympiad_operator_q10819_02.htm", true);
		addItemHaveCheck("olympiad_operator_q10819_03.htm", 45632, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("olympiad_operator_q10819_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("olympiad_operator_q10819_09.htm"))
		{
			st.takeItems(PROOF, -1);
			st.giveItems(CERTIF2, 1, false);
			st.giveItems(45945, 180);
			if (checkReward(st))
				htmltext = "olympiad_operator_q10819_10.htm";
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
			case OLYMPMANAGER:
				if (cond == 0)
					htmltext = "olympiad_operator_q10819_01.htm";
				else if (cond == 1 && st.getQuestItemsCount(PROOF) > 99)
				{
					st.setCond(2);
					htmltext = "olympiad_operator_q10819_08.htm";
				}
				else if (cond == 1)
					htmltext = "olympiad_operator_q10819_07.htm";
				else if (cond == 2)
					htmltext = "olympiad_operator_q10819_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public void onOlympiadEnd(OlympiadGame og, QuestState qs)
	{
		if (qs.getCond() == 1)
		{
			qs.giveItems(PROOF, 1);
			if (qs.getQuestItemsCount(PROOF) >= 100)
			{
				qs.setCond(2);
			}
		}
	}

	private static class QuestListeners implements OnChaosFestivalFinishBattleListener
	{
		@Override
		public void onChaosFestivalFinishBattle(Player player, boolean winner)
		{
			if (player != null)
			{
				QuestState qs = player.getQuestState(10819);
				if ((qs != null) && (qs.isStarted()))
				{
					if (qs.getCond() == 1)
					{
						qs.giveItems(PROOF, 1);
						if (qs.getQuestItemsCount(PROOF) >= 100)
						{
							qs.setCond(2);
						}
					}
				}
			}
		}
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(QUEST_LISTENERS);
	}


	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getLevel() > 99 && st.haveQuestItem(45628) && st.haveQuestItem(45629) && st.haveQuestItem(45630) && st.haveQuestItem(45631))
		{
			st.getPlayer().getQuestState(10817).setCond(2);
			return true;
		}
		return false;
	}

}


