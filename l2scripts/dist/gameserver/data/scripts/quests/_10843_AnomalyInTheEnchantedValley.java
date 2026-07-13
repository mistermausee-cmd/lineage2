package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10843_AnomalyInTheEnchantedValley extends Quest
{
	// NPC's
	private static final int NPC1 = 30610;
	private static final int NPC2 = 30747;

	// Item's
	private static final int REWARD1 = 46257;

	public _10843_AnomalyInTheEnchantedValley()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(NPC1);
		addTalkId(NPC1);
		addTalkId(NPC2);
		addLevelCheck("sage_cronos_q10843_02.htm", 101);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("sage_cronos_q10843_06.htm"))
		{
			if(st.getPlayer().getVar("10843") == null)
			{
				st.giveItems(REWARD1, 1, false);
				st.getPlayer().setVar("10843", 1);
			}
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("fairy_mymyu_q10843_03.htm"))
		{
			st.giveItems(REWARD1, 3);
			st.finishQuest();
			if(st.getPlayer().getVar("10843") != null)
				st.getPlayer().unsetVar("10843");
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
			case NPC1:
				if (cond == 0)
					htmltext = "sage_cronos_q10843_01.htm";
				else if (cond == 1)
					htmltext = "sage_cronos_q10843_07.htm";
				break;

			case NPC2:
				if (cond == 1)
					htmltext = "fairy_mymyu_q10843_01.htm";
				break;
		}
		return htmltext;
	}

}
