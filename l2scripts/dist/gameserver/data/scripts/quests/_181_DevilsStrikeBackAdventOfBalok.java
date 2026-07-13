package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _181_DevilsStrikeBackAdventOfBalok extends Quest
{
	// Npc
	private static final int FIOREN = 33044;
	// Monster
	private static final int BALOK = 29218;
	// Items
	private static final int CONTRACT = 17592;
	private static final int EAR = 17527;
	private static final int EWR = 17526;
	private static final int POUCH = 34861;
	
	private static final int EXP_REWARD = 886750000;	private static final int SP_REWARD = 212820; 	public _181_DevilsStrikeBackAdventOfBalok()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		addKillId(BALOK);
		addQuestItem(CONTRACT);
		addLevelCheck("33044-02.htm", 97/*, 99*/);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch (event)
		{
			case "33044-06.htm":
				qs.setCond(1);
				break;
			case "reward":
				qs.addExpAndSp(EXP_REWARD, SP_REWARD);
				qs.giveItems(57, 37128000L);
				qs.finishQuest();
				final int rnd = Rnd.get(2);
				switch (rnd)
				{
					case 0:
						qs.giveItems(EWR, 2);
						return "33044-09.htm";
						
					case 1:
						qs.giveItems(EAR, 2);
						return "33044-10.htm";
						
					case 2:
						qs.giveItems(POUCH, 2);
						return "33044-11.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		final int cond = qs.getCond();
		final Player player = qs.getPlayer();
		if(cond == 0)
			htmltext = "33044-01.htm";
		else if(cond == 1)
			htmltext = "33044-07.htm";
		else if(cond == 2)
			htmltext = "33044-08.htm";
		
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == FIOREN)
			htmltext = "33044-03.htm";
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.setCond(2);
			qs.giveItems(CONTRACT, 1);
		}
		return null;
	}
}
