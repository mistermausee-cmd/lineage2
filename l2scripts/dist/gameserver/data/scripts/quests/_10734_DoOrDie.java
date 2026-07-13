package quests;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.components.NpcString;

/**
 * @author blacksmoke
 */
public class _10734_DoOrDie extends Quest
{
	private static final int Katalin = 33943;
	private static final int Ayanthe = 33942;
	private static final int Adventurers_Guide = 33950;
	private static final int Training_Dummy = 19546;

	private static final int EXP_REWARD = 805;
	private static final int SP_REWARD = 2;
	
	private final static int[] _warriorBuff =
	{
		15642, // Horn Melody (Adventurer)
		15643, // Drum Melody (Adventurer)
		15644, // Pipe Organ Melody (Adventurer)
		15645, // Guitar Melody (Adventurer)
		15646, // Harp Melody (Adventurer)
		15647, // Lute Melody (Adventurer)
		15651, // Prevailing Sonata (Adventurer)
		15652, // Daring Sonata (Adventurer)
		15653, // Refreshing Sonata (Adventurer)
		15649, // Warrior's Harmony (Adventurer)
		5182, // Blessing of Protection
	};

	private final static int[] _wizardBuff =
	{
		15642, // Horn Melody (Adventurer)
		15643, // Drum Melody (Adventurer)
		15644, // Pipe Organ Melody (Adventurer)
		15645, // Guitar Melody (Adventurer)
		15646, // Harp Melody (Adventurer)
		15647, // Lute Melody (Adventurer)
		15651, // Prevailing Sonata (Adventurer)
		15652, // Daring Sonata (Adventurer)
		15653, // Refreshing Sonata (Adventurer)
		15650, // Wizard's Harmony (Adventurer)
		5182, // Blessing of Protection
		
	};

	public _10734_DoOrDie()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Katalin, Ayanthe);
		addTalkId(Katalin, Ayanthe, Adventurers_Guide);
		addKillId(Training_Dummy);
		addLevelCheck(NO_QUEST_DIALOG, 1/*, 20*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10733);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		final Player player = qs.getPlayer();
		List<Creature> target = new ArrayList<>();
		target.add(player);
		
		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				if(qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
				{
					htmltext = "33943-3.htm";
				}
				if(qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
				{
					htmltext = "33942-3.htm";
				}
				qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 4500, ScreenMessageAlign.TOP_CENTER));
				break;
			
			case "buffs_info":
				qs.showTutorialClientHTML("QT_002_Guide_01");
				if(qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
				{
					htmltext = "33950-3.htm";
				}
				else if(qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
				{
					htmltext = "33950-5.htm";
				}
				break;
			
			case "buff":
				doSupportMagic(npc, player);
				qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 4500, ScreenMessageAlign.TOP_CENTER));
				qs.setCond(6);
				if(qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
				{
					htmltext = "33950-4.htm";
				}
				else if(qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
				{
					htmltext = "33950-6.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		final int cond = qs.getCond();
		boolean e_warrior = qs.getPlayer().getClassId().getId() == 182;
		boolean e_wizard = qs.getPlayer().getClassId().getId() == 183;
		
		switch(npc.getNpcId())
		{
			case Katalin:
				switch(cond)
				{
					case 0:
						if(e_warrior)
						{
							htmltext = "33943-1.htm";
						}
						else if(e_wizard)
						{
							htmltext = "33943-8.htm";
						}
						break;
					
					case 1:
						if(e_warrior)
						{
							htmltext = "33943-4.htm";
						}
						break;
					
					case 3:
						htmltext = "33943-5.htm";
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_APPRENTICE_ADVENTURERS_GUIDE, 4500, ScreenMessageAlign.TOP_CENTER));
						qs.setCond(5);
						break;
					
					case 5:
						htmltext = "33943-6.htm";
						break;
					
					case 8:
						qs.addExpAndSp(EXP_REWARD, SP_REWARD);
						qs.finishQuest();
						htmltext = "33943-7.htm";
						break;
					
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case Ayanthe:
				switch(cond)
				{
					case 0:
						if(e_warrior)
						{
							htmltext = "33942-8.htm";
						}
						else if(e_wizard)
						{
							htmltext = "33942-1.htm";
						}
						break;
					
					case 1:
						if(e_wizard)
						{
							htmltext = "33942-4.htm";
						}
						break;
					
					case 2:
						htmltext = "33942-5.htm";
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_APPRENTICE_ADVENTURERS_GUIDE, 4500, ScreenMessageAlign.TOP_CENTER));
						qs.setCond(4);
						break;
					
					case 4:
						htmltext = "33942-6.htm";
						break;
					
					case 7:
						qs.addExpAndSp(EXP_REWARD, SP_REWARD);
						qs.finishQuest();
						htmltext = "33942-7.htm";
						break;
					
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case Adventurers_Guide:
				switch(cond)
				{
					case 0:
						htmltext = "33950-nc.htm";
						break;
					
					case 4:
					case 5:
						htmltext = "33950-1.htm";
						break;
					
					case 6:
						htmltext = "33950-4.htm";
						doSupportMagic(npc, qs.getPlayer());
						break;
					
					case 7:
						htmltext = "33950-7.htm";
						doSupportMagic(npc, qs.getPlayer());
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
		switch(qs.getCond())
		{
			case 1:
				if(qs.getPlayer().getClassId().getId() == 182)
				{
					qs.setCond(3);
				}
				else if(qs.getPlayer().getClassId().getId() == 183)
				{
					qs.setCond(2);
				}
				break;
			
			case 6:
				if(qs.getPlayer().getClassId().getId() == 182)
				{
					qs.setCond(8);
				}
				else if(qs.getPlayer().getClassId().getId() == 183)
				{
					qs.setCond(7);
				}
				break;
		}
		
		return null;
	}
	
	private void doSupportMagic(NpcInstance npc, Player player)
	{
		List<Creature> target = new ArrayList<>();
		target.add(player);
		if(player.getClassId().getId() == 182) // Ertheia Fighter
		{
			for (int buff : _warriorBuff)
			{
				npc.broadcastPacket(new MagicSkillUse(npc, player, buff, 1, 0, 0));
				npc.callSkill(SkillHolder.getInstance().getSkill(buff, 1), target, true, false);
			}
		}
		else if(player.getClassId().getId() == 183) // Ertheia Wizard
		{
			for (int buff : _wizardBuff)
			{
				npc.broadcastPacket(new MagicSkillUse(npc, player, buff, 1, 0, 0));
				npc.callSkill(SkillHolder.getInstance().getSkill(buff, 1), target, true, false);
			}
		}
	}
	
	// Need to use this (quest need to be available only at one npc for warrior/wizard)
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		final int classId = player.getClassId().getId();
		
		switch(npc.getNpcId())
		{
			case Katalin:
				if(classId == 182)
				{
					return true;
				}
				return false;
				
			case Ayanthe:
				if(classId == 183)
				{
					return true;
				}
				return false;
		}
		
		return true;
	}
	
	public boolean checkTalkNpc(NpcInstance npc, QuestState st)
	{
		return checkStartNpc(npc, st.getPlayer());
	}
}
