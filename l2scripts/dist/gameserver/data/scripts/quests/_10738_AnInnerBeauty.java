package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author blacksmoke
 */
public class _10738_AnInnerBeauty extends Quest
{
	private static final int Grakon = 33947;
	private static final int Evna = 33935;
	private static final int GrakonsNote = 39521;
	
	private static final int EXP_REWARD = 2625;	private static final int SP_REWARD = 0; 	public _10738_AnInnerBeauty()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Grakon);
		addTalkId(Grakon, Evna);
		addQuestItem(GrakonsNote);
		addLevelCheck(NO_QUEST_DIALOG, 5/*, 20*/);
		addClassIdCheck(NO_QUEST_DIALOG, 182, 183);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10737);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				qs.giveItems(GrakonsNote, 1);
				htmltext = "33947-4.htm";
				break;
			
			case "qet_rev":
				qs.takeItems(GrakonsNote, 1);
				htmltext = "33935-3.htm";
				qs.addExpAndSp(EXP_REWARD, SP_REWARD);
				qs.finishQuest();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		final int cond = qs.getCond();
		
		switch(npc.getNpcId())
		{
			case Grakon:
				if(cond == 0)
				{
					htmltext = "33947-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33947-4.htm";
				}
				else
				{
					htmltext = "noqu.htm";
				}
				break;
			
			case Evna:
				if(checkStartCondition(npc, qs.getPlayer()) == null && (cond == 1))
				{
					htmltext = "33935-1.htm";
				}
				break;
		}
		
		return htmltext;
	}
}