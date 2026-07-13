package ai.ShadowCapm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instances.AshenShadowCamp;
import instances.BaltusKnight;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Location;

//By Evil_dnk

public class IsaAI extends Fighter
{
	//Isa
	final Skill i_debuf = getSkill(23754, 1), i_debuff2 = getSkill(23757, 1);
			//Jupmp
	final Skill i_jump = getSkill(23756, 1);
			//Self
	final Skill i_stone = getSkill(23755, 1);
			//Buff
	final Skill i_rhaps = getSkill(23753, 1);

	private long _stone = 0;
	private long _rhaps = 0;
	private long _jump = 0;

	public IsaAI(NpcInstance actor)
	{
		super(actor);
	}


	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(Rnd.chance(5) && _stone < System.currentTimeMillis())
		{
			_stone = System.currentTimeMillis() + 15000L;
			getActor().altOnMagicUse(getActor(), i_stone);
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

		if(distance > 200 && _jump < System.currentTimeMillis())
		{
			_jump = System.currentTimeMillis() + 10000L;
			return chooseTaskAndTargets(i_jump, target, distance);
		}

		if(Rnd.chance(10) && _rhaps < System.currentTimeMillis())
		{
			_rhaps = System.currentTimeMillis() + 600000L;
			List<NpcInstance> around = actor.getAroundNpc(1100, 1100);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(npc.getFaction().toString().equalsIgnoreCase("shadow_camp"))
					{
						getActor().altOnMagicUse(npc, i_rhaps);
					}
				}
			}
			addTaskBuff(getActor(), i_rhaps);
		}

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		addDesiredSkill(d_skill, target, distance, i_debuf);
		addDesiredSkill(d_skill, target, distance, i_debuff2);

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
		_stone = System.currentTimeMillis() + 4000L;
		_rhaps = System.currentTimeMillis() + 4000L;
		_jump = System.currentTimeMillis() + 4000L;
	}

}