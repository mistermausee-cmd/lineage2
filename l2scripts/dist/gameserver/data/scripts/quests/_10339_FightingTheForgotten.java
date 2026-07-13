package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10339_FightingTheForgotten extends Quest
{
	private static final String A_LIST = "A_LIST";

    //Квест НПЦ
    private static final int THEODOR = 32975;
	private static final int HADEL = 33344;

    //Квест монстры
    private static final int[] MOBS = new int[]{22935,22936,22937,22931,22934,22933,23349,22938,22932};

	private static final int EXP_REWARD = 238423500;	private static final int SP_REWARD = 57221; 	public _10339_FightingTheForgotten()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(THEODOR);
		addTalkId(HADEL);
		addKillNpcWithLog(2, 533912, A_LIST, 12, MOBS);
		addLevelCheck("no_level.htm", 85);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "accept.htm";
		}	
		else if(event.equalsIgnoreCase("cod2"))
		{
			st.setCond(2);
			return "gl1.htm";
		}	
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == THEODOR)
		{
			if(cond == 0)
				return "1.htm";
			else if(cond == 1)
				return "5.htm";	
		}
		else if(npcId == HADEL)
		{
			if(cond == 1)
				return "1-1.htm";
			else if(cond == 2)
				return "gl1.htm";
			else if(cond == 3)
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(57, 528210);
				st.finishQuest();	
				return "endquest.htm";
			}
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(3);
		}
		return null;
	}	
}