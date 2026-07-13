package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10305_UnstoppableFutileEfforts extends Quest
{
	public static final String A_LIST = "A_LIST";
	//npc
	private static final int NOETI = 32895;

	private static final int EXP_REWARD = 34971975;
	private static final int SP_REWARD = 8393; 

	public _10305_UnstoppableFutileEfforts()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(NOETI);
		addTalkId(NOETI);

		addLevelCheck("32895-00.htm", 88);
		addQuestCompletedCheck("32895-00.htm", 10302);
		addKillNpcWithLog(1, 1032919, A_LIST, 5, 32919);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32895-05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("rud1"))
		{
			st.giveItems(9546, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud2"))
		{
			st.giveItems(9547, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud3"))
		{
			st.giveItems(9548, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud4"))
		{
			st.giveItems(9549, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud5"))
		{
			st.giveItems(9550, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud6"))
		{
			st.giveItems(9551, 15);
			return onEvent("32895-08.htm", st, npc);
		}
		if(event.equalsIgnoreCase("32895-08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 1007735);
			st.finishQuest();
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == NOETI)
		{
			if(cond == 0)
				return "32895.htm";
			else if(cond == 1)
				return "32895-06.htm";
			else if(cond == 2)
				return "32895-07.htm";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == NOETI)
			htmltext = "32895-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}

		return null;
	}	
}