package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _044_HelpTheSon extends Quest
{
	private static final int LUNDY = 30827;
	private static final int DRIKUS = 30505;

	private static final int WORK_HAMMER = 168;
	private static final int GEMSTONE_FRAGMENT = 7552;
	private static final int GEMSTONE = 7553;
	private static final int PET_TICKET = 7585;

	private static final int MAILLE_GUARD = 20921;
	private static final int MAILLE_SCOUT = 20920;
	private static final int MAILLE_LIZARDMAN = 20919;

	public _044_HelpTheSon()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(LUNDY);

		addTalkId(DRIKUS);

		addKillId(MAILLE_GUARD);
		addKillId(MAILLE_SCOUT);
		addKillId(MAILLE_LIZARDMAN);

		addQuestItem(GEMSTONE_FRAGMENT);
		
		addLevelCheck("pet_manager_lundy_q0044_0103.htm", 24);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			htmltext = "pet_manager_lundy_q0044_0104.htm";
			st.setCond(1);
		}
		else if(event.equals("3") && st.getQuestItemsCount(WORK_HAMMER) > 0)
		{
			htmltext = "pet_manager_lundy_q0044_0201.htm";
			st.takeItems(WORK_HAMMER, 1);
			st.setCond(2);
		}
		else if(event.equals("4") && st.getQuestItemsCount(GEMSTONE_FRAGMENT) >= 30)
		{
			htmltext = "pet_manager_lundy_q0044_0301.htm";
			st.takeItems(GEMSTONE_FRAGMENT, -1);
			st.giveItems(GEMSTONE, 1);
			st.setCond(4);
		}
		else if(event.equals("5") && st.getQuestItemsCount(GEMSTONE) > 0)
		{
			htmltext = "high_prefect_drikus_q0044_0401.htm";
			st.takeItems(GEMSTONE, 1);
			st.setCond(5);
		}
		else if(event.equals("7"))
		{
			htmltext = "pet_manager_lundy_q0044_0501.htm";
			st.giveItems(PET_TICKET, 1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(npcId == LUNDY)
		{
			if(cond == 0)
				htmltext = "pet_manager_lundy_q0044_0101.htm";
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(WORK_HAMMER) == 0)
					htmltext = "pet_manager_lundy_q0044_0106.htm";
				else
					htmltext = "pet_manager_lundy_q0044_0105.htm";
			}
			else if(cond == 2)
				htmltext = "pet_manager_lundy_q0044_0204.htm";
			else if(cond == 3)
				htmltext = "pet_manager_lundy_q0044_0203.htm";
			else if(cond == 4)
				htmltext = "pet_manager_lundy_q0044_0303.htm";
			else if(cond == 5)
				htmltext = "pet_manager_lundy_q0044_0401.htm";
		}
		else if(npcId == DRIKUS)
		{
			if(cond == 4 && st.getQuestItemsCount(GEMSTONE) > 0)
				htmltext = "high_prefect_drikus_q0044_0301.htm";
			else if(cond == 5)
				htmltext = "high_prefect_drikus_q0044_0403.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 2 && st.getQuestItemsCount(GEMSTONE_FRAGMENT) < 30)
		{
			st.giveItems(GEMSTONE_FRAGMENT, 1);
			if(st.getQuestItemsCount(GEMSTONE_FRAGMENT) >= 30)
				st.setCond(3);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}