package quests;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class _10707_FlamesofSorrow extends Quest
{
	// NPC's
	private static final int LEO = 33863;
	private static final int FLAME = 19545;
	private static final int SPIRIT = 27518;
	private static final int GNOL = 33959;

	//Items
	private static final int ZNAK = 39508;

	private static final int EXP_REWARD = 6049417;	private static final int SP_REWARD = 378; 	public _10707_FlamesofSorrow()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(LEO);
		addTalkId(LEO);
		addTalkId(FLAME);
		addTalkId(GNOL);
		addKillId(SPIRIT);
		addQuestItem(ZNAK);
		addLevelCheck("refugee_leo_q10707_02.htm", 46/*, 51*/);
		addQuestCompletedCheck("refugee_leo_q10707_02.htm", 10395);
		addRaceCheck("refugee_leo_q10707_02.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("refugee_leo_q10707_04.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("refugee_leo_q10707_07.htm"))
		{
			st.takeItems(ZNAK, -1);
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
			case LEO:
				if (cond == 0)
					htmltext = "refugee_leo_q10707_01.htm";
				else if (cond == 1)
					htmltext = "refugee_leo_q10707_05.htm";
				else if (cond == 3)
					htmltext = "refugee_leo_q10707_06.htm";
				break;

			case FLAME:
				if (cond == 1 || cond == 2)
				{
					if(cond == 1)
						st.setCond(2);
					NpcInstance spirit = NpcUtils.spawnSingle(SPIRIT, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0);
					npc.doDie(null);
					spirit.getAggroList().addDamageHate(st.getPlayer(), 0, 1);
					spirit.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK);
					Functions.npcSay(spirit, NpcString.THE_WAR_IS_NOT_YET_OVER);
					htmltext = "";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if((cond == 1 || cond == 2) && st.getQuestItemsCount(ZNAK) < 5)
		{
			if(st.rollAndGive(ZNAK, 1, 1, 5, 60))
				st.setCond(3);
			else
				st.addSpawn(33959, npc.getX(), npc.getY(), npc.getZ(), 5000);
		}
		return null;
	}
}