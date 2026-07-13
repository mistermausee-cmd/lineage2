package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10746_SeeTheWorld extends Quest
{
	// NPC's
	private static final int KALLI = 33933;
	private static final int LEVIAN = 30037;
	private static final int ASTIEL = 33948;

	private static final int EXP_REWARD = 161998;	private static final int SP_REWARD = 5; 	public _10746_SeeTheWorld()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(KALLI);
		addTalkId(LEVIAN, ASTIEL);
		addLevelCheck("33933-0.htm", 19/*, 25*/);
		addRaceCheck("33933-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33933-2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33948-2.htm"))
		{
			st.getPlayer().teleToLocation(-80712, 149992, -3069);
			st.setCond(2);
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
			case KALLI:
				if(cond == 0)
					htmltext = "33933-1.htm";
			break;

			case ASTIEL:
				if (cond == 1)
					htmltext = "33948-1.htm";
			break;

			case LEVIAN:
				if (cond == 2)
				{
					st.giveItems(3948, 1500);
					st.giveItems(1463, 1500);
					st.giveItems(736, 10);
					st.giveItems(57, 147600);
					st.giveItems(46849, 1, false);
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
					st.finishQuest();
					htmltext = "30037-1.htm";
				}
			break;
		}
		return htmltext;
	}
}