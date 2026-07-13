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
public class _10399_TheAlphabetOfTheGiants extends Quest
{
	//npc
	private static final int BACON = 33846;
	//quest_items
	private static final int BYKVA = 36667;
	//rewards
	
	//mobs
	private static final int[] MOBS = {23309, 23310};
	private static final int EXP_REWARD = 8779765;	private static final int SP_REWARD = 914; 	public _10399_TheAlphabetOfTheGiants()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(BACON);
		addTalkId(BACON);
		addQuestItem(BYKVA);
		addKillId(MOBS);
		addRaceCheck("This quest is only for 52-58 level and completed quest A Suspisious Badge", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("This quest is only for 52-58 level and completed quest A Suspisious Badge", 52/*, 58*/);
		addQuestCompletedCheck("This quest is only for 52-58 level and completed quest A Suspisious Badge", 10398);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npcId == BACON)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "3.htm";
			else if(cond == 2)
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.takeItems(BYKVA, 20L);
				st.finishQuest();
				return "endquest.htm";
			}
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
				qs.giveItems(BYKVA, 1L);
				if(qs.getQuestItemsCount(BYKVA) >= 50L)
					qs.setCond(2);
			}
		}
		return null;
	}
}