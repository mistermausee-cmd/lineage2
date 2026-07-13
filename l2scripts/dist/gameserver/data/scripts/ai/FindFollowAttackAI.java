package ai;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ai.kartia.SupportAAI;
import ai.kartia.SupportFAI;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

//By Evil_dnk
public class FindFollowAttackAI extends Fighter
{
	private NpcInstance _target = null;
	private boolean attack = false;

	private static final NpcString[] SAY_TEXT = new NpcString[] {
			NpcString.GO_ADVANCE,
			NpcString.ALL_ATTACK,
			NpcString.FORWARD,
			NpcString.FOR_THE_FREEDOM,
			};


	public FindFollowAttackAI(NpcInstance actor)
	{
		super(actor);
		_activeAITaskDelay = 250;
		_attackAITaskDelay = 250;
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

		if (!getActor().getAroundCharacters(300, 150).isEmpty() && getActor().getFollowTarget() == null)
		{
			for (Creature obj : getActor().getAroundCharacters(300, 150))
			{
				if (obj.isPlayer())
					actor.setFollowTarget(obj);
			}
		}

		if(_target == null)
		{
			List<NpcInstance> around = actor.getAroundNpc(800, 150);
			for(NpcInstance npc : around)
			{
				if(checkTarget(npc))
				{
					if(_target == null || actor.getDistance3D(npc) < actor.getDistance3D(_target))
						_target = npc;
				}
			}
		}

		if(_target == null && !attack)
		{
			Creature target = actor.getFollowTarget();

			if(target == null || target.isAlikeDead() || actor.isMovementDisabled() || target.getReflection() != actor.getReflection())
			{
				clientActionFailed();
				return false;
			}

			//Уже следуем за этой целью
			if(actor.isFollow && actor.getFollowTarget() == target)
			{
				clientActionFailed();
				return false;
			}

			if(actor.isInRange(target, Config.FOLLOW_RANGE + 100))
			{
				clientActionFailed();
				return false;
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
			return false;
		}

		if(!actor.isAttackingNow() && !actor.isCastingNow())
		{
			if(_target != null && !_target.isDead() && GeoEngine.canSeeTarget(actor, _target, false))
			{
				actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _target, 1);
				if(!attack)
					Functions.npcSay(actor, SAY_TEXT[Rnd.get(0, SAY_TEXT.length - 1)]);
				attack = true;
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
