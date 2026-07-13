package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10820_RelationshipsBefittingOfTheStatus extends Quest
{
	// NPC's
	private static final int ISHUMA = 32615;

	//Items
	private static final int CERTIF2 = 45630;
	private static final int ACCES = 45640;
	private static final int REWARDCHEST = 39324;
	private static final int PARTOFACC = 45639;
	private static final int RECIPEOFACC = 45643;

	public _10820_RelationshipsBefittingOfTheStatus()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(ISHUMA);
		addTalkId(ISHUMA);
		addLevelCheck("maestro_ishuma_q10820_02.htm", 99);
		addNobleCheck("maestro_ishuma_q10820_02.htm", true);
		addItemHaveCheck("maestro_ishuma_q10820_03.htm", 45632, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("maestro_ishuma_q10820_07.htm"))
		{
			st.giveItems(RECIPEOFACC, 1, false);
			st.giveItems(PARTOFACC, 1, false);
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("maestro_ishuma_q10820_12.htm"))
		{
			st.takeItems(ACCES, -1);
			st.takeItems(PARTOFACC, -1);
			st.takeItems(RECIPEOFACC, -1);
			st.giveItems(CERTIF2, 1, false);
			st.giveItems(REWARDCHEST, 1);
			if (checkReward(st))
				htmltext = "maestro_ishuma_q10820_13.htm";
			st.finishQuest();
		}
		else if (event.equalsIgnoreCase("maestro_ishuma_q10820_05.htm"))
		{
			if(st.getPlayer().getClassId().ordinal() == 156)
				htmltext = "maestro_ishuma_q10820_06.htm";
		}
		else if (event.equalsIgnoreCase("maestro_ishuma_q10820_09.htm"))
		{
			st.giveItems(RECIPEOFACC, 1, false);
		}
		else if (event.equalsIgnoreCase("maestro_ishuma_q10820_10.htm"))
		{
			st.giveItems(PARTOFACC, 10, false);
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
			case ISHUMA:
				if (cond == 0)
					htmltext = "maestro_ishuma_q10820_01.htm";
				else if (cond == 1 && !st.haveQuestItem(ACCES))
					htmltext = "maestro_ishuma_q10820_08.htm";
				else if (cond == 1)
					htmltext = "maestro_ishuma_q10820_11.htm";
				break;
		}
		return htmltext;
	}


	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getLevel() > 99 && st.haveQuestItem(45628) && st.haveQuestItem(45629) && st.haveQuestItem(45630) && st.haveQuestItem(45631))
		{
			st.getPlayer().getQuestState(10817).setCond(2);
			return true;
		}
		return false;
	}

}


