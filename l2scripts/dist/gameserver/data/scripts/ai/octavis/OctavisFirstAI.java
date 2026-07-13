package ai.octavis;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @reworked by Bonux
**/
public class OctavisFirstAI extends Fighter
{
	// NPC's
	private static final int VOLCANO_NPC = 19161;

	// Skill ID's
	private static final int VOLCANO_ZONE_SKILL_ID = 14025;

	private static final int Effect_Timer = 2621025;

	public OctavisFirstAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		addTimer(Effect_Timer, 500);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == Effect_Timer)
		{
			NpcInstance actor = getActor();
			if(actor != null)
			{
				NpcInstance leader = actor.getLeader();
				if(leader != null)
				{
					actor.sendChannelingEffect(leader, 1);

					if(leader.isRunning())
						actor.setRunning();
					else
						actor.setWalking();

					actor.moveToLocation(leader.getLoc(), 0, true);
				}
			}
			addTimer(Effect_Timer, 500);
		}
		else
			super.onEvtTimer(timerId, arg1, arg2);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(Rnd.chance(1))
		{
			NpcInstance npc = NpcUtils.spawnSingle(VOLCANO_NPC, Location.findPointToStay(attacker.getLoc(), 50, attacker.getGeoIndex()), attacker.getReflection(), 10000L);
			npc.doCast(SkillHolder.getInstance().getSkillEntry(VOLCANO_ZONE_SKILL_ID, 1), attacker, true);
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	public void addTaskCast(Creature target, Skill skill)
	{
		getActor().forceUseSkill(skill, getActor());
	}

	@Override
	public void addTaskBuff(Creature target, Skill skill)
	{
		getActor().forceUseSkill(skill, getActor());
	}

	@Override
	public void addTaskAttack(Creature target)
	{
		//
	}

	@Override
	public void addTaskAttack(Creature target, Skill skill, int weight)
	{
		//
	}

	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		return false;
	}

	@Override
	public void addTaskMove(Location loc, boolean pathfind)
	{
		//
	}
}