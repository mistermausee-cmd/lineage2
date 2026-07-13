package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

public class Kama56Minion extends Fighter
{
	public Kama56Minion(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getInvulnerable().start();
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		if(aggro < 10000000)
			return;
		super.onEvtAggression(attacker, aggro);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{}
}