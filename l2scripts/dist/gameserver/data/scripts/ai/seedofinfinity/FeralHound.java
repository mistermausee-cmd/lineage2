package ai.seedofinfinity;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */

public class FeralHound extends Fighter
{
	public FeralHound(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getInvulnerable().start();
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}