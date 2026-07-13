package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

/**
 Obi-Wan
 27.10.2016
 */
public class BalokJailerAI extends Fighter
{
	public BalokJailerAI(NpcInstance actor)
	{
		super(actor);
		actor.setRunning();
	}

	@Override public void runImpl()
	{
		if(getActor().isDead())
		{
			return;
		}

		if(getActor().isCastingNow() || getActor().isAttackingNow())
		{
			return;
		}

		if(_def_think)
		{
			if(doTask())
			{
				clearTasks();
			}
			return;
		}

		for(Creature target : getActor().getAroundCharacters(3000, 500))
		{
			if(target.getNpcId() != 29218)
			{
				continue;
			}
			if(getActor().getDistance3D(target) > 350)
			{
				addTaskMove(Location.findPointToStay(target.getLoc(), 300, 300), true);
			}
		}
	}

	@Override
	public boolean canAttackCharacter(Creature target)
	{
		return target.getNpcId() == 29218;
	}
}