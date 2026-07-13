package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Bonux
**/
public class _824_AttackTheCommandPost extends Quest
{
	// NPC's
	private static final int DEVIANNE_NPC_ID = 34089;
	private static final int ELIKIA_NPC_ID = 34057;
	private static final int BURNSTEIN_MONSTER_ID = 26136;

	// Items
	private static final int ADEN_VANGUARD_SUPPLY_BOX = 46283;

	//Reward EXP SP
	private static final long EXP_REWARD = 3954960000L;
	private static final long SP_REWARD = 9491880L;

	private static final int[] MONSTERS_A = { BURNSTEIN_MONSTER_ID };
	private static final String A_LIST = "A_LIST";

	public _824_AttackTheCommandPost()
	{
		super(PARTY_ALL, DAILY);

		addStartNpc(DEVIANNE_NPC_ID);
		addTalkId(ELIKIA_NPC_ID);
		addLevelCheck("devianne_inquiry_q0824_02.htm", 100);
		addKillNpcWithLog(1, 584605, A_LIST, 1, MONSTERS_A);
	}

	@Override
	public String onAcceptQuest(QuestState st, NpcInstance npc)
	{
		if(npc.getNpcId() == DEVIANNE_NPC_ID)
		{
			st.setCond(1);
			return "devianne_inquiry_q0824_05.htm";
		}
		return null;
	}

	@Override
	public String onMenuSelect(long reply, QuestState st, NpcInstance npc)
	{
		switch(npc.getNpcId())
		{
			case DEVIANNE_NPC_ID:
			{
				if(reply == 1)
					return "devianne_inquiry_q0824_03.htm";
				else if(reply == 2)
					return "devianne_inquiry_q0824_04.htm";
				break;
			}
			case ELIKIA_NPC_ID:
			{
				if(reply == 10)
				{
					st.getPlayer().addExpAndSp(EXP_REWARD, SP_REWARD);
					st.giveItems(ADEN_VANGUARD_SUPPLY_BOX, 1, false);
					st.finishQuest();
					return "ellikia_vanguard_q0824_03.htm";
				}
				break;
			}
		}
		return null;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch(npc.getNpcId())
		{
			case DEVIANNE_NPC_ID:
			{
				if(cond == 0)
					htmltext = "devianne_inquiry_q0824_01.htm";
				else if(cond == 1)
					htmltext = "devianne_inquiry_q0824_06.htm";
				else if(cond == 2)
					htmltext = "devianne_inquiry_q0824_07.htm";
				break;
			}
			case ELIKIA_NPC_ID:
			{
				if(cond == 1)
					htmltext = "ellikia_vanguard_q0824_01.htm";
				else if(cond == 2)
					htmltext = "ellikia_vanguard_q0824_02.htm";
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			if(updateKill(npc, st))
			{
				st.unset(A_LIST);
				st.setCond(2);
			}
		}
		return null;
	}
}