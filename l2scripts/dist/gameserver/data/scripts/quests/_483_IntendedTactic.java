package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _483_IntendedTactic extends Quest
{
	//npc
	public static final int ENDE = 33357;
	//mobs
	private static final int[] mobs = {23069, 23070, 23073, 23071, 23072, 23074, 23075};
	private static final int[] bosses = {25811, 25812, 25815, 25809};
	
	private static final int BLOOD_V = 17736;
	private static final int BLOOD_I = 17737;

	private static final int EXP_REWARD = 1500000;	private static final int SP_REWARD = 360; 	public _483_IntendedTactic()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(ENDE);
		addTalkId(ENDE);
		addKillId(mobs);
		addKillId(bosses);
		addQuestItem(BLOOD_V);
		addQuestItem(BLOOD_I);

		addLevelCheck("33357-lvl.htm", 48);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33357-6.htm"))
		{
			st.setCond(1);
		}	
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == ENDE)
		{
			if(cond == 0)
				return "33357.htm";
			if(cond == 1)
				return "33357-8.htm";
			if(cond == 2)
			{
				st.takeItems(BLOOD_V, -1);
				st.takeItems(BLOOD_I, -1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(17624, 1);
				st.finishQuest();			
				return "33357-10.htm";		
			}
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == ENDE)
			htmltext = "33357-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1)
			return null;
		if(ArrayUtils.contains(mobs, npc.getNpcId()) && Rnd.chance(10))
		{
			if(st.getQuestItemsCount(BLOOD_V) > 10)
				return null;
			else
				st.giveItems(BLOOD_V, 1);	
			checkItems(st);	
		}	
		if(ArrayUtils.contains(bosses, npc.getNpcId()))
		{
			if(st.getQuestItemsCount(BLOOD_I) > 0)
				return null;
			st.giveItems(BLOOD_I, 1);
			checkItems(st);
		}
		return null;	
	}	
	private static void checkItems(QuestState st)
	{
		if(st.getQuestItemsCount(BLOOD_V) >= 10 && st.getQuestItemsCount(BLOOD_I) > 0)
			st.setCond(2);
	}
}