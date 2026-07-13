package ai;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;


public class KartiaMobs extends Fighter
{
	public KartiaMobs(NpcInstance actor)
	{
		super(actor);
	}


	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		return false;
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}

}
