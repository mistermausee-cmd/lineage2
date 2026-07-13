package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnTeleportListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.utils.PositionUtils;

//By Evil_dnk

public class _10829_InSearchOfTheCause extends Quest
{
	private class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState st = player.getQuestState(10829);
			if(st == null)
				return;

			if(st.getCond() == 1)
				player.addListener(TELEPORT_LISTENER);
		}
	}

	private class TeleportListener implements OnTeleportListener
	{
		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			QuestState st = player.getQuestState(10829);
			if(st == null)
				return;

			if(st.getCond() == 1  && PositionUtils.checkIfInRange(1500, x, y, -48154, 69462))
			{
				st.setCond(2);
				player.removeListener(TELEPORT_LISTENER);
			}
		}
	}

	// NPC's
	private static final int NPC1 = 30657;
	private static final int NPC2 = 34054;
	private static final int NPC3 = 34055;

	// Item's
	private static final long EXP_REWARD = 55369440;
	private static final long SP_REWARD = 132885;
	private static final int REWARD1 = 46158;

	private final TeleportListener TELEPORT_LISTENER = new TeleportListener();

	private final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();	public _10829_InSearchOfTheCause()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addTalkId(NPC2);
		addTalkId(NPC3);
		addLevelCheck("cardinal_seresin_q10829_02.htm", 100);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("cardinal_seresin_q10829_05.htm"))
		{
			st.setCond(1);
			st.getPlayer().addListener(TELEPORT_LISTENER);
		}
		else if (event.equalsIgnoreCase("el_apple_de_khan_q10829_03.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("cyphona_q10829_03.htm"))
		{
			st.giveItems(REWARD1, 1);
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
			case NPC1:
				if (cond == 0)
					htmltext = "cardinal_seresin_q10829_01.htm";
				else if (cond == 1 || cond == 2 || cond == 3 || cond == 4)
					htmltext = "el_apple_de_khan_q10833_06.htm";
				break;

			case NPC2:
				if (cond == 2 || cond == 3)
					htmltext = "el_apple_de_khan_q10829_01.htm";
				else if (cond == 4 || cond == 1)
					htmltext = "el_apple_de_khan_q10829_04.htm";
				break;

			case NPC3:
				if (cond == 4)
					htmltext = "cyphona_q10829_02.htm";
				else if (cond == 1 || cond == 2 || cond == 3)
					htmltext = "cyphona_q10829_01.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
	}
}