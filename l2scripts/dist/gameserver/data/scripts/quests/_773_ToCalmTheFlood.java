package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

public class _773_ToCalmTheFlood extends Quest
{

	private static final int FEY = 32921;

	private static final int F_BLOOD = 46789;
	private static final int S_BLOOD = 46790;

	private static final int[] FAIRY = {22871, 22872, 22873, 22874, 22875, 22876, 22877, 22878, 22879, 22880, 22881, 22882, 22883, 22884, 22885, 22886, 22863, 22864, 22865, 22866, 22867, 22868, 22869, 22870};
	private static final int[] SATIR = {22887, 22888, 22889, 22890, 22891, 22892, 22893, 22894, 22895, 22896, 22897, 22898, 22899, 22900, 22901, 22902, 22903, 22904, 22905, 22906, 22907, 22908, 22909, 22910};

	private static final long EXP_REWARD150 = 481069620;
	private static final int SP_REWARD150 = 577260;

	private static final long EXP_REWARD300 = 962139240;
	private static final int SP_REWARD300 = 1154520;

	private static final long EXP_REWARD600 = 1925278480;
	private static final int SP_REWARD600 = 2309040;

	public _773_ToCalmTheFlood()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(FEY);
		addTalkId(FEY);
		addKillId(FAIRY);
		addKillId(SATIR);
		addLevelCheck("fairy_civilian_quest_q0773_02.htm", 88, 98);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("fairy_civilian_quest_q0773_05.htm") && st.getPlayer().getLevel() >= 88)
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("fairy_civilian_quest_q0773_10.htm") && st.getPlayer().getLevel() >= 88)
		{
			st.giveItems(57, 1448604);
			st.addExpAndSp(EXP_REWARD150, SP_REWARD150);
			st.takeItems(F_BLOOD, -1);
			st.takeItems(S_BLOOD, -1);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("fairy_civilian_quest_q0773_12.htm") && st.getPlayer().getLevel() >= 88)
		{
			st.giveItems(57, 2897208);
			st.addExpAndSp(EXP_REWARD300, SP_REWARD300);
			st.takeItems(F_BLOOD, -1);
			st.takeItems(S_BLOOD, -1);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("fairy_civilian_quest_q0773_13.htm") && st.getPlayer().getLevel() >= 88)
		{
			st.giveItems(57, 5794416);
			st.addExpAndSp(EXP_REWARD600, SP_REWARD600);
			st.takeItems(F_BLOOD, -1);
			st.takeItems(S_BLOOD, -1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case FEY:
				if (cond == 0)
					htmltext = "fairy_civilian_quest_q0773_01.htm";
				else if (cond == 1)
					htmltext = "fairy_civilian_quest_q0773_06.htm";
				else if (cond == 2)
				{
					if(st.getQuestItemsCount(F_BLOOD) >= 300 && st.getQuestItemsCount(S_BLOOD) >= 300)
						htmltext = "fairy_civilian_quest_q0773_08.htm";
					else
						htmltext = "fairy_civilian_quest_q0773_07.htm";
				}
				else if (cond == 3)
					htmltext = "fairy_civilian_quest_q0773_09.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if(ArrayUtils.contains(FAIRY, npc.getNpcId()))
				qs.rollAndGive(F_BLOOD, 1, 1, 600, 70);
			if(ArrayUtils.contains(SATIR, npc.getNpcId()))
				qs.rollAndGive(S_BLOOD, 1, 1, 600, 70);
			if(qs.getQuestItemsCount(F_BLOOD) >= 150 && qs.getQuestItemsCount(S_BLOOD) >= 150)
				qs.setCond(2);
		}
		if(qs.getCond() == 2)
		{
			if(ArrayUtils.contains(FAIRY, npc.getNpcId()))
				qs.rollAndGive(F_BLOOD, 1, 1, 600, 70);
			if(ArrayUtils.contains(SATIR, npc.getNpcId()))
				qs.rollAndGive(S_BLOOD, 1, 1, 600, 70);
			if(qs.getQuestItemsCount(F_BLOOD) >= 600 && qs.getQuestItemsCount(S_BLOOD) >= 600)
				qs.setCond(3);
		}
		return null;
	}
}

