package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10743_StrangeFungus extends Quest
{
	// NPC's
	private static final int REICHEL = 33952;
	private static final int MILHE = 33953;

	// Monster's
	private static final int[] MONSTERS = {23455, 23486, 23456};
	private static final String Shriker = "shriker";

	// Item's
	private static final int SPORE = 39530;

	private int shrikercounter;

	private static final int EXP_REWARD = 93982;	private static final int SP_REWARD = 0; 	public _10743_StrangeFungus()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(REICHEL);
		addTalkId(MILHE);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 1023455, Shriker, 99999, MONSTERS);
		addLevelCheck("33952-0.htm", 13/*, 20*/);
		addRaceCheck("33952-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33952-3.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33953-3.htm"))
		{
			st.takeItems(SPORE, -1);
			st.giveItems(37, 1);
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
			case REICHEL:
				if(cond == 0)
					htmltext = "33952-1.htm";
				else if(cond == 1)
					htmltext = "33952-4.htm";
				break;
			case MILHE:
				if (cond == 2)
					htmltext = "33953-1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 1)
			{
				if(npcId != 23456)
				{
					shrikercounter++;
					updateKill(npc, st);
					if(shrikercounter >= 3)
					{
						st.addSpawn(23456, npc.getX(), npc.getY(), npc.getZ());
						shrikercounter = 0;
					}
				}
				else
					st.giveItems(SPORE, 1);
			}
			if(st.getQuestItemsCount(SPORE) >= 10)
				st.unset(Shriker);
				st.setCond(2);
		}
		return null;
	}
}