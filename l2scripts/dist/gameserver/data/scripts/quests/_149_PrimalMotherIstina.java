package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _149_PrimalMotherIstina extends Quest
{
	private static final int LIMIER = 33293;
	
	private static final int ISXINA_NORMAL = 29195;
	

	private static final int SIGN_OF_SHILEN = 17589;

	private static final int EXP_REWARD = 833065000;	private static final int SP_REWARD = 199935; 	public _149_PrimalMotherIstina()
	{
		super(COMMAND_CHANNEL, ONETIME);

		addStartNpc(LIMIER);
		addTalkId(LIMIER);
		addKillId(ISXINA_NORMAL);
		addQuestItem(SIGN_OF_SHILEN);
		addLevelCheck("33293-lvl.htm", 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33293-5.htm"))
		{
			st.setCond(1);			
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == LIMIER)
		{
			if(cond == 0)
				return "33293.htm";
			if(cond == 1)
				return "33293-7.htm";
			if(cond == 2)
			{
				st.takeItems(SIGN_OF_SHILEN, -1);
				st.giveItems(19455, 1); // isxina bracelet GOD: harmony
				st.giveItems(17527, 10); // isxina bracelet GOD: harmony
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();			
				return "33293-8.htm";
			}	
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == LIMIER)
			htmltext = "33293-comp.htm";
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 && st.getQuestItemsCount(SIGN_OF_SHILEN) == 0)
		{
			st.giveItems(SIGN_OF_SHILEN,1);
			st.setCond(2);
		}	
		return null;	
	}
}
