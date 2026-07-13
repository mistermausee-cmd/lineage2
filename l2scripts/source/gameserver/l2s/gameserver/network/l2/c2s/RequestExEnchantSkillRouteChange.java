package l2s.gameserver.network.l2.c2s;

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

public final class RequestExEnchantSkillRouteChange extends L2GameClientPacket
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

		int oldLevel = skillEntry.getLevel();

		skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, _skillLvl);
		if(skillEntry == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!SkillUtils.isAvailableSkillEnchant(activeChar, skillEntry.getTemplate(), Skill.EnchantType.CHANGE))
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
		final int requiredItemId = info.getChangeEnchantItemId();

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
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
				return;
			}
		}

		ItemFunctions.deleteItem(activeChar, 57, requiredAdena);
		activeChar.addExpAndSp(0, -1 * requiredSp);

		activeChar.addSkill(skillEntry, true);

		SystemMessage sm = new SystemMessage(SystemMessage.Enchant_skill_route_change_was_successful_Lv_of_enchant_skill_S1_will_remain);
		sm.addSkillName(skillEntry.getId(), skillEntry.getLevel());
		activeChar.sendPacket(sm);

		Log.add(activeChar.getName() + "|Successfully changed route|" + skillEntry.getId() + "|" + oldLevel + "|to+" + skillEntry.getLevel(), "enchant_skills");

		activeChar.sendPacket(new ExEnchantSkillInfoPacket(skillEntry.getId(), skillEntry.getLevel()), new ExEnchantSkillResult(1));
		activeChar.sendSkillList();
		activeChar.updateSkillShortcuts(skillEntry.getId(), skillEntry.getLevel());
	}
}