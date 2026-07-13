package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10826_LuckBefittingOfTheStatus extends Quest
{
	// NPC's
	private static final int BLACKSMIT = 31126;

	// Item's
	private static final int KNIFE = 45645;
	private static final int CERTIF3 = 45635;
	private static final int BOOK = 46036;

	public _10826_LuckBefittingOfTheStatus()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BLACKSMIT);
		addTalkId(BLACKSMIT);
		addLevelCheck("blacksmith_of_mammon_q10826_02.htm", 99);
		addNobleCheck("blacksmith_of_mammon_q10826_02.htm", true);
		addItemHaveCheck("blacksmith_of_mammon_q10826_03.htm", 45637, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("blacksmith_of_mammon_q10826_06.htm"))
		{
			if(!st.haveQuestItem(KNIFE))
				st.giveItems(KNIFE, 1, false);
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("blacksmith_of_mammon_q10826_07.htm"))
		{
			if(!st.haveQuestItem(KNIFE))
				st.giveItems(KNIFE, 1, false);
		}
		else if (event.equalsIgnoreCase("shaper"))
		{
			st.giveItems(17416 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("cutter"))
		{
			st.giveItems(17417 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("slasher"))
		{
			st.giveItems(17418 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("avenger"))
		{
			st.giveItems(17419 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("fighter"))
		{
			st.giveItems(17420 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("stormer"))
		{
			st.giveItems(17421 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("thrower"))
		{
			st.giveItems(17422 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("shooter"))
		{
			st.giveItems(17423 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("buster"))
		{
			st.giveItems(17424 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("caster"))
		{
			st.giveItems(17425 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("retributer"))
		{
			st.giveItems(17426 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("dualsword"))
		{
			st.giveItems(17427 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("dagger"))
		{
			st.giveItems(17428 ,1, false);
			return finishquest(st);
		}
		else if (event.equalsIgnoreCase("blunt"))
		{
			st.giveItems(17429 ,1, false);
			return finishquest(st);
		}
		return htmltext;
	}

	private String finishquest(QuestState st)
	{
		String htmltext = null;
		st.takeItems(KNIFE, -1);
		st.giveItems(CERTIF3, 1, false);
		st.giveItems(BOOK, 1, false);
		if (checkReward(st))
			htmltext = "captain_kurtis_q10825_15.htm";
		else
			htmltext = "captain_kurtis_q10825_14.htm";
		st.finishQuest();
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
			case BLACKSMIT:
				if (cond == 0)
					htmltext = "blacksmith_of_mammon_q10826_01.htm";
				else if (cond == 1 && !st.haveQuestItem(KNIFE))
				{
					htmltext = "blacksmith_of_mammon_q10826_06.htm";
					st.giveItems(KNIFE, 1);
				}
				else if (cond == 1 && st.haveQuestItem(KNIFE) && st.getPlayer().getInventory().getItemByItemId(KNIFE).getEnchantLevel() == 7)
					htmltext = "blacksmith_of_mammon_q10826_12.htm";
				else if (cond == 1 && st.haveQuestItem(KNIFE) && st.getPlayer().getInventory().getItemByItemId(KNIFE).getEnchantLevel() > 7)
					htmltext = "blacksmith_of_mammon_q10826_13.htm";
				else if (cond == 1 && st.haveQuestItem(KNIFE) && st.getPlayer().getInventory().getItemByItemId(KNIFE).getEnchantLevel() < 3)
					htmltext = "blacksmith_of_mammon_q10826_09.htm";
				else if (cond == 1 && st.haveQuestItem(KNIFE) && st.getPlayer().getInventory().getItemByItemId(KNIFE).getEnchantLevel() < 4)
					htmltext = "blacksmith_of_mammon_q10826_10.htm";
				else if (cond == 1 && st.haveQuestItem(KNIFE) && st.getPlayer().getInventory().getItemByItemId(KNIFE).getEnchantLevel() < 7)
					htmltext = "blacksmith_of_mammon_q10826_11.htm";
				break;
		}
		return htmltext;
	}


	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getDualClassLevel() > 99 && st.haveQuestItem(46056) && st.haveQuestItem(46057) && st.haveQuestItem(45635) && st.haveQuestItem(45636))
		{
			st.getPlayer().getQuestState(10823).setCond(2);
			return true;
		}

		return false;
	}
}