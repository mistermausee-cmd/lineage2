package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.utils.Location;


public class RequestExMagicSkillUseGround extends L2GameClientPacket
{
	private Location _loc = new Location();
	private int _skillId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	
	@Override
	protected void readImpl()
	{
		_loc.x = readD();
		_loc.y = readD();
		_loc.z = readD();
		_skillId = readD();
		_ctrlPressed = readD() != 0;
		_shiftPressed = readC() != 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		Skill skill = SkillHolder.getInstance().getSkill(_skillId, activeChar.getSkillLevel(_skillId));
		if(skill != null)
		{
			
			if(activeChar.isTransformed() && !activeChar.getAllSkills().contains(skill.getEntry()))
				return;

			Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

			activeChar.setGroundSkillLoc(_loc);
			activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
		}
		else
			activeChar.sendActionFailed();
	}
}