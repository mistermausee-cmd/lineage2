package ai.lindvior;

import instances.LindviorBoss;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
**/
public abstract class LindviorAI extends Fighter
{
	public LindviorAI(NpcInstance actor)
	{
		super(actor);
	}

	protected int getRaidProgress()
	{
		Reflection r = getActor().getReflection();
		if(r != null)
		{
			if(r instanceof LindviorBoss)
				return ((LindviorBoss) r).getRaidProgress();
		}
		return 0;
	}

	protected boolean setRaidProgress(int value)
	{
		Reflection r = getActor().getReflection();
		if(r != null)
		{
			if(r instanceof LindviorBoss)
				return ((LindviorBoss) r).setRaidProgress(value, getActor());
		}
		return false;
	}
}