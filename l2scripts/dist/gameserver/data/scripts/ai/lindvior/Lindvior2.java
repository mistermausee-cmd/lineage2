package ai.lindvior;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author iqman
 * @reworked by Bonux
**/
public class Lindvior2 extends LindviorAI
{
	public Lindvior2(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		int progress = getRaidProgress();
		if(progress == 2)
			getActor().setCurrentHp(getActor().getMaxHp() * 0.8, false);
		else if(progress == 4)
			getActor().setCurrentHp(getActor().getMaxHp() * 0.4, false);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(actor.getCurrentHpPercents() <= 20.0D)
		{
			if(setRaidProgress(5))
				return;
		}
		else if(actor.getCurrentHpPercents() <= 60.0D)
		{
			if(setRaidProgress(3))
				return;
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}