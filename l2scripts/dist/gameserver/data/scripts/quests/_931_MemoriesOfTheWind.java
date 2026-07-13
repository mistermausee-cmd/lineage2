package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _931_MemoriesOfTheWind extends Quest
{
	// NPC's
	private static final int GIFONA = 34055;

	private static final int WINDMEM = 47188;

	// Monster's
	private static final int[] MOBS = {23797, 23559, 23560};

	private static final long EXP_REWARD_LOW = 5932440000l;
	private static final int SP_REWARD_LOW = 14237820;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 11864880000l;
	private static final int SP_REWARD_MEDIUM = 28475640;
	private static final int FP_REWARD_MEDIUM = 200;

	public _931_MemoriesOfTheWind()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(GIFONA);
		addTalkId(GIFONA);
		addKillId(MOBS);
		addQuestItem(WINDMEM);
		addLevelCheck("cyphona_q0931_02.htm", 100);
		addQuestCompletedCheck("cyphona_q0931_02.htm", 10831);
		addFactionLevelCheck("cyphona_q0931_02a.htm", FactionType.DIMENSIONAL_STRANGER, 2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("cyphona_q0931_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.DIMENSIONAL_STRANGER) >= 3)
				htmltext = "cyphona_q0931_05a.htm";
			else
				htmltext = "cyphona_q0931_05.htm";
		}
		else if(event.equalsIgnoreCase("cyphona_q0931_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("cyphona_q0931_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("cyphona_q0931_13.htm"))
		{
			st.giveItems(47181, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.DIMENSIONAL_STRANGER, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("cyphona_q0931_13a.htm"))
		{
			st.giveItems(47182, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.DIMENSIONAL_STRANGER, FP_REWARD_MEDIUM);
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
			case GIFONA:
				if (cond == 0)
					htmltext = "cyphona_q0931_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.DIMENSIONAL_STRANGER) >= 3)
						htmltext = "cyphona_q0931_05a.htm";
					else
						htmltext = "cyphona_q0931_05.htm";
				}
				else if (cond == 2)
					htmltext = "cyphona_q0931_11.htm";
				else if (cond == 3)
					htmltext = "cyphona_q0931_11a.htm";
				else if (cond == 4)
					htmltext = "cyphona_q0931_12.htm";
				else if (cond == 5)
					htmltext = "cyphona_q0931_12a.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if (qs.rollAndGive(WINDMEM, 1, 1, 15, 100)) //TODO CHANCE?
				qs.setCond(4);
		}
		else if(qs.getCond() == 3)
		{
			if (qs.rollAndGive(WINDMEM, 1, 1, 30, 100)) //TODO CHANCE?
					qs.setCond(5);
		}
		return null;
	}
}