package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10770_InSearchoftheGrail extends Quest
{
	// NPC's
	private static final int LOREIN = 30673;
	private static final int YANSEN = 30484;

	// Monster's
	private static final int[] MONSTERS = {20213, 20214, 20216, 20217, 21036};
	// Item's
	
	private static final int SHINEFRAGMENT = 39711;
	private static final int ENCHANTARMOR = 23420;
	private static final int ENCHANTWEAPON = 23414;

	private static final int EXP_REWARD = 4175045;
	private static final int SP_REWARD = 562; 

	public _10770_InSearchoftheGrail()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(LOREIN);
		addTalkId(YANSEN);
		addKillId(MONSTERS);
		addLevelCheck("30673-0.htm", 40);
		addRaceCheck("30673-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30673-5.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30484-2.htm"))
		{
			st.takeItems(SHINEFRAGMENT, -1);
		}
		else if(event.equalsIgnoreCase("30484-4.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
			case LOREIN:
				if(cond == 0)
					htmltext = "30673-1.htm";
				else if (cond == 1)
					htmltext = "30673-6.htm";
				else if (cond == 2)
					htmltext = "30673-7.htm";
			break;

			case YANSEN:
				if(cond == 2)
				{
					if(st.haveQuestItem(SHINEFRAGMENT))
						htmltext = "30484-1.htm";
					else
						htmltext = "30484-3.htm";
				}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(st.rollAndGive(SHINEFRAGMENT, 1, 1, 20, 60))
				st.setCond(2);
		}
		return null;
	}
}