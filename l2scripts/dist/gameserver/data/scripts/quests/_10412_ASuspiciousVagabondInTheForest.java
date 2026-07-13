package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10412_ASuspiciousVagabondInTheForest extends Quest
{
    private static final int Hatuba_Tracker = 33849;
    private static final int Suspicious_Vagabond_Mortally_Endangered = 33850;

	private static final int EXP_REWARD = 7541520;
	private static final int SP_REWARD = 226; 

	public _10412_ASuspiciousVagabondInTheForest()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Hatuba_Tracker);
		addTalkId(Hatuba_Tracker, Suspicious_Vagabond_Mortally_Endangered);
		addLevelCheck("chaser_ahtuba_q10412_02.htm", 65, 70);
		addRaceCheck("chaser_ahtuba_q10412_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addClassTypeCheck("chaser_ahtuba_q10412_02.htm", ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("chaser_ahtuba_q10412_05.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("chaser_ahtuba_q10412_08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case Hatuba_Tracker:
				if (cond == 0)
					htmltext = "chaser_ahtuba_q10412_01.htm";
				else if (cond == 1)
					htmltext = "chaser_ahtuba_q10412_06.htm";
				else if (cond == 2)
					htmltext = "chaser_ahtuba_q10412_07.htm";
				break;
			case Suspicious_Vagabond_Mortally_Endangered:
				if (cond == 1)
				{
					st.setCond(2);
					htmltext = "embryo_rover_b_q10412_01.htm";
				}
				break;
		}
		return htmltext;
	}
}