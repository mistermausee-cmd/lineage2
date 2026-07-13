package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10791_TheManOfMystery extends Quest
{
	// NPCs
	private static final int DOKARA = 33847;
	private static final int KAIN_VAN_HALTER = 33993;

	// Monster
	private static final int[] SUSPICIOUS_COCOON = {27536, 27537, 27538};
	private static final int NEEDLE_STAKATO_CAPTAIN = 27542;
	private static final int NEEDLE_STAKATO = 27543;

	private static final String A_LIST = "a_list";
	private NpcInstance helper = null;

	// Reward
	private static final int EXP_REWARD = 46334481;
	private static final int SP_REWARD = 4072;


	public _10791_TheManOfMystery()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(DOKARA);
		addTalkId(DOKARA);
		addKillId(NEEDLE_STAKATO_CAPTAIN);
		addKillNpcWithLog(1, 1027536, A_LIST, 5, SUSPICIOUS_COCOON);
		addLevelCheck("chaser_dokara_q10791_02.htm", 65/*, 70*/);
		addClassIdCheck("chaser_dokara_q10791_02.htm", 182, 184, 186, 188, 190);
		addRaceCheck("chaser_dokara_q10791_02a.htm", Race.ERTHEIA);
		addQuestCompletedCheck("chaser_dokara_q10791_02", 10790);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("chaser_dokara_q10791_05.htm"))
		{
			st.setCond(1);
		}

		else if (event.equalsIgnoreCase("chaser_dokara_q10791_08.htm"))
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
			case DOKARA:
				if (cond == 0)
					htmltext = "chaser_dokara_q10791_01.htm";
				else if (cond == 1 || cond == 2)
					htmltext = "chaser_dokara_q10791_06.htm";
				else if (cond == 3)
					htmltext = "chaser_dokara_q10791_07.htm";
				break;

			case KAIN_VAN_HALTER:
				if (cond == 1)
					htmltext = "q_kain_doe_q10791_01.htm";
				else if (cond == 2)
					htmltext = "q_kain_doe_q10791_04.htm";
				else if (cond == 3)
					htmltext = "q_kain_doe_q10791_05.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			boolean doneKill = updateKill(npc, st);
			if (doneKill)
			{
				st.unset(A_LIST);
				st.addSpawn(NEEDLE_STAKATO_CAPTAIN, npc.getX() + Rnd.get(-20, 20), npc.getY() + Rnd.get(-20, 20), npc.getZ(), 0, 0, 600000);
				st.setCond(2);
			}

			for (int i = 0; i < 5; i++)
			{
				final NpcInstance creature = st.addSpawn(NEEDLE_STAKATO, npc.getX() + Rnd.get(-20, 20), npc.getY() + Rnd.get(-20, 20), npc.getZ(), 0, 0, 120000);
				creature.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000000);
			}

			if(helper == null)
			{
				helper = addSpawn(KAIN_VAN_HALTER, npc.getX() + Rnd.get(-100, 100), npc.getY() + Rnd.get(-100, 100), npc.getZ(), 0, 0, 120000);
				helper.setFollowTarget(st.getPlayer());
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
			}
		}
		if (cond == 2)
		{
			if(npc.getNpcId() == NEEDLE_STAKATO_CAPTAIN)
			{
				if(helper != null)
					helper.setFollowTarget(null);
				st.setCond(3);
			}
		}
		return null;
	}
}