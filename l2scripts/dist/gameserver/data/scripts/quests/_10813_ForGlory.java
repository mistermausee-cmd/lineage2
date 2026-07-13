package quests;

import l2s.gameserver.listener.actor.player.OnChaosFestivalFinishBattleListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10813_ForGlory extends Quest
{
	// NPC's
	private static final int MISTERY = 33685;

	// Item's
	private static final int PROOF = 45872;
	private static final int CERTIF2 = 45624;

	private static final QuestListeners QUEST_LISTENERS = new QuestListeners();

	public _10813_ForGlory()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(MISTERY);
		addTalkId(MISTERY);
		addQuestItem(PROOF);

		addLevelCheck("grankain_lumiere_q10813_02.htm", 99);
		addNobleCheck("grankain_lumiere_q10813_02.htm", true);
		addItemHaveCheck("grankain_lumiere_q10813_03.htm", 45627, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("grankain_lumiere_q10813_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("grankain_lumiere_q10813_09.htm"))
		{
			st.takeItems(PROOF, -1);
			st.giveItems(CERTIF2, 1, false);
			st.giveItems(45945, 120);
			if (checkReward(st))
				htmltext = "grankain_lumiere_q10813_10.htm";
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
			case MISTERY:
				if (cond == 0)
					htmltext = "grankain_lumiere_q10813_01.htm";
				else if (cond == 1)
					htmltext = "grankain_lumiere_q10813_07.htm";
				else if (cond == 2)
					htmltext = "grankain_lumiere_q10813_08.htm";
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
			if (qs.getQuestItemsCount(PROOF) >= 80)
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
				QuestState qs = player.getQuestState(10813);
				if ((qs != null) && (qs.isStarted()))
				{
					if (qs.getCond() == 1)
					{
						qs.giveItems(PROOF, 1);
						if (qs.getQuestItemsCount(PROOF) >= 80)
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
		if (st.getPlayer().getUsedAbilitiesPoints() >= 16 && st.haveQuestItem(45623) && st.haveQuestItem(45624) && st.haveQuestItem(45625) && st.haveQuestItem(45626))
		{
			st.getPlayer().getQuestState(10811).setCond(3);
			return true;
		}
		return false;
	}

}


