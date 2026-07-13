package npc.model.octavis;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

import instances.Octavis;

/**
 * @author Bonux
**/
public final class OctavisSecondInstance extends RaidBossInstance
{
	private static final long serialVersionUID = 1L;

	public OctavisSecondInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isDeathImmune()
	{
		return true;
	}

	@Override
	public void onChangeCurrentHp(double oldHp, double newHp)
	{
		super.onChangeCurrentHp(oldHp, newHp);

		if(getCurrentHp() < 5)
		{
			deleteMe();

			Reflection reflection = getReflection();
			if(reflection instanceof Octavis)
				((Octavis) reflection).nextState();
		}
	}
}