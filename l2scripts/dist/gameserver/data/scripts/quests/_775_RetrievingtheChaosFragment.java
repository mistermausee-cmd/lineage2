package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _775_RetrievingtheChaosFragment extends Quest
{
	// NPC's
	private static final int LEONBL = 31595;

	// Monster's
	private static final int[] MONSTERS = {23354, 23362, 23363, 23364, 23811, 23812, 23813, 23814, 23815, 23365, 23355, 23367, 23356, 23368, 23357, 23369, 23358, 23370, 23360, 23372, 23361, 23373};

	// Item's
	private static final int CHAOSFRAGMENTS = 37766;

	private static final long EXP_REWARD_LOW = 5426843495l;
	private static final int SP_REWARD_LOW = 13024390;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 8140265242l;
	private static final int SP_REWARD_MEDIUM = 19536585;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 10853686990l;
	private static final int SP_REWARD_HIGH = 26048780;
	private static final int FP_REWARD_HIGH = 300;

	public _775_RetrievingtheChaosFragment()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(LEONBL);
		addTalkId(LEONBL);
		addKillId(MONSTERS);
		addQuestItem(CHAOSFRAGMENTS);
		addLevelCheck("lionna_blackbird_q0775_02.htm", 99);
		addQuestCompletedCheck("lionna_blackbird_q0775_02.htm", 10455);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("lionna_blackbird_q0775_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.BLACKBIRD_PLEDGE) == 1)
				htmltext = "lionna_blackbird_q0775_05a.htm";
			else if (st.getPlayer().getFactionList().getLevel(FactionType.BLACKBIRD_PLEDGE) >= 2)
				htmltext = "lionna_blackbird_q0775_05b.htm";
			else
				htmltext = "lionna_blackbird_q0775_05.htm";
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q0775_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q0775_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q0775_10b.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q0775_13.htm"))
		{
			st.giveItems(47356, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.BLACKBIRD_PLEDGE, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q0775_13a.htm"))
		{
			st.giveItems(47357, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.BLACKBIRD_PLEDGE, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("lionna_blackbird_q0775_13b.htm"))
		{
			st.giveItems(47358, 1);
			st.addExpAndSp(EXP_REWARD_HIGH, SP_REWARD_HIGH);
			st.getPlayer().getFactionList().addProgress(FactionType.BLACKBIRD_PLEDGE, FP_REWARD_HIGH);
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
			case LEONBL:
				if (cond == 0)
					htmltext = "lionna_blackbird_q0775_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.BLACKBIRD_PLEDGE) == 1)
						htmltext = "lionna_blackbird_q0775_05a.htm";
					else if (st.getPlayer().getFactionList().getLevel(FactionType.BLACKBIRD_PLEDGE) >= 2)
						htmltext = "lionna_blackbird_q0775_05b.htm";
					else
						htmltext = "lionna_blackbird_q0775_05.htm";
				}
				else if (cond == 2)
					htmltext = "lionna_blackbird_q0775_11.htm";
				else if (cond == 3)
					htmltext = "lionna_blackbird_q0775_11a.htm";
				else if (cond == 4)
					htmltext = "lionna_blackbird_q0775_11b.htm";
				else if (cond == 5)
					htmltext = "lionna_blackbird_q0775_12.htm";
				else if (cond == 6)
					htmltext = "lionna_blackbird_q0775_12a.htm";
				else if (cond == 7)
					htmltext = "lionna_blackbird_q0775_12b.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(CHAOSFRAGMENTS, 1, 1, 250, 60)) //TODO CHANCE?
				qs.setCond(5);
		}
		else if(qs.getCond() == 3)
		{
			if(qs.rollAndGive(CHAOSFRAGMENTS, 1, 1, 500, 60)) //TODO CHANCE?
				qs.setCond(6);
		}
		else if(qs.getCond() == 4)
		{
			if(qs.rollAndGive(CHAOSFRAGMENTS, 1, 1, 750, 60)) //TODO CHANCE?
				qs.setCond(7);
		}
		return null;
	}
}