package ai.selmahum;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.utils.Functions;

/**
 * @reworked by Bonux
**/
public class DrillRecruit extends Fighter
{
	private final int trainer_id;
	private final int direction;

	private int i_ai0 = 0;
	private int i_ai2 = 0;
	private int i_ai3 = 0;
	private int i_ai4 = 0;
	private int i_ai6 = 0;
	private int i_quest0 = 0;
	private int c_ai1 = 0;

	public DrillRecruit(NpcInstance actor)
	{
		super(actor);

		actor.setRandomWalk(false);
		actor.setHaveRandomAnim(false);

		trainer_id = actor.getParameter("trainer_id", 0);
		direction = actor.getParameter("direction", 0);
	}

	@Override
	protected void onEvtSpawn()
	{
		i_ai0 = 0;
		i_quest0 = 0;
		if(Rnd.get(18) < 1)
		{
			i_ai0 = 1;
			i_quest0 = 1;
			addTimer(2019999, 1000);
		}
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(!actor.isDead())
		{
			if(actor.getX() == actor.getSpawnedLoc().getX() && actor.getSpawnedLoc().getY() == actor.getY())
				actor.setHeading(direction, true);
			else if(i_ai6 == 0 && !actor.isInCombat())
			{
				clearTasks();
				actor.teleToLocation(actor.getSpawnedLoc());
			}
		}
		return super.thinkActive();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(i_ai6 == 1 )
			return;

		broadCastScriptEvent(String.valueOf(10016 + trainer_id), attacker.getObjectId(), 1000);
		super.onEvtAttacked(attacker, skill, damage);

	}

	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		if(i_ai6 == 1)
			return;

		super.onEvtClanAttacked(attacked, attacker, damage);
	}

	@Override
	protected void onEvtScriptEvent(String event, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(event.equalsIgnoreCase(String.valueOf(10016 + trainer_id)))
		{
			GameObject c0 = GameObjectsStorage.findObject(((Number) arg1).intValue());
			if(c0 != null && c0.isCreature())
			{
				actor.getAggroList().clear(true);
				clearTasks();
				//removeAllAttackDesire();
				if(c0.isPlayable())
				{
					actor.getAggroList().addDamageHate((Creature) c0, 0, 100);
					addTaskAttack((Creature) c0);
					//addAttackDesire(c0, 1, 100);
				}

				actor.getAggroList().addDamageHate((Creature) c0, 0, 5000);
				addTaskAttack((Creature) c0);
				//addAttackDesire(c0, 1, 5000);
			}
		}
		else if(event.equalsIgnoreCase(String.valueOf(2219023 + trainer_id)))
		{
			GameObject c0 = GameObjectsStorage.findObject(((Number) arg1).intValue());
			if(c0 != null && c0.isCreature())
			{
				i_ai6 = 1;
				clearTasks();
				addTaskMove(c0.getLoc(), true);
				//addFleeDesire(c0, 50000000);
				if(Rnd.get(4) < 1)
				{
					if(Rnd.get(2) < 1)
						Functions.npcSay(getActor(), NpcString.THE_DRILLMASTER_IS_DEAD);
					else
						Functions.npcSay(getActor(), NpcString.LINE_UP_THE_RANKS);
				}
				c_ai1 = c0.getObjectId();
				addTimer(2019777, 10);
				addTimer(2019888, 5000);
			}
		}
		else if(arg1 instanceof Integer && (Integer) arg1 == trainer_id && i_ai6 == 0)
		{
			if(event.equalsIgnoreCase(String.valueOf(2219011)))
			{
				if(i_ai0 != 1)
				{
					i_ai2 = 70;
					i_ai3 = 4;
					i_ai4 = 2;
					addTimer(22201, 100);
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(2219012)))
			{
				if(i_ai0 != 1)
				{
					i_ai2 = 130;
					i_ai3 = 1;
					i_ai4 = 2;
					addTimer(22201, 100);
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(2219013)))
			{
				if(i_ai0 != 1)
				{
					i_ai2 = 30;
					i_ai3 = 5;
					i_ai4 = 4;
					addTimer(22201, 100);
				}
				else
				{
					i_ai2 = 30;
					i_ai3 = 6;
					i_ai4 = 4;
					addTimer(22201, 100);
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(2219014)))
			{
				if(i_ai0 != 1)
				{
					i_ai2 = 30;
					i_ai3 = 7;
					i_ai4 = 2;
					addTimer(22201, 100);
				}
			}
		}
		super.onEvtScriptEvent(event, arg1, arg2);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(timerId == 2019999)
			addTimer(2019999, 5000);
		else if(timerId == 2019888)
			i_ai6 = 0;
		else if(timerId == 2019777)
		{
			GameObject target = GameObjectsStorage.findObject(c_ai1);
			if(target != null && target.isCreature())
				addTaskMove(target.getLoc(), true);
			//addFleeDesire(L2ObjectsStorage.getAsCharacter(c_ai1), 50000000);
			if(i_ai6 == 1)
				addTimer(2019777, 1000);
		}
		else if(timerId == 22201)
		{
			actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), i_ai3));
			//addEffectActionDesire(i_ai3, i_ai2 * 1000 / 30, 500);
			if(i_ai4 != 0)
			{
				i_ai4 = i_ai4 - 1;
				addTimer(22201, ((i_ai2 * 1000) / 30));
			}
		}
		super.onEvtTimer(timerId, arg1, arg2);
	}
}