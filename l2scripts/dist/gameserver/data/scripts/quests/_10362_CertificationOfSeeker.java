package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class _10362_CertificationOfSeeker extends Quest
{
	private static final int RAKSIS = 32977;
	private static final int SHESHA = 33449;
	private static final int NAZEL = 33450;
	private static final int HUSK = 22991;
	private static final int STALKER = 22992;

	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";


	private static final int EXP_REWARD = 40000;
	private static final int SP_REWARD = 12;

	public _10362_CertificationOfSeeker()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(RAKSIS);
		addTalkId(RAKSIS, SHESHA, NAZEL);
		addKillNpcWithLog(2, 1022992, A_LIST, 10, STALKER);
		addKillNpcWithLog(2, 1022991, B_LIST, 10, HUSK);

		addRaceCheck("si_illusion_larcis_q10362_02a", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_larcis_q10362_02", 9/*, 20*/);
		addQuestCompletedCheck("si_illusion_larcis_q10362_02", 10330);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("si_illusion_larcis_q10362_05.htm"))
		{
			st.setCond(1);
			//st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			//536132	u,С помощью Устройства Телепорта Эсагира, помеченного красным цветом, переместитесь в 1-ю Зону Исследования.\0
		}
		else if (event.equalsIgnoreCase("si_illusion_chesha_q10362_03.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("si_illusion_nazel_q10362_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(49, 1, false);
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
			case RAKSIS:
				if (cond == 0)
					htmltext = "si_illusion_larcis_q10362_01.htm";
				else if (cond == 1)
					htmltext = "si_illusion_larcis_q10362_06.htm";
				break;

			case SHESHA:
				if (cond == 1)
					htmltext = "si_illusion_chesha_q10362_01.htm";
				else if (cond == 2)
					htmltext = "si_illusion_chesha_q10362_04.htm";
				else if (cond == 3)
					htmltext = "si_illusion_chesha_q10362_08.htm";
				break;
			case NAZEL:
				if (cond == 3)
					htmltext = "si_illusion_nazel_q10362_02.htm";
				else if (cond != 3)
					htmltext = "si_illusion_nazel_q10362_01.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.setCond(3);
		}
		return null;
	}	
}