package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class _745_TheOutlawsAreIncoming extends Quest
{
	private static final int FLATER = 30677;
	private static final int KELIOS = 33862;
	private static final int MOEN = 30196;

	private static final int PIECE_GARG = 47047;
	private static final int PIECE_BAZIL = 47048;
	private static final int PIECE_SFINX = 47049;

	private static final int[] GARGULY = {20241};
	private static final int[] BAZILISK = {20573, 20574};
	private static final int[] SFINX = {20161, 20575, 20576, 21261, 21262, 21263, 21264};

	private static final long EXP_REWARD = 20144364;
	private static final int SP_REWARD = 1895;

	public _745_TheOutlawsAreIncoming()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(FLATER);
		addTalkId(FLATER, KELIOS, MOEN);
		addKillId(GARGULY);
		addKillId(BAZILISK);
		addKillId(SFINX);
		addQuestItem(PIECE_GARG, PIECE_BAZIL, PIECE_SFINX);
		addLevelCheck("head_blacksmith_flutter_q0745_02.htm", 46, 51);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("head_blacksmith_flutter_q0745_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("kelios_q0745_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("mouen_q0745_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		switch (npcId)
		{
			case FLATER:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "head_blacksmith_flutter_q0745_03.htm";
					else
						htmltext = "head_blacksmith_flutter_q0745_01.htm";
				}
				else if (cond == 1)
					htmltext = "head_blacksmith_flutter_q0745_07.htm";
				break;

			case KELIOS:
				if (cond == 1)
					htmltext = "kelios_q0745_01.htm";
				else if (cond == 2)
					htmltext = "kelios_q0745_04.htm";
				else if (cond == 3)
					htmltext = "kelios_q0745_05.htm";
				break;

			case MOEN:
				if (cond == 2)
					htmltext = "mouen_q0745_01.htm";
				else if (cond == 3)
					htmltext = "mouen_q0745_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(ArrayUtils.contains(GARGULY, npc.getNpcId()))
			{
				qs.rollAndGive(PIECE_GARG, 1, 1, 20, 100);
			}
			if(ArrayUtils.contains(BAZILISK, npc.getNpcId()))
			{
				qs.rollAndGive(PIECE_BAZIL, 1, 1, 50, 100);
			}
			if(ArrayUtils.contains(SFINX, npc.getNpcId()))
			{
				qs.rollAndGive(PIECE_SFINX, 1, 1, 80, 100);
			}
			if(qs.getQuestItemsCount(PIECE_GARG) >= 20 && qs.getQuestItemsCount(PIECE_BAZIL) >= 50 && qs.getQuestItemsCount(PIECE_SFINX) >= 80)
				qs.setCond(3);
		}
		return null;
	}
}

