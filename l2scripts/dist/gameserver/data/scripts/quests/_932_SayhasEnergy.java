package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _932_SayhasEnergy extends Quest
{
	// NPC's
	private static final int BELAS = 34056;

	// Monster's
	private static final int[] MONSTERS = {23545};

	private static final long EXP_REWARD_LOW = 5932440000l;
	private static final int SP_REWARD_LOW = 14237820;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11864880000l;
	private static final int SP_REWARD_MEDIUM = 28475640;
	private static final int FP_REWARD_MEDIUM = 200;

	public static final String A_LIST = "A_LIST";

	public _932_SayhasEnergy()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(BELAS);
		addTalkId(BELAS);
		addKillId(MONSTERS);
		addKillNpcWithLog(2, 1023545, A_LIST, 200, MONSTERS);
		addKillNpcWithLog(3, 1023545, A_LIST, 400, MONSTERS);
		addLevelCheck("belas_q0932_02.htm", 100);
		addQuestCompletedCheck("belas_q0932_02.htm", 10831);
		addFactionLevelCheck("belas_q0932_02a.htm", FactionType.DIMENSIONAL_STRANGER, 4);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("belas_q0932_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.DIMENSIONAL_STRANGER) >= 5)
				htmltext = "belas_q0932_05a.htm";
			else
				htmltext = "belas_q0932_05.htm";
		}
		else if(event.equalsIgnoreCase("belas_q0932_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("belas_q0932_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("belas_q0932_13.htm"))
		{
			st.giveItems(47181, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.DIMENSIONAL_STRANGER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("belas_q0932_13a.htm"))
		{
			st.giveItems(47182, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.DIMENSIONAL_STRANGER, FP_REWARD_MEDIUM);
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
			case BELAS:
				if (cond == 0)
					htmltext = "belas_q0932_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.DIMENSIONAL_STRANGER) >= 5)
						htmltext = "belas_q0932_05a.htm";
					else
						htmltext = "belas_q0932_05.htm";
				}
				else if (cond == 2)
					htmltext = "belas_q0932_11.htm";
				else if (cond == 3)
					htmltext = "belas_q0932_11a.htm";
				else if (cond == 4)
					htmltext = "belas_q0932_12.htm";
				else if (cond == 5)
					htmltext = "belas_q0932_12a.htm";
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