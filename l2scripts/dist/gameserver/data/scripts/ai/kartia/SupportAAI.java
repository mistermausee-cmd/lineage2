package ai.kartia;

import java.util.List;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.geodata.GeoEngine;

//By Evil_dnk
public class SupportAAI extends Fighter
{
	private NpcInstance _target = null;

	public SupportAAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		startAttack();
	}

	@Override
	protected boolean thinkActive()
	{
		return startAttack();
	}

	private boolean startAttack()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return false;

		if(_target == null)
		{
			List<NpcInstance> around = actor.getAroundNpc(2000, 150);
			for(NpcInstance npc : around)
			{
				if(checkTarget(npc))
				{
					if(_target == null || actor.getDistance3D(npc) < actor.getDistance3D(_target))
						_target = npc;
				}
			}
		}

		if(_target == null)
			return false;

		if(!actor.isAttackingNow() && !actor.isCastingNow())
		{
			if(!_target.isDead() && GeoEngine.canSeeTarget(actor, _target, false))
			{
				actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _target, 1);
				return true;
			}
		}

		_target = null;
		
		return false;
	}

	private boolean checkTarget(NpcInstance target)
	{
		if(target.isPeaceNpc() || target.isBusy() || target.isInvulnerable() || target instanceof GuardInstance)
			return false;
		return true;
	}
}
