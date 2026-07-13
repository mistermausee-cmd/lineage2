package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 * Reworked by Evil_dnk
 */
public class _758_TheFallenKingsMen extends Quest
{
	//npc
	private static final int INTENDANT = 33407;
	private static final int CHANCE = 20;
	//q_items
	private static final int TRAVIS_MARK = 36392;
	private static final int REPATRIAT_SOUL = 36393;
	//rewards
	private static final int EscortBox = 36394;
	//mobs
	private static final int[] MOBS = { 19455, 23296, 23294, 23292, 23291, 23290, 23300, 23299, 23298, 23297, 23295, 23293 };

	public _758_TheFallenKingsMen()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(INTENDANT);
		addTalkId(INTENDANT);
		addQuestItem(TRAVIS_MARK);
		addQuestItem(REPATRIAT_SOUL);
		
		addKillId(MOBS);
		
		addLevelCheck("no_level.htm", 97);
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
			st.takeItems(TRAVIS_MARK, 50L);
			st.takeItems(REPATRIAT_SOUL, 50L);
			if(st.getQuestItemsCount(REPATRIAT_SOUL) < 100)
			{
				st.addExpAndSp(3015185490L, 7236360);
				st.giveItems(EscortBox, 1);
				st.giveItems(57, 1017856);
			}
			else if(st.getQuestItemsCount(REPATRIAT_SOUL) >= 100 && st.getQuestItemsCount(REPATRIAT_SOUL) <= 199)
			{
				st.addExpAndSp(6030370980L, 14472720);
				st.giveItems(EscortBox, 2);
				st.giveItems(57, 2035712);
			}
			else if(st.getQuestItemsCount(REPATRIAT_SOUL) >= 200 && st.getQuestItemsCount(REPATRIAT_SOUL) <= 299)
			{
				st.addExpAndSp(9045556470L, 21709080);
				st.giveItems(EscortBox, 3);
				st.giveItems(57, 3053568);
			}
			st.finishQuest();
			htmltext = "7.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == INTENDANT)
		{
			if(cond == 0)
				return "1.htm";
			else if(cond == 1)
				return "no_ingrid.htm";
			else if(cond == 2)
				return "5.htm";
			else if(cond == 3)
			{
				st.finishQuest();
				st.takeItems(REPATRIAT_SOUL, -1L);
				st.takeItems(TRAVIS_MARK, -1L);
				st.addExpAndSp(12060741960L, 28945440);
				st.giveItems(EscortBox, 4);
				st.giveItems(57, 4071424);
				return "7.htm";
			}	
		}
			
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == INTENDANT)
			htmltext = "no_aval.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs == null)
			return null;
		if(qs.getCond() != 1 && qs.getCond() != 2)
			return null;
		if(!ArrayUtils.contains(MOBS, npcId))	
			return null;
		if(qs.getCond() == 1)
		{
			if(Rnd.chance(CHANCE))
			{
				qs.giveItems(TRAVIS_MARK, 1);
				if (qs.getQuestItemsCount(TRAVIS_MARK) >= 100L)
					qs.setCond(2);
			}
		}
		else if(qs.getCond() == 2)
		{
			if(Rnd.chance(CHANCE))
			{
				qs.giveItems(REPATRIAT_SOUL, 1);
				if (qs.getQuestItemsCount(REPATRIAT_SOUL) >= 300L)
					qs.setCond(3);
			}
		}
		return null;
	}
}