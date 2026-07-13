package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _035_FindGlitteringJewelry extends Quest
{
	int ROUGH_JEWEL = 7162;
	int ORIHARUKON = 36521;
	int PART_ARM = 36551;
	int GEM_FOR_ACC = 36556;
	int JEWEL_BOX = 7077;

	public _035_FindGlitteringJewelry()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30091);
		addTalkId(30091);
		addTalkId(30879);

		addKillId(20135);

		addQuestItem(ROUGH_JEWEL);
		
		addLevelCheck("30091-6.htm", 60);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		if(event.equals("30091-1.htm") && cond == 0)
		{
			st.setCond(1);
		}
		else if(event.equals("30879-1.htm") && cond == 1)
			st.setCond(2);
		else if(event.equals("30091-3.htm") && cond == 3)
		{
			if(st.getQuestItemsCount(ROUGH_JEWEL) == 10)
			{
				st.takeItems(ROUGH_JEWEL, -1);
				st.setCond(4);
			}
			else
				htmltext = "30091-hvnore.htm";
		}
		else if(event.equals("30091-5.htm") && cond == 4)
			if(st.getQuestItemsCount(ORIHARUKON) >= 95 && st.getQuestItemsCount(PART_ARM) >= 405 && st.getQuestItemsCount(GEM_FOR_ACC) >= 385)
			{
				st.takeItems(ORIHARUKON, 95);
				st.takeItems(PART_ARM, 405);
				st.takeItems(GEM_FOR_ACC, 385);
				st.giveItems(JEWEL_BOX, 1);
				st.finishQuest();
			}
			else
				htmltext = "30091-hvnmat-bug.htm";
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 30091)
		{
			QuestState fwear = st.getPlayer().getQuestState(37);
			if(cond == 0 && st.getQuestItemsCount(JEWEL_BOX) == 0 && fwear != null && fwear.isStarted())
				htmltext = "30091-0.htm";
			else if(cond == 1)
				htmltext = "30091-1r.htm";
			else if(cond == 2)
				htmltext = "30091-1r2.htm";
			else if(cond == 3 && st.getQuestItemsCount(ROUGH_JEWEL) == 10)
				htmltext = "30091-2.htm";
			else if(cond == 4 && (st.getQuestItemsCount(ORIHARUKON) < 95 || st.getQuestItemsCount(PART_ARM) < 405 || st.getQuestItemsCount(GEM_FOR_ACC) < 385))
				htmltext = "30091-hvnmat.htm";
			else if(cond == 4 && st.getQuestItemsCount(ORIHARUKON) >= 95 && st.getQuestItemsCount(PART_ARM) >= 405 && st.getQuestItemsCount(GEM_FOR_ACC) >= 385)
				htmltext = "30091-4.htm";
		}
		else if(npcId == 30879)
			if(cond == 1)
				htmltext = "30879-0.htm";
			else if(cond == 2)
				htmltext = "30879-1r.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		long count = st.getQuestItemsCount(ROUGH_JEWEL);
		if(count < 10)
		{
			st.giveItems(ROUGH_JEWEL, 1);
			if(st.getQuestItemsCount(ROUGH_JEWEL) == 10)
				st.setCond(3);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}