package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;

/**
 * @author blacksmoke
 */
public class _10740_NeverForget extends Quest
{
	private static final int Sivanthe = 33951;
	private static final int RemembranceTower = 33989;
	
	private static final int UnnamedRelics = 39526;
	
	private static final int KeenFloato = 23449;
	private static final int Ratel = 23450;
	private static final int RobustRatel = 23451;
	private int Relics;
	
	private static final int EXP_REWARD = 24001;	private static final int SP_REWARD = 0; 	public _10740_NeverForget()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Sivanthe);
		addTalkId(Sivanthe, RemembranceTower);
		addQuestItem(UnnamedRelics);
		addKillId(KeenFloato, Ratel, RobustRatel);
		addLevelCheck(NO_QUEST_DIALOG, 8/*, 20*/);
		addClassIdCheck(NO_QUEST_DIALOG, 182, 183);
		// addQuestCompletedCheck(NO_QUEST_DIALOG, 10739);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				htmltext = "33951-3.htm";
				break;
			
			case "quest_cont":
				qs.takeItems(UnnamedRelics, 20);
				qs.setCond(3);
				htmltext = "33989-2.htm";
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
					case 2:
						htmltext = "33951-5.htm";
						break;
					case 3:
						htmltext = "33951-6.htm";
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
						qs.giveItems(875, 2); // Ring of Knowledge
						qs.giveItems(1060, 100); // 100x Healing Potion
						qs.addExpAndSp(EXP_REWARD, SP_REWARD);
						qs.finishQuest();
						break;
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case RemembranceTower:
				switch(cond)
				{
					case 1:
						htmltext = "FIND HTML";
						break;
					case 2:
						htmltext = "33989-1.htm";
						break;
					case 3:
						htmltext = "33989-3.htm";
						break;
					default:
						htmltext = "noqu.htm";
						break;
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
			qs.giveItems(UnnamedRelics, 1);
			qs.playSound(SOUND_ITEMGET);
			Relics++;
			if(Relics >= 20)
			{
				qs.setCond(2);
				Relics = 0;
			}
		}
		return null;
	}
}
