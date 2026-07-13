package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD

public class _845_SabotageTheEmbryoSupplies extends Quest
{
	// NPC's
	private static final int RICHARD = 34235;
	private static final int SUPPORT = 34258;

	private static final int EMBRIO = 47197;

	private static final long EXP_REWARD_LOW = 7262301690l;
	private static final int SP_REWARD_LOW = 17429400;
	private static final int FP_REWARD_LOW = 100;

	private static final long EXP_REWARD_MEDIUM = 14524603380l;
	private static final int SP_REWARD_MEDIUM = 34858800;
	private static final int FP_REWARD_MEDIUM = 200;

	private static final long EXP_REWARD_HIGH = 21786905070l;
	private static final int SP_REWARD_HIGH = 52288200;
	private static final int FP_REWARD_HIGH = 300;


	public _845_SabotageTheEmbryoSupplies()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(RICHARD);
		addTalkId(RICHARD, SUPPORT);
		addQuestItem(EMBRIO);
		addLevelCheck("royal_maestre_q0845_02.htm", 101);
		addFactionLevelCheck("royal_maestre_q0845_02a.htm", FactionType.KINGDOM_ROYALGUARD, 2);
		addQuestCompletedCheck("royal_maestre_q0845_02.htm", 10844);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("royal_maestre_q0845_05.htm"))
		{
			st.setCond(1);
			if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 5 && st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) <= 7)
				htmltext = "royal_maestre_q0845_05a.htm";
			else if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 8)
				htmltext = "royal_maestre_q0845_05b.htm";
			else
				htmltext = "royal_maestre_q0845_05.htm";
		}
		else if(event.equalsIgnoreCase("royal_maestre_q0845_10.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("royal_maestre_q0845_10a.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("royal_maestre_q0845_10b.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("royal_maestre_q0845_13.htm"))
		{
			st.giveItems(47175, 1);
			st.addExpAndSp(EXP_REWARD_LOW, SP_REWARD_LOW);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_LOW);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("royal_maestre_q0845_13a.htm"))
		{
			st.giveItems(47176, 1);
			st.addExpAndSp(EXP_REWARD_MEDIUM, SP_REWARD_MEDIUM);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_MEDIUM);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("royal_maestre_q0845_13b.htm"))
		{
			st.giveItems(47177, 1);
			st.addExpAndSp(EXP_REWARD_HIGH, SP_REWARD_HIGH);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD_HIGH);
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
			case RICHARD:
				if (cond == 0)
					htmltext = "royal_maestre_q0845_01.htm";
				else if (cond == 1)
				{
					if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 5 && st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) <= 7)
						htmltext = "royal_maestre_q0845_05a.htm";
					else if(st.getPlayer().getFactionList().getLevel(FactionType.KINGDOM_ROYALGUARD) >= 8)
						htmltext = "royal_maestre_q0845_05b.htm";
					else
						htmltext = "royal_maestre_q0845_05.htm";
				}
				else if (cond == 2)
					htmltext = "royal_maestre_q0845_11.htm";
				else if (cond == 3)
					htmltext = "royal_maestre_q0845_11a.htm";
				else if (cond == 4)
					htmltext = "royal_maestre_q0845_11b.htm";
				else if (cond == 5)
					htmltext = "royal_maestre_q0845_12.htm";
				else if (cond == 6)
					htmltext = "royal_maestre_q0845_12a.htm";
				else if (cond == 7)
					htmltext = "royal_maestre_q0845_12b.htm";
				break;

			case SUPPORT:
				if (cond == 2)
				{
					st.giveItems(EMBRIO, 1);
					if(st.getQuestItemsCount(EMBRIO) >= 40)
						st.setCond(5);
					npc.deleteMe();
					return null;
				}
				else if (cond == 3)
				{
					st.giveItems(EMBRIO, 1);
					if(st.getQuestItemsCount(EMBRIO) >= 80)
						st.setCond(6);
					npc.deleteMe();
					return null;
				}
				if (cond == 4)
				{
					st.giveItems(EMBRIO, 1);
					if(st.getQuestItemsCount(EMBRIO) >= 120)
						st.setCond(7);
					npc.deleteMe();
					return null;
				}
				break;
		}
		return htmltext;
	}
}