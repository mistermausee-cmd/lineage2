package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.AbnormalList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class TheornInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	// Skill's
	private static final Skill BEGIN_RESEARCH_SKILL = SkillHolder.getInstance().getSkill(16135, 1);	// Начало Исследований
	private static final Skill REWARD_X_2_SKILL = SkillHolder.getInstance().getSkill(16136, 1);	// Награда - больше в 2 раза
	private static final Skill REWARD_X_4_SKILL = SkillHolder.getInstance().getSkill(16137, 1);	// Награда - больше в 4 раза
	private static final Skill REWARD_X_8_SKILL = SkillHolder.getInstance().getSkill(16138, 1);	// Награда - больше в 8 раз
	private static final Skill REWARD_X_16_SKILL = SkillHolder.getInstance().getSkill(16139, 1);	// Награда - больше в 16 раз
	private static final Skill REWARD_X_32_SKILL = SkillHolder.getInstance().getSkill(16140, 1);	// Награда - больше в 32 раза
	private static final Skill RESEARCH_SUCCESS_1_SKILL = SkillHolder.getInstance().getSkill(16141, 1);	// Успех Исследования
	private static final Skill RESEARCH_SUCCESS_2_SKILL = SkillHolder.getInstance().getSkill(16142, 1);	// Успех Исследования
	private static final Skill RESEARCH_SUCCESS_3_SKILL = SkillHolder.getInstance().getSkill(16143, 1);	// Успех Исследования
	private static final Skill RESEARCH_SUCCESS_4_SKILL = SkillHolder.getInstance().getSkill(16144, 1);	// Успех Исследования
	private static final Skill RESEARCH_SUCCESS_5_SKILL = SkillHolder.getInstance().getSkill(16145, 1);	// Успех Исследования
	private static final Skill RESEARCH_FAIL = SkillHolder.getInstance().getSkill(16146, 1);	// Провал Исследования

	// Cost's
	private static final long ADENA_COST = 100000L;
	private static final int SP_COST = 500000;

	public TheornInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
		{
			final AbnormalList effectList = player.getAbnormalList();
			if(effectList.contains(AbnormalType.research_fail) || effectList.contains(AbnormalType.research_success))
			{
				player.sendActionFailed();
				return;
			}

			if(effectList.contains(AbnormalType.research_reward))
			{
				if(effectList.contains(REWARD_X_32_SKILL))
				{
					showChatWindow(player, "default/" + getNpcId() + "-research_end.htm", firstTalk);
					return;
				}

				showChatWindow(player, "default/" + getNpcId() + "-research_continue.htm", firstTalk);
				return;
			}
		}
		super.showChatWindow(player, val, firstTalk, replace);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("roll"))
		{
			final AbnormalList effectList = player.getAbnormalList();
			if(effectList.contains(AbnormalType.research_fail) || effectList.contains(AbnormalType.research_success))
			{
				player.sendActionFailed();
				return;
			}

			if(effectList.contains(AbnormalType.research_reward))
			{
				if(effectList.contains(REWARD_X_32_SKILL))
				{
					player.sendActionFailed();
					return;
				}
			}
			else
			{
				if(player.getSp() < SP_COST || !player.reduceAdena(ADENA_COST, true))
				{
					showChatWindow(player, "default/" + getNpcId() + "-research_no_cost.htm", false);
					return;
				}

				player.sendPacket(new SystemMessagePacket(SystemMsg.YOUR_SP_HAS_DECREASED_BY_S1).addInteger(SP_COST));
				player.setSp(player.getSp() - SP_COST);
				player.forceUseSkill(BEGIN_RESEARCH_SKILL, player);
			}

			Skill skill;
			if(Rnd.chance(50))
			{
				if(effectList.contains(REWARD_X_16_SKILL))
					skill = RESEARCH_SUCCESS_5_SKILL;
				else if(effectList.contains(REWARD_X_8_SKILL))
					skill = RESEARCH_SUCCESS_4_SKILL;
				else if(effectList.contains(REWARD_X_4_SKILL))
					skill = RESEARCH_SUCCESS_3_SKILL;
				else if(effectList.contains(REWARD_X_2_SKILL))
					skill = RESEARCH_SUCCESS_2_SKILL;
				else
					skill = RESEARCH_SUCCESS_1_SKILL;
			}
			else
				skill = RESEARCH_FAIL;

			forceUseSkill(skill, player);
		}
		else if(command.startsWith("reward"))
		{
			final AbnormalList effectList = player.getAbnormalList();
			if(!effectList.contains(AbnormalType.research_reward))
			{
				showChatWindow(player, "default/" + getNpcId() + "-research_busy.htm", false);
				return;
			}

			int modifier = 1;
			if(effectList.stop(REWARD_X_32_SKILL) > 0)
			{
				broadcastPacket(new ExShowScreenMessage(NpcString.S1_ACQUIRED_32_TIMES_THE_SKILL_POINTS_AS_A_REWARD, 3000, ScreenMessageAlign.TOP_CENTER, false, true, player.getName()));
				modifier = 32;
			}
			else if(effectList.stop(REWARD_X_16_SKILL) > 0)
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_ACQUIRED_SP_X_16, 3000, ScreenMessageAlign.TOP_CENTER));
				modifier = 16;
			}
			else if(effectList.stop(REWARD_X_8_SKILL) > 0)
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_ACQUIRED_SP_X_8, 3000, ScreenMessageAlign.TOP_CENTER));
				modifier = 8;
			}
			else if(effectList.stop(REWARD_X_4_SKILL) > 0)
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_ACQUIRED_SP_X_4, 3000, ScreenMessageAlign.TOP_CENTER));
				modifier = 4;
			}
			else if(effectList.stop(REWARD_X_2_SKILL) > 0)
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_ACQUIRED_SP_X_2, 3000, ScreenMessageAlign.TOP_CENTER));
				modifier = 2;
			}
			else
			{
				player.sendActionFailed();
				return;
			}

			player.setSp(player.getSp() + (SP_COST * modifier));
		}
		else
			super.onBypassFeedback(player, command);
	}
}