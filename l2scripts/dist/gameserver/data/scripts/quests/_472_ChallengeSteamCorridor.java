package quests;


import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _472_ChallengeSteamCorridor extends Quest
{
	//npc
	public static final int FIOREN = 33044;
	
	//mobs
	public static final int[] KECHI = {25797, 26111, 26112, 26113};


public _472_ChallengeSteamCorridor()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(FIOREN);
		addKillId(KECHI);
		addLevelCheck("33044-lvl.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("33044-3.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("33044-6.htm"))
		{
			st.giveItems(30387,2); // hell proof
			st.finishQuest();		
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == FIOREN)
		{
			if(cond == 0)
				return "33044.htm";
			if(cond == 1)
				return "33044-4.htm";
			if(cond == 2)
				return "33044-5.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == FIOREN)
			htmltext = "33044-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		st.setCond(2);
		return null;
	}	
}