package ai.ShadowCapm;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

//By Evil_dnk

public class DaggerAI extends Fighter
{
	// Dagger
	final Skill d_bluff = getSkill(23738, 1), d_heart= getSkill(23739, 1), d_rain= getSkill(23741, 1), d_trick= getSkill(23742, 1);
			//Ultima
	final Skill d_ultim = getSkill(23740, 1);

	private static long _ultima = 0;

	public DaggerAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(getActor().getCurrentHpPercents() < 60 && _ultima < System.currentTimeMillis())
		{
			_ultima = System.currentTimeMillis() + 600000L;
			addTaskBuff(getActor(), d_ultim);
		}

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

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		addDesiredSkill(d_skill, target, distance, d_bluff);
		addDesiredSkill(d_skill, target, distance, d_heart);
		addDesiredSkill(d_skill, target, distance, d_rain);
		addDesiredSkill(d_skill, target, distance, d_trick);

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
		_ultima = System.currentTimeMillis() + 4000L;
	}

}