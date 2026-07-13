package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

public class DeathPenalty extends Skill
{
	public DeathPenalty(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		
		if(activeChar.isPK())
		{
			activeChar.sendActionFailed();
			return false;
		}

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isPlayer())
			return;

		target.getPlayer().getDeathPenalty().reduceLevel();
	}
}