package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _036_MakeASewingKit extends Quest
{
	int REINFORCED_STEEL = 7163;
	int ARTISANS_FRAME = 36521;
	int ORIHARUKON = 36561;
	int SEWING_KIT = 7078;

	public _036_MakeASewingKit()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30847);
		addTalkId(30847);
		addTalkId(30847);

		addKillId(20566);

		addQuestItem(REINFORCED_STEEL);
		
		addLevelCheck("head_blacksmith_ferris_q0036_0103.htm", 60);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		if(event.equals("head_blacksmith_ferris_q0036_0104.htm") && cond == 0)
		{
			st.setCond(1);
		}
		else if(event.equals("head_blacksmith_ferris_q0036_0201.htm") && cond == 2)
		{
			st.takeItems(REINFORCED_STEEL, 5);
			st.setCond(3);
		}
		else if(event.equals("head_blacksmith_ferris_q0036_0301.htm"))
			if(st.getQuestItemsCount(ORIHARUKON) >= 360 && st.getQuestItemsCount(ARTISANS_FRAME) >= 180)
			{
				st.takeItems(ORIHARUKON, 360);
				st.takeItems(ARTISANS_FRAME, 180);
				st.giveItems(SEWING_KIT, 1);
				st.finishQuest();
			}
			else
				htmltext = "head_blacksmith_ferris_q0036_0203.htm";
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		QuestState fwear = st.getPlayer().getQuestState(37);
		if (npc.getNpcId() == 30847)
		{
			if (cond == 0 && st.getQuestItemsCount(SEWING_KIT) == 0 && fwear != null && fwear.isStarted())
			htmltext = "head_blacksmith_ferris_q0036_0101.htm";
		else if(cond == 1 && st.getQuestItemsCount(REINFORCED_STEEL) < 5)
			htmltext = "head_blacksmith_ferris_q0036_0106.htm";
		else if(cond == 2 && st.getQuestItemsCount(REINFORCED_STEEL) == 5)
			htmltext = "head_blacksmith_ferris_q0036_0105.htm";
		else if(cond == 3 && (st.getQuestItemsCount(ORIHARUKON) < 360 || st.getQuestItemsCount(ARTISANS_FRAME) < 180))
			htmltext = "head_blacksmith_ferris_q0036_0204.htm";
		else if(cond == 3 && st.getQuestItemsCount(ORIHARUKON) >= 360 && st.getQuestItemsCount(ARTISANS_FRAME) >= 180)
			htmltext = "head_blacksmith_ferris_q0036_0203.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(REINFORCED_STEEL) < 5)
		{
			st.giveItems(REINFORCED_STEEL, 1);
			if(st.getQuestItemsCount(REINFORCED_STEEL) == 5)
				st.setCond(2);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}