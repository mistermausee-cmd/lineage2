package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author blacksmoke
 */
public class _10741_ADraughtForTheCold extends Quest
{
	// Npcs
	private static final int Sivanthe = 33951;
	private static final int Leira = 33952;
	// Monsters
	private static final int Honeybee = 23452;
	private static final int Kiku = 23453;
	private static final int RobustHoneybee = 23484;
	// Items
	private static final int EmptyHoneyJar = 39527;
	private static final int SweetHoney = 39528;
	private static final int NutritiousMeat = 39529;
	
	private static final int EXP_REWARD = 22973;	private static final int SP_REWARD = 2; 	public _10741_ADraughtForTheCold()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Sivanthe);
		addTalkId(Sivanthe, Leira);
		addKillId(Honeybee, Kiku, RobustHoneybee);
		addQuestItem(EmptyHoneyJar, SweetHoney, NutritiousMeat);
		addLevelCheck(NO_QUEST_DIALOG, 10/*, 20*/);
		addClassIdCheck(NO_QUEST_DIALOG, 182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				qs.giveItems(EmptyHoneyJar, 10);
				htmltext = "33951-3.htm";
				break;
			
			case "qet_rev":
				qs.takeItems(SweetHoney, 10);
				qs.takeItems(NutritiousMeat, 10);
				htmltext = "33952-2.htm";
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
			case Sivanthe:
				switch(cond)
				{
					case 0:
						htmltext = "33951-1.htm";
						break;
					
					case 1:
						htmltext = "33951-4.htm";
						break;
					
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case Leira:
				if(cond == 2)
				{
					htmltext = "33952-1.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			switch(npc.getNpcId())
			{
				case Honeybee:
				case RobustHoneybee:
					if(qs.getQuestItemsCount(EmptyHoneyJar) > 0)
					{
						qs.takeItems(EmptyHoneyJar, 1);
						qs.giveItems(SweetHoney, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
				
				case Kiku:
					if(qs.getQuestItemsCount(NutritiousMeat) < 10)
					{
						qs.giveItems(NutritiousMeat, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
			}
			
			if((qs.getQuestItemsCount(SweetHoney) >= 10) && (qs.getQuestItemsCount(NutritiousMeat) >= 10))
			{
				qs.setCond(2);
			}
		}
		return null;
	}
}
