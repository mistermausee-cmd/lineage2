package ai.tauti;

import gnu.trove.map.TIntObjectMap;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Skill;
import l2s.gameserver.ThreadPoolManager;
import l2s.commons.threading.RunnableImpl;

/**
 * @author Rivelia
 */
public class TautiAxeAI extends DefaultAI
{
	private final Skill tauti_final_attack;

	public TautiAxeAI(NpcInstance actor)
	{
		super(actor);
		tauti_final_attack = SkillHolder.getInstance().getSkill(15125, 1);
		// Epic: tauti_final_attack = getActor().getTemplate().getSkills().get(15126);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
	}

	// @Rivelia.
	@Override
	protected boolean createNewTask()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return true;

		clearTasks();

		// Determinate target.
		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		double distance = actor.getDistance(target);
		Skill r_skill = null;
		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();

		addDesiredSkill(d_skill, target, distance, tauti_final_attack);
		r_skill = selectTopSkill(d_skill);

		if (r_skill != null)
		{
			addTaskCast(target, tauti_final_attack);
			return true;
		}
		return false;
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		super.onEvtAttacked(attacker, skill, damage);
	}
}
