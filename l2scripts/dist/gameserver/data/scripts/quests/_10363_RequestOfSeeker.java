package quests;

import com.sun.org.apache.regexp.internal.RE;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk
public class _10363_RequestOfSeeker extends Quest
{
	private static final int NAGEL = 33450;
	private static final int SELIN = 33451;

	private static final int HUSK = 22991;
	private static final int STALKER = 22992;

	private static final int REPORT = 47606;

	private static final int EXP_REWARD = 70000;
	private static final int SP_REWARD = 13;

	public _10363_RequestOfSeeker()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(NAGEL);
		addTalkId(NAGEL, SELIN);
		addKillId(HUSK, STALKER);
		addRaceCheck("si_illusion_nazel_q10363_02a", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_nazel_q10363_02", 11/*, 20*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10362);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("si_illusion_nazel_q10363_05.htm"))
		{
			st.setCond(1);
			//st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			//536233	u,С помощью Устройства Телепорта Эсагира, помеченного красным цветом, переместитесь во 2-ю Зону Исследования.\0
		}
		else if (event.equalsIgnoreCase("si_illusion_chesha_q10362_03.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("si_illusion_selin_q10363_03.htm"))
		{
			st.takeItems(REPORT, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(43, 1, false);
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
			case NAGEL:
				if (cond == 0)
					htmltext = "si_illusion_nazel_q10363_01.htm";
				else if (cond == 1)
					htmltext = "si_illusion_nazel_q10363_06.htm";
				else if (cond == 2)
					htmltext = "si_illusion_nazel_q10363_08.htm";
				break;

			case SELIN:
				if (cond != 2)
					htmltext = "si_illusion_selin_q10363_01.htm";
				else if (cond == 2)
					htmltext = "si_illusion_selin_q10363_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.rollAndGive(REPORT, 1, 1, 15, 60);
			if (st.getQuestItemsCount(REPORT) >= 15)
				st.setCond(2);
		}
		return null;
	}
}