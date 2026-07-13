package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

//TODO Make Listener for Exp gaining and giving 36077!!!
public class _500_BrothersBoundInChains extends Quest
{
	// NPC's
	private static final int NPC1 = 30981;

	// Monster's
	private static final int[] MONSTERS_A = {};

	// Item's
	private static final int DROPITEM = 36077;

	private static final int PENITENT85 = 36060;
	private static final int PENITENT90 = 36061;
	private static final int PENITENT95 = 36062;
	private static final int PENITENT61 = 36063;
	private static final int PENITENT76 = 36064;

	private static int PKCOUNT = 1;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(500);
			if(questState == null)
				return;

			if(questState.getCond() == 1)
			{
				//player.addListener(EXP_GET_LISTENER);
			}
		}
	}

	public _500_BrothersBoundInChains()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addQuestItem(DROPITEM);
		addKillId(MONSTERS_A);
		addLevelCheck("black_judge_q0500_02.htm", 61);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("black_judge_q0500_08.htm"))
		{
			if(st.getQuestItemsCount(2132) < 200)
				return "black_judge_q0500_06.htm";
			st.setCond(1);
			if(st.getPlayer().getLevel() < 76 && !st.haveQuestItem(PENITENT61))
				st.giveItems(PENITENT61, 1, false);
			else if(st.getPlayer().getLevel() < 85 && !st.haveQuestItem(PENITENT76))
				st.giveItems(PENITENT76, 1, false);
			else if(st.getPlayer().getLevel() < 90 && !st.haveQuestItem(PENITENT85))
				st.giveItems(PENITENT85, 1, false);
			else if(st.getPlayer().getLevel() < 95 && !st.haveQuestItem(PENITENT90))
				st.giveItems(PENITENT90, 1, false);
			else if(st.getPlayer().getLevel() >= 95 && !st.haveQuestItem(PENITENT95))
				st.giveItems(PENITENT95, 1, false);

		}
		else if (event.equalsIgnoreCase("black_judge_q0500_07.htm"))
		{
			if(st.getQuestItemsCount(2132) < 200)
	            return "black_judge_q0500_06.htm";
		}
		else if (event.equalsIgnoreCase("black_judge_q0500_12.htm"))
		{
			if(!st.getPlayer().isBaseClassActive())
				return "black_judge_q0500_13.htm";
			st.takeItems(PENITENT85, -1);
			st.takeItems(PENITENT90, -1);
			st.takeItems(PENITENT95, -1);
			st.takeItems(PENITENT61, -1);
			st.takeItems(PENITENT76, -1);
			st.takeItems(DROPITEM, -1);
			if(Rnd.chance(50))
				PKCOUNT = Rnd.get(2,5);
			else if(Rnd.chance(25))
				PKCOUNT = Rnd.get(6,9);
			else if(Rnd.chance(7))
				PKCOUNT = 10;
			if (st.getPlayer().getPkKills() > 0)
			{
				if (st.getPlayer().getPkKills() - PKCOUNT < 0)
					st.getPlayer().setPkKills(0);
				else
					st.getPlayer().setPkKills(st.getPlayer().getPkKills() - PKCOUNT);
				st.getPlayer().broadcastCharInfo();
			}
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
			case NPC1:
				if (cond == 0)
					htmltext = "black_judge_q0500_01.htm";
				else if (cond == 1 && st.getQuestItemsCount(DROPITEM) < 10)
					htmltext = "black_judge_q0500_09.htm";
				else if (cond == 1 && st.getQuestItemsCount(DROPITEM) >= 10)
				{
					if(!st.getPlayer().isBaseClassActive())
						return "black_judge_q0500_13.htm";

					htmltext = "black_judge_q0500_10.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getPkKills() == 0)
			return "black_judge_q0500_02.htm";

		return super.checkStartCondition(npc, player);
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == NPC1)
			htmltext = "black_judge_q0500_03.htm";
		return htmltext;
	}

}