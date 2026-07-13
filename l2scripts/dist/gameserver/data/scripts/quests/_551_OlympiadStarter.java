package quests;

import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _551_OlympiadStarter extends Quest
{
	// NPCs
	private static final int OLYMPIAD_MANAGER = 31688;

	// Items
	private static final int OLYMPIAD_CHEST = 46037;
	private static final int OLYMPIAD_CHEST2 = 46038;
	private static final int POWER_OF_GIGANT = 35563;
	private static final int OLYMPIAD_CERT2 = 17239;
	private static final int OLYMPIAD_CERT3 = 17240;
	private static final int OLYMPIAD_CERT4 = 37757;

	public _551_OlympiadStarter()
	{
		super(PARTY_NONE, DAILY);

		addStartNpc(OLYMPIAD_MANAGER);
		addTalkId(OLYMPIAD_MANAGER);
		addQuestItem(OLYMPIAD_CERT2, OLYMPIAD_CERT3, OLYMPIAD_CERT4);
		addLevelCheck("olympiad_operator_q0551_08.htm", 85);
		addClassLevelCheck("olympiad_operator_q0551_08.htm", false, ClassLevel.AWAKED);
		addClassLevelCheck("olympiad_operator_q0551_08.htm", true, ClassLevel.THIRD); // Ertheia
		addNobleCheck("olympiad_operator_q0551_08.htm", true);
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
					return "olympiad_operator_q0551_01.htm";
				else if(cond == 1)
				{
					if (st.getQuestItemsCount(OLYMPIAD_CERT2, OLYMPIAD_CERT3, OLYMPIAD_CERT4) == 0)
						return "olympiad_operator_q0551_04.htm";
					else
						return "olympiad_operator_q0551_05.htm";
				}
				if(cond == 2)
				{
					if(st.getQuestItemsCount(OLYMPIAD_CERT4) > 0)
					{
						st.giveItems(OLYMPIAD_CHEST2, 1);
						st.giveItems(POWER_OF_GIGANT, 2);
						st.giveItems(-300, 10000);
						st.takeItems(OLYMPIAD_CERT2, -1);
						st.takeItems(OLYMPIAD_CERT3, -1);
						st.takeItems(OLYMPIAD_CERT4, -1);
						if(st.getPlayer().getLevel() >= 99)
							st.addExpAndSp(0, 71622475);
						st.finishQuest();
						return "olympiad_operator_q0551_07.htm";
					}
					else
						return "olympiad_operator_q0551_05.htm";
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
			htmltext = "olympiad_operator_q0551_06.htm";
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("olympiad_operator_q0551_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("olympiad_operator_q0551_07.htm"))
		{
			if(st.getQuestItemsCount(OLYMPIAD_CERT3) > 0)
			{
				st.giveItems(OLYMPIAD_CHEST, 2);
				st.giveItems(POWER_OF_GIGANT, 2);
				st.giveItems(-300, 10000);
				st.takeItems(OLYMPIAD_CERT2, -1);
				st.takeItems(OLYMPIAD_CERT3, -1);
				st.takeItems(OLYMPIAD_CERT4, -1);
				if(st.getPlayer().getLevel() >= 99)
					st.addExpAndSp(0, 5966391);
				st.finishQuest();
			}
			else if(st.getQuestItemsCount(OLYMPIAD_CERT2) > 0)
			{
				st.giveItems(OLYMPIAD_CHEST, 2);
				st.giveItems(POWER_OF_GIGANT, 1);
				st.giveItems(-300, 6000);
				st.giveItems(OLYMPIAD_CHEST, 1);
				st.takeItems(OLYMPIAD_CERT2, -1);
				st.takeItems(OLYMPIAD_CERT3, -1);
				st.takeItems(OLYMPIAD_CERT4, -1);
				if(st.getPlayer().getLevel() >= 99)
					st.addExpAndSp(0, 7159669);
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
			int count = qs.getInt("count") + 1;
			qs.set("count", count);
			if(count == 5)
			{
				qs.giveItems(OLYMPIAD_CERT2, 1);
				qs.playSound(SOUND_ITEMGET);
			}
			else if(count == 10)
			{
				qs.giveItems(OLYMPIAD_CERT3, 1);
				qs.playSound(SOUND_ITEMGET);
			}
			else if(count == 50)
			{
				qs.giveItems(OLYMPIAD_CERT4, 1);
				qs.setCond(2);
			}
		}
	}
}