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
public class DrillSergeant extends Fighter
{
	private final int trainer_id;
	private final int trainning_range;
	private final int direction;

	private int i_ai0 = 0;
	private int i_ai1 = 0;
	private int c_ai0 = 0;

	public DrillSergeant(NpcInstance actor)
	{
		super(actor);

		actor.setRandomWalk(false);
		actor.setHaveRandomAnim(false);

		trainer_id = actor.getParameter("trainer_id", 0);
		trainning_range = actor.getParameter("trainning_range", 1000);
		direction = actor.getParameter("direction", 0);
	}

	@Override
	protected void onEvtSpawn()
	{
		addTimer(2219001, 1000L);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(!actor.isDead())
		{
			i_ai0 = 0;
			if(actor.getX() == actor.getSpawnedLoc().getX() && actor.getSpawnedLoc().getY() == actor.getY())
				actor.setHeading(direction, true);
			else if(!actor.isInCombat())
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
		if(damage > 0)
		{
			NpcInstance actor = getActor();
			if(i_ai0 == 0)
			{
				c_ai0 = attacker.getObjectId();
				broadCastScriptEvent(String.valueOf(10016 + trainer_id), attacker.getObjectId(), trainning_range);
				if(actor.getAggroList().getMostHated(getMaxHateRange()) != null)
				{
					i_ai0 = 1;
					i_ai1 = 1;
					addTimer(2219002, 60000);
				}
			}
		}

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(timerId == 2219001)
		{
			if(!actor.isMoving)
			{
				switch(Rnd.get(6))
				{
					case 0:
						actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 1));
						//addEffectActionDesire(1, (130 * 1000) / 30, 50);
						broadCastScriptEvent(String.valueOf(2219011), trainer_id, trainning_range);
						break;
					case 1:
						actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 4));
						//addEffectActionDesire(4, (70 * 1000) / 30, 50);
						broadCastScriptEvent(String.valueOf(2219012), trainer_id, trainning_range);
						break;
					case 2:
						actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 5));
						//addEffectActionDesire(5, (30 * 1000) / 30, 50);
						broadCastScriptEvent(String.valueOf(2219013), trainer_id, trainning_range);
						break;
					case 3:
					case 4:
					case 5:
						actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 7));
						//addEffectActionDesire(7, (130 * 1000) / 30, 50);
						broadCastScriptEvent(String.valueOf(2219014), trainer_id, trainning_range);
						break;
				}
			}
			addTimer(2219001, 15000L);
		}
		else if(timerId == 2219002)
		{
			i_ai1 = 0;
		}
		super.onEvtTimer(timerId, arg1, arg2);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(i_ai1 == 1)
		{
			GameObject c0 = GameObjectsStorage.findObject(c_ai0);
			if(c0 != null && c0.isCreature())
				broadCastScriptEvent(String.valueOf(2219023 + trainer_id), c_ai0, trainning_range);
		}
		super.onEvtDead(killer);
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
				if(Rnd.get(10) < 1)
				{
					if(Rnd.get(2) < 1)
						Functions.npcSay(getActor(), NpcString.HOW_DARE_YOU_ATTACK_MY_RECRUITS);
					else
						Functions.npcSay(getActor(), NpcString.WHO_IS_DISRUPTING_THE_ORDER);
				}
				if(i_ai0 == 0)
				{
					actor.getAggroList().addDamageHate((Creature) c0, 0, 5000);
					addTaskAttack((Creature) c0);
					//addAttackDesire(c0, 1, 5000);
				}
			}
		}
		super.onEvtScriptEvent(event, arg1, arg2);
	}
}