package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;

/**
 * @author blacksmoke
 */
public class _10736_ASpecialPower extends Quest
{
	private static final int Katalin = 33943;
	private static final int Katalin_Instance = 33945;
	private static final int Floato = 27526;
	private static final int Ratel = 27527;

	private static final String FLOATO_2_KILL_VAR = "killed_2_floato";
	private static final String FLOATO_4_KILL_VAR = "killed_4_floato";
	private static final String FLOATO_6_KILL_VAR = "killed_6_ratel";
	
	private static final int EXP_REWARD = 3154;	private static final int SP_REWARD = 0; 	public _10736_ASpecialPower()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Katalin, Katalin_Instance);
		addTalkId(Katalin, Katalin_Instance);
		addKillId(Floato, Ratel);
		addSkillUseId(Ratel);
		addKillNpcWithLog(2, FLOATO_2_KILL_VAR, 2, Floato);
		addKillNpcWithLog(4, FLOATO_4_KILL_VAR, 2, Floato);
		addKillNpcWithLog(6, FLOATO_6_KILL_VAR, 2, Ratel);
		addLevelCheck(NO_QUEST_DIALOG, 4/*, 20*/);
		addClassIdCheck(NO_QUEST_DIALOG, 182);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10734);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		final Player player = qs.getPlayer();
		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				htmltext = "33943-2.htm";
				break;
			case "enter_instance":
				if(qs.getCond() == 1)
				{
					enterInstance(qs, 252);
					return null;
				}
				break;
			case "more_monsters":
				htmltext = "33945-3.htm";
				qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_MONSTER, 4500, ScreenMessageAlign.TOP_CENTER));
				break;
			case "skill_fight":
				qs.setCond(6);
				htmltext = "33945-7.htm";
				qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.FIGHT_USING_SKILLS, 4500, ScreenMessageAlign.TOP_CENTER));
				qs.getPlayer().getReflection().addSpawnWithoutRespawn(Ratel, new Location(-75112, 240760, -3615, 0), 0);
				qs.getPlayer().getReflection().addSpawnWithoutRespawn(Ratel, new Location(-75016, 240456, -3628, 0), 0);
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
			case Katalin:
				switch(cond)
				{
					case 0:
						htmltext = "33943-1.htm";
						break;
					case 1:
						htmltext = "33943-4.htm";
						break;
					case 7:
						htmltext = "33943-3.htm";
						qs.giveItems(1835, 500);
						qs.addExpAndSp(EXP_REWARD, SP_REWARD);
						qs.finishQuest();
						break;
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			case Katalin_Instance:
				switch(cond)
				{
					case 1:
						htmltext = "33945-1.htm";
						qs.setCond(2, false);
						qs.getPlayer().getReflection().addSpawnWithoutRespawn(Floato, new Location(-75112, 240760, -3615, 0), 0);
						qs.getPlayer().getReflection().addSpawnWithoutRespawn(Floato, new Location(-75016, 240456, -3628, 0), 0);
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_MONSTER, 4500, ScreenMessageAlign.TOP_CENTER));
						break;
					case 2:
						htmltext = "33945-2.htm";
						break;
					case 3:
						if(qs.getInt("ss_gived") == 0)
						{
							qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SPIRITSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ScreenMessageAlign.TOP_CENTER));
							qs.showTutorialClientHTML("QT_003_bullet_01");
							htmltext = "33945-4.htm";
							qs.giveItems(1835, 150);
							qs.set("ss_gived", 1);
						}
						else
						{
							htmltext = "33945-5.htm";
							qs.getPlayer().getReflection().addSpawnWithoutRespawn(Floato, new Location(-75112, 240760, -3615, 0), 0);
							qs.getPlayer().getReflection().addSpawnWithoutRespawn(Floato, new Location(-75016, 240456, -3628, 0), 0);
							qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_MONSTER, 4500, ScreenMessageAlign.TOP_CENTER));
							qs.setCond(4, false);
						}
						break;
					case 4:
						htmltext = "33945-2.htm";
						break;
					case 5:
						htmltext = "33945-6.htm";
						qs.showTutorialClientHTML("QT_004_skill_01");
						break;
					case 6:
						htmltext = "33945-2.htm";
						break;
					case 7:
						htmltext = "33945-8.htm";
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
	public String onSkillUse(NpcInstance npc, Skill skill, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case Ratel:
			{
				if(cond == 6 && (skill.getSkillType() == SkillType.PDAM || skill.getSkillType() == SkillType.MDAM))
					npc.reduceCurrentHp(npc.getMaxHp() / 2, st.getPlayer(), null, false, false, true, false, false, false, false);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		final int cond = qs.getCond();
		switch(npc.getNpcId())
		{
			case Floato:
				if((cond == 2) || (cond == 4))
				{
					if(updateKill(npc, qs))
					{
						qs.unset(cond == 2 ? FLOATO_2_KILL_VAR : FLOATO_4_KILL_VAR);
						qs.setCond(cond + 1);
					}
					else
						qs.playSound(SOUND_ITEMGET);
				}
				break;
			
			case Ratel:
				if(cond == 6)
				{
					if(updateKill(npc, qs))
					{
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_AYANTHE_TO_LEAVE_THE_TRAINING_GROUNDS, 4500, ScreenMessageAlign.TOP_CENTER));
						qs.unset(FLOATO_6_KILL_VAR);
						qs.setCond(cond + 1);
					}
					else
						qs.playSound(SOUND_ITEMGET);
				}
				break;
		}
		return null;
	}
}