package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _753_ForcedSituation extends Quest
{
	//q items
	private static final int KEY = 36054;
	//reward items
	private static final int SCROLL = 36082;

	private static final int BERNA = 33796;

	public static final String A_LIST = "A_LIST";


	private static final int EXP_REWARD = 408665250;	private static final int SP_REWARD = 98079; 	public _753_ForcedSituation()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(BERNA);
		addTalkId(BERNA);
		addQuestItem(KEY);
		addSkillUseId(19296);
		addKillId(23270, 23271, 23272, 23272, 23274, 23275, 23276, 19296);
		addKillNpcWithLog(1, 539354, A_LIST, 5, 19296);

		addLevelCheck("You cannot procceed with this quest until you have completed the Mystrerious Journey quest!", 93);
		addQuestCompletedCheck("You cannot procceed with this quest until you have completed the Mystrerious Journey quest!", 10386);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
			st.set("q753doneKill", 0);
			if(!st.haveQuestItem(36065))
				st.giveItems(36065, 1);
		}

		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().unsetVar("q753doneKill");
			st.takeAllItems(KEY);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(SCROLL, 1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if(npcId == BERNA)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "notcollected.htm";
			else if(cond == 2)
				htmltext = "collected.htm";
		}

		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == BERNA)
			htmltext = "You have completed this quest today, come back tomorow at 6:30!";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs == null)
			return null;
		if(qs.getCond() != 1)
			return null;

		if(qs.getQuestItemsCount(KEY) < 30 && Rnd.chance(10))
			qs.giveItems(KEY, 1);
		if(qs.getQuestItemsCount(KEY) >= 30 && qs.getInt("A_LIST") >= 5)
			qs.setCond(2);

		return null;
	}

	@Override
	public String onSkillUse(NpcInstance npc, Skill skill, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1 && (skill.getId() == 9584))
		{
			if (npc.getNpcId() == 19296 && st.getInt("A_LIST") < 5)
			{
				if(updateKill(npc, st))
				{
					if (st.getQuestItemsCount(KEY) >= 30)
						st.setCond(2);
				}
			}
		}
		return null;
	}

}