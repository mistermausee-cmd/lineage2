package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public class _453_NotStrongEnough extends Quest
{
	private static final int Klemis = 32734;

	public static final String A_MOBS = "a_mobs";
	public static final String B_MOBS = "b_mobs";
	public static final String C_MOBS = "c_mobs";
	public static final String E_MOBS = "e_mobs";

	private static final int[] Rewards = {
			34861,
			17526,
			17527,
			9546,
			9547,
			9548,
			9549,
			9550,
			9551,
			9552,
			9553,
			9554,
			9555,
			9556,
			9557
	};

	public _453_NotStrongEnough()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(Klemis);

		// bistakon 4	"1022746|1022747|1022748|1022749"	4	"15|15|15|15"
		addKillNpcWithLog(2, A_MOBS, 15, 22746, 22750);
		addKillNpcWithLog(2, B_MOBS, 15, 22747, 22751);
		addKillNpcWithLog(2, C_MOBS, 15, 22748, 22752);
		addKillNpcWithLog(2, E_MOBS, 15, 22749, 22753);
		// reptilikon 3	"1022754|1022755|1022756"	3	"20|20|20"
		addKillNpcWithLog(3, A_MOBS, 20, 22754, 22757);
		addKillNpcWithLog(3, B_MOBS, 20, 22755, 22758);
		addKillNpcWithLog(3, C_MOBS, 20, 22756, 22759);
		// cokrakon  3	"1022760|1022761|1022762"	3	"20|20|20"
		addKillNpcWithLog(4, A_MOBS, 20, 22760, 22763);
		addKillNpcWithLog(4, B_MOBS, 20, 22761, 22764);
		addKillNpcWithLog(4, C_MOBS, 20, 22762, 22765);
		addLevelCheck("klemis_q453_00.htm", 85);
		addQuestCompletedCheck("klemis_q453_00.htm", 10282);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klemis_q453_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("bistakon"))
		{
			htmltext = "klemis_q453_05.htm";
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("reptilicon"))
		{
			htmltext = "klemis_q453_06.htm";
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("cokrakon"))
		{
			htmltext = "klemis_q453_07.htm";
			st.setCond(4);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Klemis)
		{
			if(cond == 0)
				htmltext = "klemis_q453_01.htm";
			else if(cond == 1)
			{
				htmltext = "klemis_q453_03.htm";
			}
			else if(cond == 2)
			{
				htmltext = "klemis_q453_09.htm";
			}
			else if(cond == 3)
			{
				htmltext = "klemis_q453_10.htm";
			}
			else if(cond == 4)
			{
				htmltext = "klemis_q453_11.htm";
			}
			else if(cond == 5)
			{
				htmltext = "klemis_q453_12.htm";
				int rewardId = Rewards[Rnd.get(Rewards.length)];
				st.giveItems(rewardId, rewardId == 34861 ? Rnd.get(1,4) : 1);
				st.finishQuest();
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Klemis)
			htmltext = "klemis_q453_00a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_MOBS);
			st.unset(B_MOBS);
			st.unset(C_MOBS);
			st.unset(E_MOBS);
			st.setCond(5);
		}
		return null;
	}
}