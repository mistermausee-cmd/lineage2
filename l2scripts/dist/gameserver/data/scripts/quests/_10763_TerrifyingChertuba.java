package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10763_TerrifyingChertuba extends Quest
{
	// NPC's
	private static final int Borbo = 33966;

	// Monster's
	private static final int[] MONSTERS = {23422, 23423};
	private static final String Illusion = "ILLUSION";
	// Item's
	
	private static final int CHAINKEY2 = 39489;

	private static final int EXP_REWARD = 1529848;	private static final int SP_REWARD = 215; 	public _10763_TerrifyingChertuba()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(Borbo);
		addTalkId(Borbo);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 576312, Illusion, 1, MONSTERS);
		addQuestCompletedCheck("33966-0.htm", 10762);
		addLevelCheck("33966-0.htm", 34);
		addRaceCheck("33966-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33966-3.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33966-6.htm"))
		{
			st.takeItems(CHAINKEY2, -1);
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
			case Borbo:
				if(cond == 0)
					htmltext = "33966-1.htm";
				else if (cond == 1)
					htmltext = "33966-4.htm";
				else if (cond == 2)
					htmltext = "33966-5.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.giveItems(CHAINKEY2, 1, false);
				st.unset(Illusion);
				st.setCond(2);
			}
		}
		return null;
	}
}