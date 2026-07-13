package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10540_ThwartingMimirsPlan extends Quest
{
	private static final int KRENAT = 34237;

	private static final int MIMIR = 26137;

	private static final long EXP_REWARD = 3954960000l;
	private static final long SP_REWARD = 9491880;

	public _10540_ThwartingMimirsPlan()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(KRENAT);
		addTalkId(KRENAT);
		addKillId(MIMIR);

		addLevelCheck("leader_kekrops_q10540_02.htm", 100/*, 20*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("leader_kekrops_q10540_05.htm"))
		{
			st.setCond(1);
		}

		else if (event.equalsIgnoreCase("leader_kekrops_q10540_08.htm"))
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
			case KRENAT:
				if (cond == 0)
					htmltext = "leader_kekrops_q10540_01.htm";
				else if (cond == 1)
					htmltext = "leader_kekrops_q10540_06.htm";
				else if (cond == 2)
					htmltext = "leader_kekrops_q10540_07.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
		return null;
	}
}
