package ai.Balthus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instances.BaltusKnight;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.DefaultAI;
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

public class AntarasInst extends DefaultAI
{
	// debuffs
	final Skill s_antaras_lite_fear = getSkill(14387, 1), s_antaras_lite_short_fear1 = getSkill(14388, 1),
			s_antaras_lite_debuff = getSkill(14381, 1),  s_antaras_lite_breath = getSkill(14384, 1);

	// AOE skills
	final Skill s_antaras_lite_jump = getSkill(14382, 1), s_antaras_lite_meteor1 = getSkill(14383, 1);

	//ordinal
	final Skill s_antaras_lite_mouth = getSkill(14378, 1), s_antaras_lite_mouth2 = getSkill(14379, 1),
		s_antaras_lite_tail = getSkill(14380, 1),  s_antaras_lite_normal_attack = getSkill(14385, 1),
			s_antaras_lite_normal_attack_ex = getSkill(14386, 1);

	// Vars
	private int _hpStage = 0;
	private static final Location CENTER = new Location(180664, 114904, -7689, 0);
	private static final Location INCAVE = new Location(186120, 114808, -8245, 0);
	private static final Location SPAWN = new Location(176646, 114768, -7708, 21156);
	public AntarasInst(NpcInstance actor)
	{
		super(actor);
		setMaxPursueRange(20000);
	}


	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if (attacker == null)
		{
			return;
		}
		List<NpcInstance> around = getActor().getAroundNpc(4000, 1000);
		if (around != null && !around.isEmpty())
		{
			for (NpcInstance npc : around)
			{
				if (npc.getNpcId() != 19098 && npc.getNpcId() != 19097 && npc.getNpcId() != 19140 && npc.getNpcId() != 19134)
				{
					notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 1);
				}
			}

			for (Player p : getActor().getReflection().getPlayers())
				notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1);

			super.onEvtAttacked(attacker, skill, damage);
		}
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

		// Buffs and stats
		double chp = actor.getCurrentHpPercents();
		if(_hpStage == 0)
		{
			_hpStage = 1;
		}

		Reflection r = getActor().getReflection();
		if(r != null)
		{
			if (r instanceof BaltusKnight)
			{
				BaltusKnight lInst = (BaltusKnight) r;
				if (lInst.getStage() == 1)
				{
					if (Rnd.chance(10))
						return chooseTaskAndTargets(s_antaras_lite_tail, target, distance);
					else
						return false;
				}
				else if (lInst.getStage() == 2)
				{
					getActor().getAggroList().clear();
					addTaskMove(Location.findPointToStay(INCAVE, 40, 40, getActor().getGeoIndex()), true);
				}
				else if (lInst.getStage() == 4)
				{
					getActor().getAggroList().clear();
					addTaskMove(Location.findPointToStay(CENTER, 40, 40, getActor().getGeoIndex()), true);
				}
				else if (lInst.getStage() == 11)
				{
					getActor().getAggroList().clear();
					addTaskMove(Location.findPointToStay(SPAWN, 40, 40, getActor().getGeoIndex()), true);
				}
			}
		}

		// Basic Attack
		if(Rnd.chance(30))
			return chooseTaskAndTargets(Rnd.chance(50) ? s_antaras_lite_mouth : s_antaras_lite_mouth2, target, distance);
		else if(Rnd.chance(65))
			return chooseTaskAndTargets(Rnd.chance(50) ? s_antaras_lite_normal_attack : s_antaras_lite_normal_attack_ex, target, distance);
		else if(Rnd.chance(3))
			return chooseTaskAndTargets(Rnd.chance(30) ? s_antaras_lite_short_fear1 : s_antaras_lite_fear, target, distance);
		else if(Rnd.chance(5))
			return chooseTaskAndTargets(s_antaras_lite_debuff, target, distance);
		else if(Rnd.chance(5))
			return chooseTaskAndTargets(s_antaras_lite_breath, target, distance);
		else if(Rnd.chance(5))
		{
			for (Playable p : getActor().getReflection().getPlayers())
				p.sendPacket(new ExShowScreenMessage(NpcString.WRATH_OF_THE_GROUND_WILL_FALL_FROM_THE_SKY_ON_S1, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, String.valueOf(p.getName())));

			return chooseTaskAndTargets(s_antaras_lite_meteor1, target, distance);
		}
			// Stage based skill attacks
		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		switch(_hpStage)
		{
			case 1:
				addDesiredSkill(d_skill, target, distance, s_antaras_lite_jump);
				addDesiredSkill(d_skill, target, distance, s_antaras_lite_tail);
			break;
			default:
				break;
		}

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
	protected boolean hasRandomWalk()
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
}