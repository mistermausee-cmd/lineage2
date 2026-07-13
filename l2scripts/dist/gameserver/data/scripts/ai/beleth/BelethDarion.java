package ai.beleth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bosses.NewBelethManager;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.*;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class BelethDarion extends DefaultAI
{
	// Vars
	protected final List<Location> _raidclones = new ArrayList<Location>();
	private int minionid = 29247;
	private boolean _fake = false;
	NpcInstance minion1;
	NpcInstance minion2;
	Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
	private static long _massattackSpawnDelay = 0;
	private static long _notOrdinalattackDelay = 0;
	private int DAMAGE_COUNTER = 0;

	// 0 Stage
	final Skill b_vulcano = getSkill(16158, 1);
	final Skill b_blaze = getSkill(16159, 1);
	final Skill b_flame = getSkill(16160, 1);
	final Skill d_bleed = getSkill(16161, 1);

	// 1 Stage
	final Skill bd_tornado = getSkill(16170, 1);

	public BelethDarion(NpcInstance actor)
	{
		super(actor);
		_raidclones.add(new Location(-17048, 245752, -865));
		_raidclones.add(new Location(-17048, 246152, -865));
		_raidclones.add(new Location(-17368, 246456, -865));
		_raidclones.add(new Location(-17784, 246456, -865));
		_raidclones.add(new Location(-18088, 246168, -865));
		_raidclones.add(new Location(-18056, 245768, -865));
		_raidclones.add(new Location(-17752, 245432, -865));
		_raidclones.add(new Location(-17320, 245432, -865));
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();

		if (actor.isDead())
			return;

		if(DAMAGE_COUNTER == 0)
			actor.getAI().startAITask();

		if(actor.getNpcId() == 29244)
		{
			if (actor.getCurrentHpPercents() < 50.0D && NewBelethManager.getBelethStage() == 0)
			{
				if (!_fake)
				{
					NewBelethManager.setBelethStage(1);
					NewBelethManager.startMainform(_fake);
				}
				else
				{
					NewBelethManager.sleep();
				}
			}
		}
		if(actor.getNpcId() == 29245)
		{
			if (actor.getCurrentHpPercents() <= 10.0D && NewBelethManager.getBelethStage() == 1)
			{
				Location loc = Rnd.get(_raidclones);

				NewBelethManager.setBelethStage(2);
				NewBelethManager.startLastForm(loc);
				_raidclones.remove(loc);
				spawnMinions();
				actor.deleteMe();
			}
		}
		if(actor.getNpcId() == 29246)
		{
			if (actor.getCurrentHpPercents() <= 10.0D && NewBelethManager.getBelethStage() == 1)
			{
				NewBelethManager.setBelethStage(2);
				minion1 = NpcUtils.spawnSingle(29248, getActor().getLoc());
				minion2 = NpcUtils.spawnSingle(29249, getActor().getLoc());
			}
		}

		NewBelethManager.setLastAttackTime();
		DAMAGE_COUNTER++;

		super.onEvtAttacked(attacker, skill, damage);
	}

	public void fakeornot(boolean fake)
	{
		_fake = fake;
	}

	public boolean getfakeornot()
	{
		return _fake;
	}

	protected void spawnMinions()
	{
		 for(Location location : _raidclones)
		 {
			 NpcInstance minion;
			 minion = NpcUtils.spawnSingle(minionid, location);
			 minion.getFlags().getImmobilized().start();
			 minion.setCurrentHp(minion.getMaxHp() * 0.1, false, true);
		 }
	}

	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_massattackSpawnDelay= System.currentTimeMillis() + 60000L;
		_notOrdinalattackDelay = System.currentTimeMillis() + 20000L;
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(getActor().getNpcId() == 29250)
		{
			List<NpcInstance> around = getActor().getAroundNpc(3000, 3000);
			if (around != null && !around.isEmpty())
			{
				for (NpcInstance npc : around)
				{
					if (npc.getNpcId() == minionid)
						npc.deleteMe();
				}
			}
		}
		if(getActor().getNpcId() == 29246)
		{
			if (minion1 != null)
				minion1.deleteMe();
			if (minion2 != null)
				minion2.deleteMe();
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

		if(getActor().getNpcId() == 29244 || getActor().getNpcId() == 29247 || getActor().getNpcId() == 29250)
		{
			addDesiredSkill(d_skill, target, distance, b_vulcano);
			getActor().getFlags().getImmobilized().start();
		}
		else if(getActor().getNpcId() == 29245)
		{
			addDesiredSkill(d_skill, target, distance, b_vulcano);
		}

		//Массовые атаки
		if(getActor().getNpcId() == 29245 || getActor().getNpcId() == 29246)
		{
			if (_massattackSpawnDelay < System.currentTimeMillis())
			{
				addTaskCast(target, bd_tornado);
				_massattackSpawnDelay = System.currentTimeMillis() + (Rnd.get(30000, 60000));
			}
		}

		//Различные дебафы
		if(_notOrdinalattackDelay < System.currentTimeMillis())
		{
			if(Rnd.chance(30))
			{
				if (actor.getNpcId() == 29244)
					addTaskCast(target, b_blaze);
				else if (actor.getNpcId() == 29245)
					addTaskCast(target, b_flame);
				else if (actor.getNpcId() == 29246)
					addTaskCast(target, d_bleed);
				_notOrdinalattackDelay = System.currentTimeMillis() + (Rnd.get(10000, 40000));
			}
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

}