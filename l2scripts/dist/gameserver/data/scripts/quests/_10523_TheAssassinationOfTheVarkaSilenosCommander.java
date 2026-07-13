package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10523_TheAssassinationOfTheVarkaSilenosCommander extends Quest
{

	// NPC's
	private static final int HANSEN = 33853;

	private static final int MOS = 27502;

	private static final int EXP_REWARD = 327446943;
	private static final int SP_REWARD = 1839;

	public _10523_TheAssassinationOfTheVarkaSilenosCommander()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(MOS);
		addRaceCheck("hansen_q10523_02a.htm", Race.ERTHEIA);
		addLevelCheck("hansen_q10523_02a.htm", 76/*, 80*/);
		addQuestCompletedCheck("hansen_q10523_02a.htm", 10522);
		addClassTypeCheck("hansen_q10523_02.htm", ClassType.FIGHTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("hansen_q10523_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("hansen_q10523_08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
			case HANSEN:
				if (cond == 0)
					htmltext = "hansen_q10523_01.htm";
				else if (cond == 1)
					htmltext = "hansen_q10523_06.htm";
				else if (cond == 2)
					htmltext = "hansen_q10523_07.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.setCond(2);
		}
		return null;
	}
}