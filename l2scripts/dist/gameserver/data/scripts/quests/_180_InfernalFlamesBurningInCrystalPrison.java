package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _180_InfernalFlamesBurningInCrystalPrison extends Quest
{
	private static final int FIOREN = 33044;
	
	private static final int BAYLOR = 29213; // is this the new baylor???
	

	private static final int SIGN_OF_BAYLOR = 17589;

	private static final int EXP_REWARD = 14000000;	private static final int SP_REWARD = 3360; 	public _180_InfernalFlamesBurningInCrystalPrison()
	{
		super(PARTY_ALL, ONETIME);

		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		addKillId(BAYLOR);
		addQuestItem(SIGN_OF_BAYLOR);
		addLevelCheck("33044-lvl.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33044-5.htm"))
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
		if(npcId == FIOREN)
		{
			if(cond == 0)
				return "33044.htm";
			if(cond == 1)
				return "33044-6.htm";
			if(cond == 2)
			{
				st.takeItems(SIGN_OF_BAYLOR, -1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(17527, 1); //enchant armor R grade
				st.finishQuest();			
				return "33044-7.htm";
			}	
		}
		return htmltext;
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
		if(st.getCond() == 1 && st.getQuestItemsCount(SIGN_OF_BAYLOR) == 0)
		{
			st.giveItems(SIGN_OF_BAYLOR,1);
			st.setCond(2);
		}	
		return null;	
			
	}
}
