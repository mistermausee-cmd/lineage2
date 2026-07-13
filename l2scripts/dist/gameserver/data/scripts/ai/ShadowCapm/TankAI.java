package ai.ShadowCapm;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;


//By Evil_dnk

public class TankAI extends Fighter
{
	// Tank
	final Skill t_aggr = getSkill(23728, 1), t_para = getSkill(23729, 1), t_galak = getSkill(23730, 1), t_shield = getSkill(23732, 1);
	final Skill t_gydra = getSkill(23731, 1);
	private static long _gydra = 0;

	public TankAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		double distance = actor.getDistance(target);

		// Basic Attack
		if(distance > 100 && _gydra < System.currentTimeMillis())
		{
			_gydra = System.currentTimeMillis() + 45000L;
			return chooseTaskAndTargets(t_gydra, target, distance);
		}

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		addDesiredSkill(d_skill, target, distance, t_aggr);
		addDesiredSkill(d_skill, target, distance, t_para);
		addDesiredSkill(d_skill, target, distance, t_galak);
		addDesiredSkill(d_skill, target, distance, t_shield);

		Skill r_skill = selectTopSkill(d_skill);
		if(r_skill != null && !r_skill.isOffensive())
			target = actor;

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	private Skill getSkill(int id, int level)
	{
		return SkillHolder.getInstance().getSkill(id, level);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean teleportHome()
	{
		return false;
	}

	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		return false;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_gydra = System.currentTimeMillis() + 4000L;
	}
}