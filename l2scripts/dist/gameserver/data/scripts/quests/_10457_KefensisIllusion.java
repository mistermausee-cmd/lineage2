package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10457_KefensisIllusion extends Quest
{
	// NPC's
	private static final int DEVIAN = 31590;
	//Mobs
	private static final int[] MONSTERS = {23384, 23385, 23386, 23387, 23388, 23395, 23396, 23397, 23398, 23399, 23389};

	private static final long REWARD_EXP = 3876316782L;
	private static final int REWARD_SP = 9303137;


	public _10457_KefensisIllusion()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(DEVIAN);
		addTalkId(DEVIAN);
		addKillId(MONSTERS);
		addQuestCompletedCheck("truthseeker_devianne_q10457_02.htm", 10455);
		addLevelCheck("truthseeker_devianne_q10457_02.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("truthseeker_devianne_q10457_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("truthseeker_devianne_q10457_08"))
		{
			st.addExpAndSp(REWARD_EXP,  REWARD_SP);
			st.giveItems(57, 2373300);
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
			case DEVIAN:
				if(cond == 0)
					htmltext = "truthseeker_devianne_q10457_01.htm";
				else if (cond == 1 || cond == 3 || cond == 3)
					htmltext = "truthseeker_devianne_q10457_06.htm";
				else if (cond == 4)
					htmltext = "truthseeker_devianne_q10457_07.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			//if(Rnd.chance(5))
			if(st.getPlayer().getAbnormalList().contains(16129))
			{
				st.setCond(2);
			}
		}
		if(cond == 2)
		{
			if(Rnd.chance(5) && st.getPlayer().getAbnormalList().contains(16129))
			{
				st.setCond(3);
			}
		}
		if(cond == 3)
		{
			if(npc.getNpcId() == 23389)
				st.setCond(4);
		}

		return null;
	}
}