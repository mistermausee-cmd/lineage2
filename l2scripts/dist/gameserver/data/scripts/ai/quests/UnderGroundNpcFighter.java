package ai.quests;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Iqman
 */
public class UnderGroundNpcFighter extends Fighter
{
	public UnderGroundNpcFighter(NpcInstance actor)
	{
		super(actor);
		actor.setAggroRange(300);
		actor.setRandomWalk(false);
	}

	@Override
	protected boolean canAttackCharacter(Creature target)
	{
		if(target.isNpc())
			if(((NpcInstance) target).getNpcId() == 23113 || ((NpcInstance) target).getNpcId() == 19141)
				return true;
		return false;
	}
}