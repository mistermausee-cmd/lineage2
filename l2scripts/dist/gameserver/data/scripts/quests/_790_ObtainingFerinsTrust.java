package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By SanyaDC	
/*if(Rnd.chance(80))
					st.giveItems(adena, 1);
				else if(Rnd.chance(60))
					st.giveItems(adena, 200);
				else st.giveItems(adena, 9999);*/

public class _790_ObtainingFerinsTrust extends Quest
{
	// NPC's
	private static final int CIFONA = 34055;

	// Monster's
	private static final int[] MONSTERS = {23541, 23542, 23543, 23544, 23546, 23547, 23548, 23548, 23549, 23550, 23551, 23552, 23553, 23555, 23556, 23557, 23558};

	// Item's
	private static final int SOULOFD = 45849;// души
	private static final int RL = 47356;
	private static final int RM = 47357;
	private static final int RH = 47358;
	//private static final int MARKOFTRUSTLOW = 45840;  //Dont know where used 45841 45842
	//private static final int MARKOFTRUSTMID = 45843;  //Dont know where used 45844-45847
	//private static final int MARKOFTRUSTHIGH = 45848;

	//private static final int CHESTEFRI = 46165;


	// Quest item chance drop       
	private static final int CHANCE = 100;


	public _790_ObtainingFerinsTrust()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(CIFONA);
		addKillId(MONSTERS);
		addQuestItem(SOULOFD);
		addLevelCheck("cyphona_q0790_02.htm", 100);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("cyphona_q0790_05a.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("cyphona_q0790_06l.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("cyphona_q0790_06m.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("cyphona_q0790_06h.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("reward.htm") && st.getQuestItemsCount(SOULOFD) == 200)
		{				
					
				st.addExpAndSp(5932440000L, 14237820);					
				st.takeItems(SOULOFD, -1);
				if(Rnd.chance(80))
					st.giveItems(RL, 1);
				else if(Rnd.chance(60))
					st.giveItems(RM, 1);
				else st.giveItems(RH, 1);
				st.finishQuest();
			}	

		else if(event.equalsIgnoreCase("reward.htm") && st.getQuestItemsCount(SOULOFD) == 400)
		{				
					
				st.addExpAndSp(11864880000L, 28475640);					
				st.takeItems(SOULOFD, -1);
				if(Rnd.chance(50))
					st.giveItems(RL, 1);
				else if(Rnd.chance(70))
					st.giveItems(RM, 1);
				else st.giveItems(RH, 1);
				st.finishQuest();
			}
		else if(event.equalsIgnoreCase("reward.htm") && st.getQuestItemsCount(SOULOFD) == 600)
		{				
					
				st.addExpAndSp(17797320000L, 42713460);						
				st.takeItems(SOULOFD, -1);
				if(Rnd.chance(10))
					st.giveItems(RL, 1);
				else if(Rnd.chance(20))
					st.giveItems(RM, 1);
				else st.giveItems(RH, 1);
			st.finishQuest();
			}				
			//if(!st.haveQuestItem(MARKOFTRUSTLOW) && !st.haveQuestItem(MARKOFTRUSTMID) && !st.haveQuestItem(MARKOFTRUSTHIGH))
			
		
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
			case CIFONA:
				if(cond == 0)
				{
					/*if(st.haveQuestItem(MARKOFTRUSTLOW))
						htmltext = "cyphona_q0790_01a.htm";
					else if(st.haveQuestItem(MARKOFTRUSTMID))
						htmltext = "cyphona_q0790_01b.htm";
					else if(st.haveQuestItem(MARKOFTRUSTHIGH))
						htmltext = "cyphona_q0790_01c.htm";
					else*/
						htmltext = "cyphona_q0790_01.htm";
				}
				else if(cond == 1)
					htmltext = "cyphona_q0790_05a.htm";
				else if(cond == 2)
					htmltext = "cyphona_q0790_noe.htm";
				else if(cond == 3)
					htmltext = "cyphona_q0790_noe.htm";
				else if(cond == 4)
					htmltext = "cyphona_q0790_noe.htm";
				else if(cond == 5)
					htmltext = "cyphona_q0790_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == CIFONA)
			htmltext = "cyphona_q0790_13.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 2)
		{
			if(st.rollAndGive(SOULOFD, 1, 1, 200, CHANCE))
				st.setCond(5);
		}
		else if(cond == 3)
		{
			if(st.rollAndGive(SOULOFD, 1, 1, 400, CHANCE))
				st.setCond(5);
		}
		else if(cond == 4)
		{
			if(st.rollAndGive(SOULOFD, 1, 1, 600, CHANCE))
				st.setCond(5);
		}
		return null;
	}
}