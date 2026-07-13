package ai.incubatorOfEvil;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;

import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.utils.Location;

public class VanHolterFAI extends Fighter
{
	private boolean _thinking = false;
	private ScheduledFuture<?> _followTask;

	public VanHolterFAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomWalk()
	{
		if(getActor() instanceof MonsterInstance)
			return true;

		return false;
	}

	@Override
	protected void onEvtThink()
	{
		NpcInstance actor = getActor();
		if(_thinking || actor.isActionsDisabled() || actor.isAfraid() || actor.isDead() || actor.isMovementDisabled())
			return;

		_thinking = true;
		try
		{
			if(!Config.BLOCK_ACTIVE_TASKS && _followTask != null)
				thinkActive();
			else if(_followTask == null)
				thinkFollow();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			_thinking = false;
		}
	}

	protected void thinkFollow()
	{
		NpcInstance actor = getActor();

		Creature target = actor.getFollowTarget();

		//Находимся слишком далеко цели, либо цель не пригодна для следования, либо не можем перемещаться
		if(target == null || target.isAlikeDead() || actor.isMovementDisabled() || target.getReflection() != actor.getReflection())
		{
			clientActionFailed();
			return;
		}
		  //перемещаем к тому за кем следует если цель убежала слишком далеко
		if(actor.getDistance(target) > 2000 && target.getReflection().getId() != 0)
			actor.teleToLocation(target.getLoc());

		//Уже следуем за этой целью
		if(actor.isFollow && actor.getFollowTarget() == target)
		{
			clientActionFailed();
			return;
		}

		//Находимся достаточно близко
		if(actor.isInRange(target, Config.FOLLOW_RANGE + 20))
			clientActionFailed();

		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);
	}

	protected class ThinkFollow extends RunnableImpl
	{
		public NpcInstance getActor()
		{
			return VanHolterFAI.this.getActor();
		}

		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if(actor == null)
				return;

			Creature target = actor.getFollowTarget();

			if(target == null || target.isAlikeDead())
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			if(actor.getDistance(target) > 2000 && target.getReflection().getId() != 0)
				actor.teleToLocation(target.getLoc());

			if(!actor.isInRange(target, Config.FOLLOW_RANGE + 20) && (!actor.isFollow || actor.getFollowTarget() != target))
			{
				Location loc = new Location(target.getX() + Rnd.get(-60, 60), target.getY() + Rnd.get(-60, 60), target.getZ());
				actor.followToCharacter(loc, target, Config.FOLLOW_RANGE, false);
			}
			_followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
		}
	}
}