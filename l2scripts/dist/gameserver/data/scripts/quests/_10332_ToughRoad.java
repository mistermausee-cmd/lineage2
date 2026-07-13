package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk dev.fairytale-world.ru
public class _10332_ToughRoad extends Quest
{
	private static final int batis = 30332;
	private static final int kakai = 30565;

	private static final int EXP_REWARD = 42250;	private static final int SP_REWARD = 20; 	public _10332_ToughRoad()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(kakai);
		addTalkId(kakai);
		addTalkId(batis);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 20/*, 40*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			st.giveItems(17582, 1, false);
			htmltext = "0-2.htm";
		}
		
		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "1-3.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeAllItems(17582);
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

		if(npcId == kakai)
		{
			if(cond == 0)
				htmltext = "0-1.htm";
			else if(cond == 1)
				htmltext = "0-3.htm";
		} 
		else if(npcId == batis)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == batis)
			htmltext = "1-c.htm";
		return htmltext;
	}	
}