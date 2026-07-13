package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Krash
 */
public class _10744_StrongerThanSteel extends Quest
{
	// Npcs
	private static final int MILHE = 33953;
	private static final int DOLKIN = 33954;
	// Monsters
	private static final int Treant = 23457;
	private static final int Leafie = 23458;
	// Drops
	private static final int Treant_leaf = 39532;
	private static final int Leafie_leaf = 39531;
	
	private static final int EXP_REWARD = 153994;	private static final int SP_REWARD = 5; 	public _10744_StrongerThanSteel()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(MILHE);
		addTalkId(DOLKIN);
		addQuestItem(Treant_leaf, Leafie_leaf);
		addKillId(Treant, Leafie);
		addLevelCheck("milie_q10744_02.htm", 15/*, 20*/);
		addClassIdCheck("milie_q10744_02.htm", 182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch (event)
		{
			case "milie_q10744_04.htm":
				qs.setCond(1);
				break;
			case "quest_middle":
				qs.setCond(2);
				htmltext = "33954-2.htm";
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		final int cond = qs.getCond();
		
		switch (npc.getNpcId())
		{
			case MILHE:
				switch (cond)
				{
					case 0:
						htmltext = "milie_q10744_01.htm";
						break;
					case 1:
						htmltext = "milie_q10744_05.htm";
						break;
				}
				break;
			case DOLKIN:
				switch (cond)
				{
					case 1:
						htmltext = "33954-1.htm";
						break;
					case 3:
						htmltext = "33954-3.htm";
						qs.takeItems(Treant_leaf, 20);
						qs.takeItems(Leafie_leaf, 15);
						qs.addExpAndSp(EXP_REWARD, SP_REWARD);
						qs.finishQuest();
						break;
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() == 2)
		{
			switch (npc.getNpcId())
			{
				case Treant:
					if(qs.getQuestItemsCount(Treant_leaf) < 20)
					{
						qs.giveItems(Treant_leaf, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
				
				case Leafie:
					if(qs.getQuestItemsCount(Leafie_leaf) < 15)
					{
						qs.giveItems(Leafie_leaf, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
			}
			
			if ((qs.getQuestItemsCount(Treant_leaf) >= 20) && (qs.getQuestItemsCount(Leafie_leaf) >= 15))
			{
				qs.setCond(3);
			}
		}
		return null;
	}
}