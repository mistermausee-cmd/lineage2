package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

public class VitalityHeal extends Skill
{
	public VitalityHeal(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isPlayer())
			return;

		final Player player = target.getPlayer();
		final int points = (int) (Player.MAX_VITALITY_POINTS / 100 * getPower());
		player.setVitality(player.getVitality() + points, true);
	}
}