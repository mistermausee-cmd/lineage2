package ai.ShadowCapm;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.ai.Mystic;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;


//By Evil_dnk

public class SummonerAI extends Mystic
{

	//Summoner
	final Skill s_crest = getSkill(23758, 1), s_shadow = getSkill(23760, 1), s_mass = getSkill(23761, 1), s_mark = getSkill(23762, 1);;
		//Debuffs
	final Skill s_dimens = getSkill(23759, 1);

	// Vars
	private long _debuff = 0;

	public SummonerAI(NpcInstance actor)
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

		if(_debuff < System.currentTimeMillis())
		{
			_debuff = System.currentTimeMillis() + 300000L;
			return chooseTaskAndTargets(s_dimens, target, distance);
		}

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		addDesiredSkill(d_skill, target, distance, s_crest);
		addDesiredSkill(d_skill, target, distance, s_shadow);
		addDesiredSkill(d_skill, target, distance, s_mass);
		addDesiredSkill(d_skill, target, distance, s_mark);

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
		_debuff = System.currentTimeMillis() + 4000L;
	}

}