package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author blacksmoke, Krash
 */
public class _10733_TheTestForSurvival extends Quest
{
	// Npcs
	private static final int Gereth = 33932;
	private static final int Dia = 34005;
	private static final int Katalin = 33943;
	private static final int Ayanthe = 33942;

	// Item
	private static final int Gereth_Recommendtion = 39519;

	private static final int EXP_REWARD = 295;	private static final int SP_REWARD = 2; 	public _10733_TheTestForSurvival()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Gereth);
		addTalkId(Gereth, Dia, Katalin, Ayanthe);
		addQuestItem(Gereth_Recommendtion);
		addLevelCheck(NO_QUEST_DIALOG, 1/*, 20*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10732);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;

		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				qs.showTutorialClientHTML("QT_027_Quest_01");
				qs.giveItems(Gereth_Recommendtion, 1);
				htmltext = "33932-2.htm";
				break;
			case "quest_fighter_cont":
				qs.setCond(2);
				htmltext = "34005-3.htm";
				break;
			case "quest_wizard_cont":
				qs.setCond(3);
				htmltext = "34005-6.htm";
				break;
			case "qet_rev":
				htmltext = "33943-2.htm";
				qs.takeItems(Gereth_Recommendtion, 1);
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
			case Gereth:
				switch(cond)
				{
					case 0:
						htmltext = "33932-1.htm";
						break;
					case 1:
						htmltext = "33932-3.htm";
						break;
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			case Dia:
				if(cond == 0)
					htmltext = "34005-nc.htm";
				else if((cond == 1) && (qs.getQuestItemsCount(Gereth_Recommendtion) > 0))
				{
					if(qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
						htmltext = "34005-1.htm";
					else if(qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
						htmltext = "34005-4.htm";
				}
				break;
			case Katalin:
				if(cond == 0)
					htmltext = "33943-4.htm";
				else if((cond == 2) && (qs.getQuestItemsCount(Gereth_Recommendtion) > 0))
					htmltext = "33943-1.htm";
				break;
			case Ayanthe:
				if(cond == 0)
					htmltext = "33942-4.htm";
				else if((cond == 3) && (qs.getQuestItemsCount(Gereth_Recommendtion) > 0))
					htmltext = "33942-1.htm";
				break;
		}
		return htmltext;
	}
}