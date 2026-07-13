package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _774_DreamingOfPeace extends Quest
{
	private static final int NERUPA = 30370;

	private static final int[] MOBS = {22867, 22875, 22883, 22891, 22899, 22907};

	private static final long EXP_REWARD300 = 724334400;
	private static final int SP_REWARD300 = 820890;

	private static final long EXP_REWARD600 = 1448668800;
	private static final int SP_REWARD600 = 1641780;

	private static final long EXP_REWARD900 = 2173003200L;
	private static final int SP_REWARD900 = 2462670;
	
	private static final long EXP_REWARD1200 = 2897337600l;
	private static final int SP_REWARD1200 = 3283560;

	public static final String A_LIST = "A_LIST";

	public _774_DreamingOfPeace()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(NERUPA);
		addTalkId(NERUPA);
		addKillNpcWithLog(1, 77405, A_LIST, 300, MOBS);
		addKillNpcWithLog(2, 77405, A_LIST, 1200, MOBS);
		addLevelCheck("nerupa_q0774_02.htm", 90, 100);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (event.equalsIgnoreCase("nerupa_q0774_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("nerupa_q0774_10.htm") && st.getInt(A_LIST) >= 300 && st.getInt(A_LIST) <= 599 && st.getPlayer().getLevel() >= 90)
		{
			st.giveItems(57, 1509354);
			st.addExpAndSp(EXP_REWARD300, SP_REWARD300);
			st.finishQuest();
		}
		else if (event.equalsIgnoreCase("nerupa_q0774_12.htm") && st.getInt(A_LIST) >= 600 && st.getInt(A_LIST) <= 899 && st.getPlayer().getLevel() >= 90)
		{
			st.giveItems(57, 3018708);
			st.addExpAndSp(EXP_REWARD600, SP_REWARD600);
			st.finishQuest();
		}
		else if (event.equalsIgnoreCase("nerupa_q0774_12.htm") && st.getInt(A_LIST) >= 900 && st.getInt(A_LIST) <= 1199 && st.getPlayer().getLevel() >= 90)
		{
			st.giveItems(57, 4528062);
			st.addExpAndSp(EXP_REWARD900, SP_REWARD900);
			st.finishQuest();
		}
		else if (event.equalsIgnoreCase("nerupa_q0774_13.htm")  && st.getInt(A_LIST) >= 1200 && st.getPlayer().getLevel() >= 90)
		{
			st.giveItems(57, 6037416);
			st.addExpAndSp(EXP_REWARD1200, SP_REWARD1200);
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
			case NERUPA:
				if (cond == 0)
					htmltext = "nerupa_q0774_01.htm";
				else if (cond == 1)
					htmltext = "nerupa_q0774_06.htm";
				else if (cond == 2)
				{
					if(st.getInt(A_LIST) >= 600)
						htmltext = "nerupa_q0774_08.htm";
					else
						htmltext = "nerupa_q0774_07.htm";
				}
				else if (cond == 3 && st.getInt(A_LIST) >= 1200)
					htmltext = "fairy_civilian_quest_q0773_09.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() == 1)
		{
			if (updateKill(npc, qs))
			{
				qs.setCond(2);
			}
		}
		if (qs.getCond() == 2)
		{
			if (updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(3);
			}
		}
		return null;
	}
}