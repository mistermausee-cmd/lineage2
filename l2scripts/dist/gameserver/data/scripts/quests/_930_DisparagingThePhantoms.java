package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;


//By Evil_dnk
//TODO CHECK REWARD

public class _930_DisparagingThePhantoms extends Quest
{
	// NPC's
	private static final int SFORCA = 34230;

	// Monster's
	private static final int[] MONSTERS = {23389};

	private static final long EXP_REWARD_LOW = 7752633564l;
	private static final int SP_REWARD_LOW = 18606274;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11628950346l;
	private static final int SP_REWARD_MEDIUM = 27909411;
	private static final int FP_REWARD_MEDIUM = 200;

	public static final String A_LIST = "A_LIST";

	public _930_DisparagingThePhantoms()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(SFORCA);
		addTalkId(SFORCA);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 1023389, A_LIST, 3, MONSTERS);
		addKillNpcWithLog(3, 1023389, A_LIST, 6, MONSTERS);
		addLevelCheck("blackbird_sporcha_q0930_02.htm", 99);
		addQuestCompletedCheck("blackbird_sporcha_q0930_02.htm", 10457);
		addFactionLevelCheck("blackbird_sporcha_q0930_02a.htm", FactionType.BLACKBIRD_PLEDGE, 4);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("blackbird_sporcha_q0930_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.BLACKBIRD_PLEDGE) >= 5)
				htmltext = "blackbird_sporcha_q0930_05a.htm";
			else
				htmltext = "blackbird_sporcha_q0930_05.htm";
		}
		else if(event.equalsIgnoreCase("blackbird_sporcha_q0930_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("blackbird_sporcha_q0930_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("blackbird_sporcha_q0930_13.htm"))
		{
			st.giveItems(47356, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.BLACKBIRD_PLEDGE, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("blackbird_sporcha_q0930_13a.htm"))
		{
			st.giveItems(47357, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.BLACKBIRD_PLEDGE, FP_REWARD_MEDIUM);
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
			case SFORCA:
				if (cond == 0)
					htmltext = "blackbird_sporcha_q0930_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.BLACKBIRD_PLEDGE) >= 5)
						htmltext = "blackbird_sporcha_q0930_05a.htm";
					else
						htmltext = "blackbird_sporcha_q0930_05.htm";
				}
				else if (cond == 2)
					htmltext = "blackbird_sporcha_q0930_11.htm";
				else if (cond == 3)
					htmltext = "blackbird_sporcha_q0930_11a.htm";
				else if (cond == 4)
					htmltext = "blackbird_sporcha_q0930_12.htm";
				else if (cond == 5)
					htmltext = "blackbird_sporcha_q0930_12a.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(4);
			}
		}
		else if(qs.getCond() == 3)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(5);
			}
		}
		return null;
	}
}