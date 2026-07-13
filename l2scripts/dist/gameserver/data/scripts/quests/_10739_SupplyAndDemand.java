package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;

/**
 * @author: Krash
 */
public class _10739_SupplyAndDemand extends Quest
{
	// Npcs
	private static final int Evna = 33935;
	private static final int Denya = 33934;
	private static final int Pelu = 33936;
	private static final int Ceri = 33937;
	private static final int Sivanthe = 33951;
	// Items
	private static final int Weapon_Supply_Box = 39522;
	private static final int Armor_Supply_Box = 39523;
	private static final int Grocery_Supply_Box = 39524;
	private static final int Accessory_Supply_Box = 39525;
	private static final int Leather_Shirt = 21;
	private static final int Leather_Pants = 29;
	private static final int Apprentice_Earring = 112;
	private static final int Necklace_of_Knowledge = 906;
	
	private static final int EXP_REWARD = 8136;	private static final int SP_REWARD = 0; 	public _10739_SupplyAndDemand()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Evna);
		addTalkId(Evna, Denya, Pelu, Ceri, Sivanthe);
		addQuestItem(Weapon_Supply_Box);
		addLevelCheck(NO_QUEST_DIALOG, 6/*, 20*/);
		addClassIdCheck(NO_QUEST_DIALOG, 182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "evna_cont":
				qs.setCond(1);
				qs.giveItems(Weapon_Supply_Box, 1);
				htmltext = "33935-3.htm";
				break;
			
			case "denya_cont":
				qs.setCond(2);
				qs.giveItems(Armor_Supply_Box, 1);
				htmltext = "33934-2.htm";
				break;
			
			case "pelu_cont":
				qs.setCond(3);
				qs.giveItems(Grocery_Supply_Box, 1);
				htmltext = "33936-2.htm";
				break;
			
			case "ceri_cont":
				qs.setCond(4);
				qs.giveItems(Accessory_Supply_Box, 1);
				htmltext = "33937-2.htm";
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
			case Evna:
				if(cond == 0)
				{
					htmltext = "33935-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33935-4.htm";
				}
				break;
			
			case Denya:
				if(cond == 1)
				{
					htmltext = "33934-1.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33934-3.htm";
				}
				break;
			
			case Pelu:
				if(cond == 2)
				{
					htmltext = "33936-1.htm";
				}
				else if(cond == 3)
				{
					htmltext = "33936-3.htm";
				}
				break;
			
			case Ceri:
				if(cond == 3)
				{
					htmltext = "33937-1.htm";
				}
				else if(cond == 4)
				{
					htmltext = "33937-3.htm";
				}
				break;
			
			case Sivanthe:
				if(cond == 4)
				{
					qs.giveItems(Leather_Shirt, 1);
					qs.giveItems(Leather_Pants, 1);
					qs.giveItems(57, 1400);
					qs.giveItems(Apprentice_Earring, 1);
					qs.giveItems(Apprentice_Earring, 1);
					qs.giveItems(Necklace_of_Knowledge, 1);
					qs.takeItems(Weapon_Supply_Box, 1);
					qs.takeItems(Armor_Supply_Box, 1);
					qs.takeItems(Grocery_Supply_Box, 1);
					qs.takeItems(Accessory_Supply_Box, 1);
					qs.addExpAndSp(EXP_REWARD, SP_REWARD);
					htmltext = "33951-1.htm";
					qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
					qs.finishQuest();
				}
				break;
		}
		
		return htmltext;
	}
}