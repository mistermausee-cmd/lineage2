package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _746_TheReasonOneCannotWait extends Quest
{
	private static final int MOEN = 30196;
	private static final int ENDI = 33845;
	private static final int PETR = 33864;

	private static final int SPORE = 47050;

	private static final int[] MOBS = {20555, 20558, 23306, 23307, 23308, 23305, 23309, 23310};

	private static final long EXP_REWARD = 32123859;
	private static final int SP_REWARD = 2132;

	public _746_TheReasonOneCannotWait()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(MOEN);
		addTalkId(MOEN, ENDI, PETR);
		addKillId(MOBS);
		addQuestItem(SPORE);
		addLevelCheck("mouen_q0746_02.htm", 52, 57);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("mouen_q0746_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("spore_ranger_andy_q0746_03.htm"))
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("petterzan_q0746_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		switch (npcId)
		{
			case MOEN:
				if (cond == 0)
				{
					if(st.getPlayer().isBaseClassActive())
						htmltext = "mouen_q0746_03.htm";
					else
						htmltext = "mouen_q0746_01.htm";
				}
				else if (cond == 1)
					htmltext = "mouen_q0746_07.htm";
				break;

			case ENDI:
				if (cond == 1)
					htmltext = "spore_ranger_andy_q0746_01.htm";
				else if (cond == 2)
					htmltext = "spore_ranger_andy_q0746_04.htm";
				else if (cond == 3)
					htmltext = "spore_ranger_andy_q0746_05.htm";
				break;

			case PETR:
				if (cond == 2)
					htmltext = "petterzan_q0746_01.htm";
				else if (cond == 3)
					htmltext = "petterzan_q0746_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(SPORE, 1, 1, 200, 100));
				qs.setCond(3);
		}
		return null;
	}
}

