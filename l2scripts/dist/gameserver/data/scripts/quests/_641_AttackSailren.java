package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _641_AttackSailren extends Quest
{
	//NPC
	private static int STATUE = 32109;

	//MOBS
	private static int VEL1 = 22196;
	private static int VEL2 = 22197;
	private static int VEL3 = 22198;
	private static int VEL4 = 22218;
	private static int VEL5 = 22223;
	private static int PTE = 22199;
	//items
	private static int FRAGMENTS = 8782;
	private static int GAZKH = 8784;

	private static final int EXP_REWARD = 1500000;	private static final int SP_REWARD = 360; 	public _641_AttackSailren()
	{
		super(PARTY_ONE, REPEATABLE);

		addStartNpc(STATUE);

		addKillId(VEL1);
		addKillId(VEL2);
		addKillId(VEL3);
		addKillId(VEL4);
		addKillId(VEL5);
		addKillId(PTE);

		addQuestItem(FRAGMENTS);

		addLevelCheck("statue_of_shilen_q0641_02.htm", 77);
		addQuestCompletedCheck("statue_of_shilen_q0641_02.htm", 126);			
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("statue_of_shilen_q0641_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("statue_of_shilen_q0641_08.htm"))
		{
			st.takeItems(FRAGMENTS, -1);
			st.giveItems(GAZKH, 1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(cond == 0)
			htmltext = "statue_of_shilen_q0641_01.htm";
		else if(cond == 1)
			htmltext = "statue_of_shilen_q0641_05.htm";
		else if(cond == 2)
			htmltext = "statue_of_shilen_q0641_07.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(FRAGMENTS) < 30)
		{
			st.giveItems(FRAGMENTS, 1);
			if(st.getQuestItemsCount(FRAGMENTS) == 30)
				st.setCond(2);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}