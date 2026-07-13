package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10398_ASuspiciousBadge extends Quest
{
	//npc
	private static final int ANDY = 33845;
	private static final int BACON = 33846;
	//quest_items
	private static final int UNIDENTIFIED_SUSPICIOUS_BADGE = 36666;
	//mobs
	private static final int[] MOBS = {20555, 20558, 23305, 23306, 23307, 23308};
	private static final int EXP_REWARD = 6135787;	private static final int SP_REWARD = 914; 	public _10398_ASuspiciousBadge()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(ANDY);
		addTalkId(ANDY);
		addTalkId(BACON);
		addQuestItem(UNIDENTIFIED_SUSPICIOUS_BADGE);
		addKillId(MOBS);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("no_level.htm", 52/*, 58*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(UNIDENTIFIED_SUSPICIOUS_BADGE, 20L);
			st.giveItems(948, 5);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npcId == ANDY)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == BACON)
		{
			if(cond == 2)
				htmltext = "1-1.htm";		
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		int cond = qs.getCond();

		if(ArrayUtils.contains(MOBS, npcId))
		{
			if(cond == 1)
			{
				qs.giveItems(UNIDENTIFIED_SUSPICIOUS_BADGE, 1L);
				if(qs.getQuestItemsCount(UNIDENTIFIED_SUSPICIOUS_BADGE) >= 50L)
					qs.setCond(2);
			}
		}
		return null;
	}
}