package ai.kartia;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;

import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

//By Evil_dnk

public class SupportFAI extends Fighter
{
	private boolean _thinking = false;
	private ScheduledFuture<?> _followTask;

	public SupportFAI(NpcInstance actor)
	{
		super(actor);
		_activeAITaskDelay = 250;
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

		//Уже следуем за этой целью
		if(actor.isFollow && actor.getFollowTarget() == target)
		{
			clientActionFailed();
			return;
		}

		//Находимся достаточно близко
		if(actor.isInRange(target, Config.FOLLOW_RANGE + 200))
			clientActionFailed();

		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 2500L);
	}

	protected class ThinkFollow extends RunnableImpl
	{
		public NpcInstance getActor()
		{
			return SupportFAI.this.getActor();
		}

		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if(actor == null)
				return;

			Creature target = actor.getFollowTarget();

			if(target == null || target.isAlikeDead() || actor.getReflection() != target.getReflection())
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			if(actor.getDistance(target) > 1000)
			{
				actor.teleToLocation(target.getLoc());
			}
			else if(!actor.isInRange(target, Config.FOLLOW_RANGE + 200) && (!actor.isFollow || actor.getFollowTarget() != target))
			{
				double angle = PositionUtils.convertHeadingToDegree(target.getLoc().h);
				double radians = Math.toRadians(angle);
				double radius = 150.0D;
				double course = 200.0D;

				int x = (int)(Math.cos(Math.PI + radians + course) * radius);
				int y = (int)(Math.sin(Math.PI + radians + course) * radius);

				actor.setRunning();
				Location loc = target.getLoc();
				loc.setX(loc.getX() + x + Rnd.get(-150, 150));
				loc.setY(loc.getY() + y + Rnd.get(-150, 150));
				actor.followToCharacter(loc, target, Config.FOLLOW_RANGE, false);
			}
			_followTask = ThreadPoolManager.getInstance().schedule(this, 2500L);
		}
	}
}