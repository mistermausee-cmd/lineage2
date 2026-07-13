package ai;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
 */
public class TrainingFighter extends Fighter
{
	private static final String SCARECROW_NPC_ID_VAR = "scarecrow_npc_id";
	private static final String SCARECROW_ID_VAR = "scarecrow_id";

	private final int _scarecrowNpcId;
	private final int _scarecrowId;

	public TrainingFighter(NpcInstance actor)
	{
		super(actor);

		_scarecrowNpcId = actor.getParameter(SCARECROW_NPC_ID_VAR, 0);
		_scarecrowId = actor.getParameter(SCARECROW_ID_VAR, 0);
	}

	private boolean checkScarecrow(Creature target)
	{
		if(_scarecrowNpcId > 0)
		{
			if(target.isNpc())
			{
				NpcInstance npc = (NpcInstance) target;
				if(npc.getNpcId() == _scarecrowNpcId && npc.getParameter(SCARECROW_ID_VAR, 0) == _scarecrowId)
					return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isAggressive()
	{
		return _scarecrowNpcId > 0 || super.isAggressive();
	}

	@Override
	protected boolean canAttackCharacter(Creature target)
	{
		return checkScarecrow(target) || super.canAttackCharacter(target);
	}

	@Override
	protected boolean checkAggression(Creature target)
	{
		return checkScarecrow(target) || super.checkAggression(target);
	}

	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		if(damage > 0 && getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
		{
			if(_scarecrowNpcId > 0 && getAttackTarget() != null && getAttackTarget().getNpcId() == _scarecrowNpcId)
			{
				getActor().abortAttack(true, false);
				notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10);
				return;
			}
		}
		super.onEvtClanAttacked(attacked, attacker, damage);
	}
}