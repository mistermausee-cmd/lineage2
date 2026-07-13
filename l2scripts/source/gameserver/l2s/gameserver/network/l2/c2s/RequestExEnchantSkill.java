package l2s.gameserver.network.l2.c2s;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillEnchantInfoHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillResult;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.SkillEnchantInfo;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.SkillUtils;


public class RequestExEnchantSkill extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
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

		skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, _skillLvl);
		if(skillEntry == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!SkillUtils.isAvailableSkillEnchant(activeChar, skillEntry.getTemplate(), Skill.EnchantType.NORMAL))
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

		final long requiredSp = info.getSp();
		final long requiredAdena = info.getAdena();
		final int rate = info.getSuccesRate();
		final int requiredItemId = info.getNormalEnchantItemId();

		if(activeChar.getSp() < requiredSp)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}

		if(activeChar.getAdena() < requiredAdena)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(requiredItemId > 0)
		{
			if(!ItemFunctions.deleteItem(activeChar, requiredItemId, 1))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT));
				return;
			}
		}

		if(Rnd.chance(rate))
		{
			activeChar.addExpAndSp(0, -1 * requiredSp);
			ItemFunctions.deleteItem(activeChar, 57, requiredAdena);
			activeChar.sendPacket(new SystemMessage(SystemMessage.SP_HAS_DECREASED_BY_S1).addNumber(requiredSp), new SystemMessage(SystemMessage.SUCCEEDED_IN_ENCHANTING_SKILL_S1).addSkillName(skillEntry.getId(), skillEntry.getLevel()), new ExEnchantSkillResult(1));
			Log.add(activeChar.getName() + "|Successfully enchanted|" + skillEntry.getId() + "|to+" + enchantLevel + "|" + rate, "enchant_skills");
		}
		else
		{
			int safeEnchantLevel = SkillUtils.getSafeEnchantLevel(enchantLevel);
			int safeSubLevel = SkillUtils.getSubSkillLevel(SkillUtils.getSkillEnchantType(skillEntry.getLevel()), safeEnchantLevel);
			int safeSkillLevel = SkillUtils.getSkillLevelMask(SkillUtils.getSkillLevelFromMask(skillEntry.getLevel()), safeSubLevel);
			skillEntry = SkillHolder.getInstance().getSkillEntry(this._skillId, safeSkillLevel);
			if(skillEntry == null)
			{
		        activeChar.sendActionFailed();
		        return;
		    }
			activeChar.sendPacket(new SystemMessage(SystemMessage.FAILED_IN_ENCHANTING_SKILL_S1).addSkillName(skillEntry.getId(), skillEntry.getLevel()), new ExEnchantSkillResult(0));
			Log.add(activeChar.getName() + "|Failed to enchant|" + skillEntry.getId() + "|to+" + enchantLevel + "|" + rate, "enchant_skills");
		}

		activeChar.addSkill(skillEntry, true);
		activeChar.sendSkillList();
		activeChar.updateSkillShortcuts(skillEntry.getId(), skillEntry.getLevel());
		activeChar.sendPacket(new ExEnchantSkillInfoPacket(skillEntry.getId(), skillEntry.getLevel()));
	}
}