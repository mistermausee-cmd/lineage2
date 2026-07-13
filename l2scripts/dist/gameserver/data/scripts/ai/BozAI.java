package ai;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Rivelia
 */
public class BozAI extends DefaultAI
{
	public BozAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
	
	@Override
	protected boolean createNewTask()
	{
		return false;
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
