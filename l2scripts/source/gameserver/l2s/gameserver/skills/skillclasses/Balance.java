package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

public class Balance extends Skill
{
	public Balance(StatsSet set)
	{
		super(set);
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		double summaryCurrentHp = 0;
		int summaryMaximumHp = 0;

		for(Creature target : targets)
		{
			if(target == null)
				continue;

			if(target.isAlikeDead())
				continue;

			summaryCurrentHp += target.getCurrentHp();
			summaryMaximumHp += target.getMaxHp();
		}

		final double percent = summaryCurrentHp / summaryMaximumHp;

		for(Creature target : targets)
		{
			if(target == null)
				continue;

			if(target.isAlikeDead())
				continue;

			double hp = target.getMaxHp() * percent;
			if(hp > target.getCurrentHp())
			{
				
				double limit = target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100.;
				if(target.getCurrentHp() < limit) 
					target.setCurrentHp(Math.min(hp, limit), false);
			}
			else
				
				target.setCurrentHp(Math.max(1.01, hp), false);
		}
	}
}