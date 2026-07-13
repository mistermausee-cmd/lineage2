package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _759_TheCeaselessNightmareofDwarves extends Quest
{
	//npc
	private static final int DAICHIR = 30537;
	//mob
	private static final int TRASKEN = 29197;
	
	public _759_TheCeaselessNightmareofDwarves()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(DAICHIR);
		addTalkId(DAICHIR);
		addKillId(TRASKEN);
		
		addLevelCheck("daichir_head_priest_q759_00.htm", 98);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("daichir_head_priest_q759_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("daichir_head_priest_q759_06.htm"))
		{
			calculateReward(st);
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
		if(npcId == DAICHIR)
		{
			if(cond == 0)
				htmltext = "daichir_head_priest_q759_01.htm";
			else if(cond == 1)
				htmltext = "daichir_head_priest_q759_04.htm";
			else if(cond == 2)
				htmltext = "daichir_head_priest_q759_05.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == DAICHIR)
			htmltext = "daichir_head_priest_q759_00a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs == null)
			return null;
		if(qs.getCond() != 1)
			return null;
		for(Player player : World.getAroundPlayers(npc, 1500, 1500))
		{
			QuestState thisqs = player.getQuestState(759);
			if(thisqs == null || thisqs.getCond() != 1)
				continue;
			thisqs.setCond(2);	
		}
		return null;
	}
	
	public void calculateReward(QuestState st)
	{

		int itemID = 0;

		if(Rnd.chance(0.05))
			itemID = 17623;
		else if(Rnd.chance(1.15))
		{
			int rndNum = Rnd.get(1, 11);
			switch (rndNum)
			{
				case 1:
					itemID = 35327;
					break;
				case 2:
					itemID = 35328;
					break;
				case 3:
					itemID = 35329;
					break;
				case 4:
					itemID = 35330;
					break;
				case 5:
					itemID = 35331;
					break;
				case 6:
					itemID = 35332;
					break;
				case 7:
					itemID = 35333;
					break;
				case 8:
					itemID = 35334;
					break;
				case 9:
					itemID = 35335;
					break;
				case 10:
					itemID = 35336;
					break;
				case 11:
					itemID = 35337;
					break;
			}
		}
		else
		{
			int rndNum = Rnd.get(1, 6);
			switch (rndNum)
			{
				case 1:
					itemID = 9552;
					break;
				case 2:
					itemID = 9553;
					break;
				case 3:
					itemID = 9554;
					break;
				case 4:
					itemID = 9555;
					break;
				case 5:
					itemID = 9556;
					break;
				case 6:
					itemID = 9557;
			}
		}
		st.giveItems(itemID, 1);
	}
}