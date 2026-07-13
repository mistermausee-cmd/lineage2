package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10761_AnOrcinLove extends Quest
{
	// NPC's
	private static final int Borbo = 33966;

	// Monster's
	private static final int[] MONSTERS = {20495, 20496, 20497, 20498, 20499, 20500, 20501, 20546, 20494};
	private static final String OrcTurek = "TUREK";
	// Item's
	

	private static final int EXP_REWARD = 706841;	private static final int SP_REWARD = 85; 	public _10761_AnOrcinLove()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(Borbo);
		addTalkId(Borbo);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 576111, OrcTurek, 30, MONSTERS);
		addLevelCheck("33966-0.htm", 30);
		addRaceCheck("33966-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33966-5.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33966-8.htm"))
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
			case Borbo:
				if(cond == 0)
					htmltext = "33966-1.htm";
				else if (cond == 1)
					htmltext = "33966-6.htm";
				else if (cond == 2)
					htmltext = "33966-7.htm";
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
				st.unset(OrcTurek);
				st.setCond(2);
			}
		}
		return null;
	}
}