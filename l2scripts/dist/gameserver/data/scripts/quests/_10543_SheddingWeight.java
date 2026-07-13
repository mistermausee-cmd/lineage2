package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class _10543_SheddingWeight extends Quest
{
	private static final int SHENON = 32974;
	private static final int WILF = 30005;

	private static final int EXP_REWARD = 2630;
	private static final int SP_REWARD = 9;

	public _10543_SheddingWeight()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(SHENON);
		addTalkId(SHENON);
		addTalkId(WILF);

		addRaceCheck("si_illusion_shannon_q10543_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_shannon_q10543_02.htm", 1/*, 20*/);
		addQuestCompletedCheck("si_illusion_shannon_q10543_02.htm", 10542);
	}


	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("si_illusion_shannon_q10543_04.htm"))
		{
			st.setCond(1);
		}

		else if (event.equalsIgnoreCase("wilph_q10543_04.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			st.showTutorialClientHTML("QT_007_post_01");
			st.giveItems(7816, 1, false);
			st.giveItems(7817, 1, false);
			st.giveItems(7818, 1, false);
			st.giveItems(7819, 1, false);
			st.giveItems(7820, 1, false);
			st.giveItems(7821, 1, false);
			st.giveItems(22, 1, false);
			st.giveItems(29, 1, false);
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
			case SHENON:
				if (cond == 0)
					htmltext = "si_illusion_shannon_q10543_01.htm";
				else if (cond == 1)
					htmltext = "si_illusion_shannon_q10543_05.htm";
				break;

			case WILF:
				 if (cond == 1)
					htmltext = "wilph_q10543_02.htm";
				break;
		}
		return htmltext;
	}
}
