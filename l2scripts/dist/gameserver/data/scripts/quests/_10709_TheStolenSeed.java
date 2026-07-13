package quests;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

//By Evil_dnk

public class _10709_TheStolenSeed extends Quest
{
	// NPCs
	private static final int NOVIAN = 33866;
	private static final int CONTROL_DEVICE = 33961; // Magic Circle Control Device
	private static final int REMEMBERED_AKUM = 27524; // Remembered Giant Akum
	private static final int REMEMBERED_EMBRYO = 27525; // Remembered Embryo
	private static final int CURSED_AKUM = 27520; // Cursed Giant Akum
	// Items
	private static final int FRAGMENT = 39511; // Normal Fragment
	private static final int MEMORY_FRAGMENT = 39510; // Akum's Memory Fragment
	private static final int EAB = 948; // Scroll: Enchant Armor (B-grade)

	private static final int EXP_REWARD = 5598386;	private static final int SP_REWARD = 175; 	public _10709_TheStolenSeed()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(NOVIAN);
		addTalkId(NOVIAN, CONTROL_DEVICE);
		addKillId(CURSED_AKUM);
		addQuestItem(FRAGMENT, MEMORY_FRAGMENT);
		addLevelCheck(NOVIAN, "33866-08.htm", 58, 61);
		addQuestCompletedCheck(NOVIAN, "33866-08.htm", 10403);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33866-04.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("action"))
		{
			// Take items
			st.takeItems(MEMORY_FRAGMENT, -1);
			// Spawn + chat
			final NpcInstance akum = addSpawn(REMEMBERED_AKUM, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 4000);
			Functions.npcSay(akum, NpcString.ARGH_WHO_ISHIDING_THERE);
			final NpcInstance embryo = addSpawn(REMEMBERED_EMBRYO, akum.getX() + 100, akum.getY() + 100, akum.getZ(), 0, 0, 4000);
			Functions.npcSay(akum, NpcString.A_SMART_GIANT_HUH_WELL_HAND_IT_OVER_THE_KARTIAS_SEED_IS_OURS);
        	// Attack + invul
			akum.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, embryo, 10000);
			embryo.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, akum, 10000);
        	embryo.getFlags().getInvulnerable().start();
			akum.getFlags().getInvulnerable().start();
			st.setCond(2, true);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				akum.getTarget();
				if ((akum != null) && (st != null))
				{
					Functions.npcSay(embryo, NpcString.KARTIAS_SEED_GOT_IT);
					Functions.npcSay(akum, NpcString.ARGHH);
					Functions.npcSay(embryo, NpcString.YOU_WORTHLESS_GIANTCURSE_YOU_FOR_ETERNITY);
					final NpcInstance akumc = st.addSpawn(CURSED_AKUM, st.getPlayer().getX()+30, st.getPlayer().getY()+30, st.getPlayer().getZ(), 0, 0, 180000);
					akumc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
				}
			}, 10000);
			return "";
		}
		else if(event.equalsIgnoreCase("33866-07.htm"))
		{
			st.giveItems(57, 990000);
			st.giveItems(46851, 1, false);
			st.giveItems(1466, 6000);
			st.giveItems(3951, 6000);
			st.giveItems(33640, 3);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(FRAGMENT, -1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;

		if(npc.getNpcId() == NOVIAN)
		{
			if(st.getCond() == 0)
				htmtext = "33866-01.htm";
			else if(st.getCond() == 1 || st.getCond() == 2)
				htmtext = "33866-05.htm";
			else if(st.getCond() == 3)
				htmtext = "33866-06.htm";
		}
		else if(npc.getNpcId() == CONTROL_DEVICE)
		{
			if (st.getCond() == 1)
				htmtext = "33961-01.htm";
		}
		return htmtext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 2)
		{
			st.giveItems(FRAGMENT, 1, false);
			st.setCond(3);
		}
		return null;
	}
}
