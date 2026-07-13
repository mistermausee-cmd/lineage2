package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.Location;

/**
 * @author Krash
 */
public class _10745_TheSecretIngredients extends Quest
{
	// Npcs
	private static final int Dolkin = 33954;
	private static final int Dolkin2 = 34002;
	private static final int Karla = 33933;
	// Monsters
	private static final int Karaphon = 23459;
	// Items
	private static final int Secret_Ingredients = 39533;
	private static final int Dolkin_Report = 39534;
	// Rewards
	private static final int Faeron_Support_Box_Warrior = 40262;
	private static final int Faeron_Support_Box_Mage = 40263;
	// Other
	private int killedKaraphon;
	
	private static final int EXP_REWARD = 241076;	private static final int SP_REWARD = 5; 	public _10745_TheSecretIngredients()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Dolkin);
		addTalkId(Dolkin, Dolkin2, Karla);
		addQuestItem(Secret_Ingredients, Dolkin_Report);
		addKillId(Karaphon);
		addLevelCheck(NO_QUEST_DIALOG, 17/*, 25*/);
		addClassIdCheck(NO_QUEST_DIALOG, 182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;

		switch (event)
		{
			case "quest_ac":
				qs.setCond(1);
				htmltext = "33954-2.htm";
				break;
			
			case "enter_instance":
				if (qs.getCond() == 1)
				{
					htmltext = "33954-3.htm";
					enterInstance(qs, 253);
					return null;
				}
				break;
			
			case "quest_middle":
				qs.setCond(3);
				htmltext = "33954-5.htm";
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
			case Dolkin:
				switch (cond)
				{
					case 0:
						htmltext = "33954-1.htm";
						break;
					
					case 2:
						htmltext = "33954-4.htm";
						qs.takeItems(Secret_Ingredients, 1);
						qs.giveItems(Dolkin_Report, 1);
						break;
				}
				break;
			
			case Dolkin2:
				if (cond == 2)
				{
					htmltext = "34002-1.htm";
				}
				break;
			
			case Karla:
				if (cond == 3)
				{
					htmltext = "33933-1.htm";
					qs.takeItems(Dolkin_Report, 1);
					qs.addExpAndSp(EXP_REWARD, SP_REWARD);
					qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
					if (qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
					{
						qs.giveItems(Faeron_Support_Box_Warrior, 1);
					}
					else if (qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
					{
						qs.giveItems(Faeron_Support_Box_Mage, 1);
					}
					qs.finishQuest();
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() == 1)
		{
			qs.setCond(2);
			qs.giveItems(Secret_Ingredients, 1);
			qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_DOLKIN_AND_LEAVE_THE_KARAPHON_HABITAT, 4500, ScreenMessageAlign.TOP_CENTER));
			qs.getPlayer().getReflection().addSpawnWithoutRespawn(Dolkin2, new Location(-82100, 246311, -14152, 0), 0);
		}
		return null;
	}
}