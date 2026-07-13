package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10387_Soulless extends Quest
{
	//reward items
	//group1
	private static final int ANVIL = 19307;
	private static final int WARSMITH = 19307;
	private static final int HOLDER = 19514;
	private static final int MOLD = 19515;
	private static final int ANVIL2 = 19516;
	private static final int WARSMITH2 = 19517;	
	//group2
	private static final int ENCH_WEAPON = 17526;
	private static final int ENCH_ARMOR = 17527;
	//group3
	private static final int HARDNER = 32279;
		
	private static final int HESET = 33780;
	private static final int BERNA = 33796;

	private static final int EXP_REWARD = 817330500;	private static final int SP_REWARD = 196159; 	public _10387_Soulless()
	{
		super(PARTY_ALL, ONETIME);
		
		addStartNpc(HESET);
		addTalkId(HESET);
		addTalkId(BERNA);
		
		addKillId(25901);
		
		addLevelCheck("you cannot procceed with this quest until you have completed the Mystrerious Journey quest", 93);
		addQuestCompletedCheck("you cannot procceed with this quest until you have completed the Mystrerious Journey quest", 10386);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("acceptedBerma.htm"))
		{
			st.setCond(2);
		}
		
		if(event.startsWith("getRev"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			if(event.equalsIgnoreCase("getRev1"))
			{
				st.giveItems(ANVIL, 1);
				st.giveItems(WARSMITH, 1);
				st.giveItems(HOLDER, 1);
				st.giveItems(MOLD, 1);
				st.giveItems(ANVIL2, 1);
				st.giveItems(WARSMITH2, 1);
			}
			else if(event.equalsIgnoreCase("getRev2"))
			{
				st.giveItems(ENCH_WEAPON, 1);
				st.giveItems(ENCH_ARMOR, 1);
			}	
			else if(event.equalsIgnoreCase("getRev3"))
			{
				st.giveItems(HARDNER, 4);
			}			
			st.finishQuest();
			return "endquest.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == HESET)
		{
			if(cond == 0)
				htmltext = "start.htm";
		}
		else if(npcId == BERNA)
		{
			if(cond == 1)
				htmltext = "berna.htm";
			else if(cond == 3)
				return "collected.htm";
		}		
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 3)
			qs.setCond(3);
		return null;
	}
}