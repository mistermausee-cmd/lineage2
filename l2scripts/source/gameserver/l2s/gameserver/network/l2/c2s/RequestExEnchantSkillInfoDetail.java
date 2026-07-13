package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SkillEnchantInfoHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill.EnchantType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoDetailPacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.SkillEnchantInfo;
import l2s.gameserver.utils.SkillUtils;
public final class RequestExEnchantSkillInfoDetail extends L2GameClientPacket
{
	private EnchantType _type;
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_type = EnchantType.VALUES[readD()];
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if(activeChar.isTransformed() || activeChar.isMounted() || activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING_FUNCTION_UNDER_OFFBATTLE_STATUS_AND_CANNOT_USE_THE_FUNCTION_WHILE_TRANSFORMING_BATTLING_AND_ONBOARD);
			return;
		}

		if(activeChar.getLevel() < 85)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL_YOU_CAN_USE_THE_CORRESPONDING_FUNCTION_ON_LEVELS_HIGHER_THAN_76LV_);
			return;
		}

		if(!activeChar.getClassId().isAwaked())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_CORRESPONDING_FUNCTION_WHEN_COMPLETING_THE_THIRD_CLASS_CHANGE);
			return;
		}

		SkillEntry skillEntry = activeChar.getKnownSkill(_skillId);
		if(skillEntry != null && activeChar.isSkillDisabled(skillEntry.getTemplate()))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING_FUNCTION_UNDER_OFFBATTLE_STATUS_AND_CANNOT_USE_THE_FUNCTION_WHILE_TRANSFORMING_BATTLING_AND_ONBOARD);
			return;
		}

		if(!SkillUtils.isEnchantedSkill(_skillLvl))
		{
			skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, 1);
			if(skillEntry == null)
			{
				activeChar.sendActionFailed();
				return;
			}
			skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, skillEntry.getTemplate().getMaxLevel());
		}
		else
			skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, _skillLvl);

		if(skillEntry == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!SkillUtils.isAvailableSkillEnchant(activeChar, skillEntry.getTemplate(), _type))
		{
			activeChar.sendActionFailed();
			return;
		}

		final int enchantLevel = SkillUtils.getSkillEnchantLevel(skillEntry.getLevel());
		if(enchantLevel <= 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		final SkillEnchantInfo info = SkillEnchantInfoHolder.getInstance().getInfo(enchantLevel);
		if(info == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		long requiredSp = info.getSp();
		long requiredAdena = info.getAdena();
		final int rate = info.getSuccesRate();
		int requiredItemId = 0;

		switch(_type)
		{
			case NORMAL:
			{
				requiredItemId = info.getNormalEnchantItemId();
				break;
			}
			case BLESSED:
			{
				requiredItemId = info.getBlessedEnchantItemId();
				break;
			}
			case CHANGE:
			{
				requiredItemId = info.getChangeEnchantItemId();
				break;
			}
			case IMMORTAL:
			{
				requiredSp = 0;
				requiredAdena = 0;
				requiredItemId = info.getSafeEnchantItemId();
				break;
			}
		}

		
		activeChar.sendPacket(new ExEnchantSkillInfoDetailPacket(skillEntry.getId(), _skillLvl, requiredSp, rate, requiredItemId, requiredItemId > 0 ? 1 : 0, (int) requiredAdena));
	}
}