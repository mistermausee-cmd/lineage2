package quests;

import l2s.gameserver.model.base.ClassType;
import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _762_AnOminousRequest extends Quest
{
	private static final int MATIAS = 31340;
	private static final int HATUVA = 33849;
	private static final int ANDREY = 31292;
    private static final int[] MOBS = {18119,21579,21576,21548,21549,21555,21547};
    private static final int M_BONE = 36670;
    private static final int M_BLOOD = 36671;

	private static final int EXP_REWARD = 157490428;
	private static final int SP_REWARD = 4524; 

	public _762_AnOminousRequest()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(MATIAS);
		addTalkId(MATIAS, HATUVA, ANDREY);
		addQuestItem(M_BONE, M_BLOOD);

		addKillId(MOBS);
		addLevelCheck("captain_mathias_q0762_02.htm", 65, 70);
		addClassTypeCheck("captain_mathias_q0762_02.htm", ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("captain_mathias_q0762_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("chaser_ahtuba_q0762_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("captain_andrei_q0762_03.htm"))
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
			case MATIAS:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "captain_mathias_q0762_03.htm";
					else
						htmltext = "captain_mathias_q0762_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_mathias_q0762_07.htm";
				break;
			case HATUVA:
				if (cond == 1)
					htmltext = "chaser_ahtuba_q0762_01.htm";
				else if (cond == 2)
					htmltext = "chaser_ahtuba_q0762_04.htm";
				else if (cond == 3)
					htmltext = "chaser_ahtuba_q0762_05.htm";
				break;
			case ANDREY:
				if (cond == 2)
					htmltext = "captain_andrei_q0762_01.htm";
				else if (cond == 3)
					htmltext = "captain_andrei_q0762_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			qs.rollAndGive(M_BONE, 1, 1, 200, 40);
			qs.rollAndGive(M_BLOOD, 1, 1, 200, 40);
			if(qs.getQuestItemsCount(M_BONE) >= 200 && qs.getQuestItemsCount(M_BLOOD) >= 200)
				qs.setCond(3);
		}
		return null;
	}
}