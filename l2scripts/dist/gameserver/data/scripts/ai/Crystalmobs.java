package ai;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

//By Evil_dnk

public class Crystalmobs extends Fighter
{
	public Crystalmobs(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		NpcInstance actor = getActor();
		if(target.isPlayable() && !target.isDead())
		{
			actor.getAggroList().addDamageHate(target, 0, 1);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		if(target instanceof NpcInstance)
		{
			if (target.getNpcId() == 19013 || target.getNpcId() == 19014)
			{
				actor.getAggroList().addDamageHate(target, 0, 1);
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
		}
		return true;
	}
}
