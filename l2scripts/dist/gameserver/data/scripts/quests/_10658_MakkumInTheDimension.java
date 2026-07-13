package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10658_MakkumInTheDimension extends Quest
{
	private static final int RAIS = 34265;

	private static final int[] MOBS = {26194, 26195, 26196, 26197, 26198};

	private static final long EXP_REWARD = 4303647428l;
	private static final int SP_REWARD = 10328753;

	public _10658_MakkumInTheDimension()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(RAIS);
		addTalkId(RAIS);
		addKillId(MOBS);
		addLevelCheck("rias_q10658_02.htm", 100);
		addItemHaveCheck("rias_q10658_03.htm", 47511, 100);
		addQuestCompletedCheck("rias_q10658_02.htm", 928);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("rias_q10658_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("schwann_q0748_03.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("select_str"))
		{
			htmltext = "rias_q10658_09.htm";
			st.giveItems(47509, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("select_dex"))
		{
			htmltext = "rias_q10658_09.htm";
			st.giveItems(47507, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("select_con"))
		{
			htmltext = "rias_q10658_09.htm";
			st.giveItems(47505, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("select_int"))
		{
			htmltext = "rias_q10658_09.htm";
			st.giveItems(47510, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("select_wit"))
		{
			htmltext = "rias_q10658_09.htm";
			st.giveItems(47508, 1, false);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("select_men"))
		{
			htmltext = "rias_q10658_09.htm";
			st.giveItems(47506, 1, false);
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
			case RAIS:
				if (cond == 0)
					htmltext = "rias_q10658_01.htm";
				else if (cond == 1)
					htmltext = "rias_q10658_07.htm";
				else if (cond == 2)
					htmltext = "rias_q10658_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.setCond(2);
		}
		return null;
	}
}
