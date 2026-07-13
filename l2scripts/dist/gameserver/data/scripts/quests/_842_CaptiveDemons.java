package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _842_CaptiveDemons extends Quest
{
	// NPC's
	private static final int STOR = 34219;

	// Monster's
	private static final int[] MONSTERS = {23735, 23736, 23737, 23738};

	private static final long EXP_REWARD_LOW = 5536944000l;
	private static final int SP_REWARD_LOW = 13288590;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11073888000l;
	private static final int SP_REWARD_MEDIUM = 28475640;
	private static final int FP_REWARD_MEDIUM = 200;

	public static final String A_LIST = "A_LIST";

	public _842_CaptiveDemons()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(STOR);
		addTalkId(STOR);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 84205, A_LIST, 200, MONSTERS);
		addKillNpcWithLog(3, 84205, A_LIST, 400, MONSTERS);
		addLevelCheck("stor_q0842_02.htm", 100);
		addFactionLevelCheck("stor_q0842_02a.htm", FactionType.GIANT_CHASER, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("stor_q0842_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) < 3)
				htmltext = "stor_q0842_05.htm";
			else
				htmltext = "stor_q0842_05a.htm";
		}
		else if(event.equalsIgnoreCase("stor_q0842_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("stor_q0842_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("stor_q0842_13.htm"))
		{
			st.giveItems(47359, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("stor_q0842_13a.htm"))
		{
			st.giveItems(47360, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.GIANT_CHASER, FP_REWARD_MEDIUM);
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
			case STOR:
				if (cond == 0)
					htmltext = "stor_q0842_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.GIANT_CHASER) < 3)
						htmltext = "stor_q0842_05.htm";
					else
						htmltext = "stor_q0842_05a.htm";
				}
				else if (cond == 2)
					htmltext = "stor_q0842_11.htm";
				else if (cond == 3)
					htmltext = "stor_q0842_11a.htm";
				else if (cond == 4)
					htmltext = "stor_q0842_12.htm";
				else if (cond == 5)
					htmltext = "stor_q0842_12a.htm";
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