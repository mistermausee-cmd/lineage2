package ai.lindvior;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author iqman
 * @reworked by Bonux
**/
public class Lindvior1 extends LindviorAI
{
	public Lindvior1(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(actor.getCurrentHpPercents() <= 80.0D)
		{
			if(setRaidProgress(2))
				return;
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}