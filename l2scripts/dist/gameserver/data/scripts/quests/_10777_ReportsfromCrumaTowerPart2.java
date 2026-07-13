package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.Functions;

//By Evil_dnk

public class _10777_ReportsfromCrumaTowerPart2 extends Quest
{
	// NPC's
	private static final int BELKATI = 30485;
	private static final int MAGICOWL = 33991;

	//ITEMS
	private static final int ENCHANTARMOR = 23420;

	private NpcInstance owl = null;

	private static final int EXP_REWARD = 1257435;	private static final int SP_REWARD = 36; 	public _10777_ReportsfromCrumaTowerPart2()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(BELKATI);
		addTalkId(BELKATI);
		addTalkId(MAGICOWL);
		addQuestCompletedCheck("30485-0.htm", 10776);
		addLevelCheck("30485-0.htm", 49);
		addRaceCheck("30485-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30485-5.htm"))
		{
			owl = null;
			st.setCond(1);
		}

		else if(event.equalsIgnoreCase("30485-8.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}

		else if(event.equalsIgnoreCase("summonowl"))
		{
			if (owl == null)
				owl = st.addSpawn(MAGICOWL, 17666, 108589, -9072, 0, 0, 120000);
			return null;
		}
		else if(event.equalsIgnoreCase("sendowl"))
		{
			st.setCond(2);
			Functions.npcSay(owl, NpcString.TO_QUEEN_NAVARI_OF_FAERON);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				npc.doDie(null);
				npc.endDecayTask();
			}, 6000L);

			return null;
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
			case BELKATI:
				if(cond == 0)
					htmltext = "30485-1.htm";
				else if (cond == 1)
					htmltext = "30485-6.htm";
				else if (cond == 2)
					htmltext = "30485-7.htm";
				break;

			case MAGICOWL:
				if (cond == 1)
					htmltext = "33991-1.htm";
			break;
		}
		return htmltext;
	}
}