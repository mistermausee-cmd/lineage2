package quests;

import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk
//TODO CHECK REWARD 

public class _10846_BloodyBattleMeetingTheCommander extends Quest
{
	private static final int XYRAK = 34064;
	private static final int DEVIAN = 34089;

	private static final int MOBS = 23587;

	private static final long EXP_REWARD = 7262301690l;
	private static final int SP_REWARD = 17429400;
	private static final int FP_REWARD = 100;

	public _10846_BloodyBattleMeetingTheCommander()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(XYRAK);
		addTalkId(XYRAK, DEVIAN);
		addKillId(MOBS);
		addLevelCheck("blackbird_hurak_q10846_02.htm", 101);
		addQuestCompletedCheck("blackbird_hurak_q10846_02.htm", 10845);
		addFactionLevelCheck("blackbird_hurak_q10846_02.htm", FactionType.KINGDOM_ROYALGUARD, 4);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (event.equalsIgnoreCase("blackbird_hurak_q10846_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("devianne_inquiry_q10846_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(39738, 1);
			st.giveItems(46158, 1);
			st.getPlayer().getFactionList().addProgress(FactionType.KINGDOM_ROYALGUARD, FP_REWARD); //TODO FIND
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
			case XYRAK:
				if (cond == 0)
					htmltext = "blackbird_hurak_q10846_05.htm";
				else if (cond == 1)
					htmltext = "blackbird_hurak_q10846_06.htm";
				break;

			case DEVIAN:
				if (cond == 1)
					htmltext = "devianne_inquiry_q10846_01.htm";
				else if (cond == 2)
					htmltext = "devianne_inquiry_q10846_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
			qs.setCond(2);
		return null;
	}
}
