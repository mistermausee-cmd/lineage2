package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.entity.olympiad.OlympiadMember;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _553_OlympiadUndefeated extends Quest
{
	// NPCs
	private static final int OLYMPIAD_MANAGER = 31688;

	// Items
	private static final int OLYMPIAD_CHEST = 46037;
	private static final int POWER_OF_GIGANT = 35563;
	private static final int WINS_CONFIRMATION1 = 17244;
	private static final int WINS_CONFIRMATION2 = 17245;
	private static final int WINS_CONFIRMATION3 = 17246;

	public _553_OlympiadUndefeated()
	{
		super(PARTY_NONE, DAILY);

		addStartNpc(OLYMPIAD_MANAGER);
		addTalkId(OLYMPIAD_MANAGER);
		addQuestItem(WINS_CONFIRMATION1, WINS_CONFIRMATION2, WINS_CONFIRMATION3);
		addLevelCheck("olympiad_operator_q0553_08.htm", 85);
		addClassLevelCheck("olympiad_operator_q0553_08.htm", false, ClassLevel.AWAKED);
		addClassLevelCheck("olympiad_operator_q0553_08.htm", true, ClassLevel.THIRD); // Ertheia
		addNobleCheck("olympiad_operator_q0553_08.htm", true);
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case OLYMPIAD_MANAGER:
				if(cond == 0)
					return "olympiad_operator_q0553_01.htm";
				else if(cond == 1)
				{
					if (st.getQuestItemsCount(WINS_CONFIRMATION1, WINS_CONFIRMATION2, WINS_CONFIRMATION3) == 0)
						return "olympiad_operator_q0553_04.htm";
					else
						return "olympiad_operator_q0553_05.htm";
				}
				if(cond == 2)
				{
					if(st.getQuestItemsCount(WINS_CONFIRMATION3) > 0)
					{
						st.giveItems(OLYMPIAD_CHEST, 6);
						st.giveItems(POWER_OF_GIGANT, 3);
						st.giveItems(-300, 20000);
						st.takeItems(WINS_CONFIRMATION1, -1);
						st.takeItems(WINS_CONFIRMATION2, -1);
						st.takeItems(WINS_CONFIRMATION3, -1);
						st.finishQuest();
						return "olympiad_operator_q0553_07.htm";
					}
					else
						return "olympiad_operator_q0553_05.htm";
				}
				break;
		}

		return null;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == OLYMPIAD_MANAGER)
			htmltext = "olympiad_operator_q0553_06.htm";
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("olympiad_operator_q0553_03.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("olympiad_operator_q0553_07.htm"))
		{
			if(st.getQuestItemsCount(WINS_CONFIRMATION3) > 0)
			{
				st.giveItems(OLYMPIAD_CHEST, 6);
				st.giveItems(POWER_OF_GIGANT, 3);
				st.giveItems(-300, 20000);
				st.takeItems(WINS_CONFIRMATION1, -1);
				st.takeItems(WINS_CONFIRMATION2, -1);
				st.takeItems(WINS_CONFIRMATION3, -1);
				st.finishQuest();
			}
			else if(st.getQuestItemsCount(WINS_CONFIRMATION2) > 0)
			{
				st.giveItems(OLYMPIAD_CHEST, 3);
				st.giveItems(POWER_OF_GIGANT, 1);
				st.giveItems(-300, 10000);
				st.takeItems(WINS_CONFIRMATION1, -1);
				st.takeItems(WINS_CONFIRMATION2, -1);
				st.takeItems(WINS_CONFIRMATION3, -1);
				st.finishQuest();
			}
			else if(st.getQuestItemsCount(WINS_CONFIRMATION1) > 0)
			{
				st.giveItems(OLYMPIAD_CHEST, 1);
				st.takeItems(WINS_CONFIRMATION1, -1);
				st.takeItems(WINS_CONFIRMATION2, -1);
				st.takeItems(WINS_CONFIRMATION3, -1);
				st.finishQuest();
			}
		}
		return event;
	}

	@Override
	public void onOlympiadEnd(OlympiadGame og, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			int count = qs.getInt("count"); //TODO
			OlympiadMember winner = og.getWinnerMember();
			if(winner != null && winner.getObjectId() == qs.getPlayer().getObjectId())
				count++;
			else
				count = 0;

			qs.set("count", count);
			if(count == 2 && qs.getQuestItemsCount(WINS_CONFIRMATION1) == 0)
			{
				qs.giveItems(WINS_CONFIRMATION1, 1);
				qs.playSound(SOUND_ITEMGET);
			}
			else if(count == 5 && qs.getQuestItemsCount(WINS_CONFIRMATION2) == 0)
			{
				qs.giveItems(WINS_CONFIRMATION2, 1);
				qs.playSound(SOUND_ITEMGET);
			}
			else if(count == 10 && qs.getQuestItemsCount(WINS_CONFIRMATION3) == 0)
			{
				qs.giveItems(WINS_CONFIRMATION3, 2);
				qs.setCond(2);
			}
			if(count < 10 && qs.getQuestItemsCount(WINS_CONFIRMATION3) > 0)
				qs.takeItems(WINS_CONFIRMATION3, -1);
			if(count < 5 && qs.getQuestItemsCount(WINS_CONFIRMATION2) > 0)
				qs.takeItems(WINS_CONFIRMATION2, -1);
			if(count < 2 && qs.getQuestItemsCount(WINS_CONFIRMATION1) > 0)
				qs.takeItems(WINS_CONFIRMATION1, -1);
		}
	}
}
