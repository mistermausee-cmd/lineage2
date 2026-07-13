package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Quest "Request to Find Sakum"
 *
 * @author 
 */
public class _10335_RequestToFindSakum extends Quest
{
	private static final int BATHIS = 30332;
	private static final int KALLESIN = 33177;
	private static final int ZENATH = 33509;

	private static final int SKELETON_TRACKER = 20035;
	private static final int SKELETON_BOWMAN = 20051;
	private static final int SKELETON_SCOUT = 20045;
	private static final int RUIN_SPARTOI = 20054;
	private static final int RUIN_ZOMBIE = 20026;
	private static final int RUIN_ZOMBIE_LEADER = 20029;

	public static final String TRACKER = "TRACKER";
	public static final String BOWMAN = "BOWMAN";
	public static final String SPARTOI = "SPARTOI";
	public static final String ZOMBIE = "ZOMBIE";

	private static final int EXP_REWARD = 350000;	private static final int SP_REWARD = 84; 	public _10335_RequestToFindSakum()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(BATHIS);
		addTalkId(KALLESIN, ZENATH);
		addKillNpcWithLog(2, BOWMAN, 10, SKELETON_BOWMAN, SKELETON_SCOUT);
		addKillNpcWithLog(2, SPARTOI, 15, RUIN_SPARTOI);
		addKillNpcWithLog(2, TRACKER, 10, SKELETON_TRACKER);
		addKillNpcWithLog(2, ZOMBIE, 15, RUIN_ZOMBIE, RUIN_ZOMBIE_LEADER);
		addRaceCheck("bathis_q10335_0.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("bathis_q10335_0.htm", 23/*, 40*/);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;

		int cond = qs.getCond();
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "bathis_q10335_3.htm";
			qs.setCond(1);
		}
		else if(cond == 1 && event.equalsIgnoreCase("kallesin_accept"))
		{
			htmltext = "kallesin_q10335_2.htm";
			qs.setCond(2);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "zenath_q10335_3.htm";
			qs.addExpAndSp(EXP_REWARD, SP_REWARD);
			qs.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = qs.getCond();
		switch(npcId)
		{
			case BATHIS:
				if(cond == 0)
					htmltext = "bathis_q10335_1.htm";
				else if(cond == 1 || cond == 2 || cond == 3)
					htmltext = "bathis_q10335_3.htm";
				else
				break;
			case KALLESIN:
				if(cond == 1)
					htmltext = "kallesin_q10335_1.htm";
				break;
			case ZENATH:
				if(cond == 3)
					htmltext = "zenath_q10335_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == BATHIS)
			htmltext = "bathis_q10335_taken.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return "";

		if(updateKill(npc, qs)) 
		{
			qs.unset(BOWMAN);
			qs.unset(SPARTOI);
			qs.unset(TRACKER);
			qs.unset(ZOMBIE);
			qs.setCond(3);
		}

		return "";
	}
}
