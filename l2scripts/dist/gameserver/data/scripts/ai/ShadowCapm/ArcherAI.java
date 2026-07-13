package ai.ShadowCapm;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Ranger;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

//By Evil_dnk

public class ArcherAI extends Ranger
{
	//Archer
	final Skill a_deb = getSkill(23743, 1), a_mass = getSkill(23744, 1), a_stun = getSkill(23745, 1), a_tor = getSkill(23747, 1);
			//Back jump
	final Skill d_back = getSkill(23746, 1);

	private static long _moveback = 0;

	public ArcherAI(NpcInstance actor)
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

		if(_moveback < System.currentTimeMillis() && distance > 150)
		{
			_moveback = System.currentTimeMillis() + 15000L;
			return chooseTaskAndTargets(d_back, target, distance);
		}

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		addDesiredSkill(d_skill, target, distance, a_deb);
		addDesiredSkill(d_skill, target, distance, a_mass);
		addDesiredSkill(d_skill, target, distance, a_stun);
		addDesiredSkill(d_skill, target, distance, a_tor);

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
		_moveback = System.currentTimeMillis() + 4000L;
	}

}