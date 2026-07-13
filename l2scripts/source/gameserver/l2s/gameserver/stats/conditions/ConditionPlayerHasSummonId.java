package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.stats.Env;


public class ConditionPlayerHasSummonId extends Condition
{
	private int _id;

	public ConditionPlayerHasSummonId(int id)
	{
		_id = id;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.target == null || !env.target.isPlayer())
			return false;

		Player player = (Player) env.target;
		for(SummonInstance summon : player.getSummons())
		{
			if(summon.getNpcId() == _id)
		        return true;
		}
		return false;
	}
}