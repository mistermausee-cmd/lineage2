package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.attachment.FlagItemAttachment;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.SkillEntry;

public class RequestMagicSkillUse extends L2GameClientPacket
{
	private Integer _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	
	@Override
	protected void readImpl()
	{
		_magicId = readD();
		_ctrlPressed = readD() != 0;
		_shiftPressed = readC() != 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.setActive();

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}
		SkillEntry skillEntry = null;
		if(isComboSkill(this._magicId.intValue()))
			skillEntry = SkillHolder.getInstance().getSkillEntry(_magicId.intValue(), 1);
		else
			skillEntry = SkillHolder.getInstance().getSkillEntry(_magicId.intValue(), activeChar.getSkillLevel(_magicId));

		if(skillEntry != null)
		{
			skillEntry = skillEntry.getTemplate().getElementalSkill(skillEntry, activeChar);
			Skill skill = skillEntry.getTemplate();
			if(!skill.isActive() && !skill.isToggle())
			{
				activeChar.sendActionFailed();
				return;
			}

			FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
			if(attachment != null && !attachment.canCast(activeChar, skill))
			{
				activeChar.sendActionFailed();
				return;
			}

			
			if(activeChar.isTransformed() && !activeChar.getAllSkills().contains(skillEntry) && skill.getSkillType() != Skill.SkillType.CHANGE_CLASS)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(skill.isToggle())
			{
				if(activeChar.getAbnormalList().contains(skill))
				{
					if(!skill.isNecessaryToggle())
					{
						if(activeChar.isSitting())
						{
							activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
							return;
						}
						activeChar.getAbnormalList().stop(skill.getId());
						activeChar.sendActionFailed();
					}
					activeChar.sendActionFailed();
					return;
				}
			}

			Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

			activeChar.setGroundSkillLoc(null);
			activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
		}
		else
			activeChar.sendActionFailed();
	}

	private static final boolean isComboSkill(int magicId)
	{
		switch(magicId)
		{
			case 10500:
			case 10499:
			case 10749:
			case 10750:
			case 10250:
			case 10249:
			case 11000:
			case 10999:
			case 11249:
			case 11250:
			case 11500:
			case 11499:
			case 11750:
			case 11749:
			case 12000:
			case 11999:	
			case 15606:
				return true;
		}
		return false;
	}
}