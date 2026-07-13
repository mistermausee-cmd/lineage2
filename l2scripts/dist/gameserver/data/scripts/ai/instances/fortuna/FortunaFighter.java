package ai.instances.fortuna;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
**/
public class FortunaFighter extends Fighter
{
	public FortunaFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean isGlobalAggro()
	{
		return true;
	}
}
