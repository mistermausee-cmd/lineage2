package npc.model.octavis;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

import instances.Octavis;

/**
 * @author Bonux
**/
public final class OctavisFirstInstance extends RaidBossInstance
{
	private static final long serialVersionUID = 1L;

	public OctavisFirstInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isInvulnerable()
	{
		NpcInstance leader = getLeader();
		if(leader != null && leader.getCurrentHpPercents() > 50)
			return true;
		return super.isInvulnerable();
	}

	@Override
	public boolean isTargetable(Creature creature)
	{
		NpcInstance leader = getLeader();
		if(leader != null && leader.getCurrentHpPercents() > 50)
		{
			if(creature.isPlayer() && creature.getPlayer().isGM())
				return true;
			return false;
		}
		return super.isTargetable(creature);
	}

	@Override
	public int getRunSpeed()
	{
		NpcInstance leader = getLeader();
		if(leader != null)
			return leader.getRunSpeed();
		return super.getRunSpeed();
	}

	@Override
	public int getWalkSpeed()
	{
		NpcInstance leader = getLeader();
		if(leader != null)
			return leader.getWalkSpeed();
		return super.getWalkSpeed();
	}

	@Override
	public boolean isDeathImmune()
	{
		return true;
	}

	@Override
	public void onChangeCurrentHp(double oldHp, double newHp)
	{
		super.onChangeCurrentHp(oldHp, newHp);

		if(getCurrentHp() < 5)
		{
			setNpcState(6);
			setNpcState(0);
			deleteMe();

			Reflection reflection = getReflection();
			if(reflection instanceof Octavis)
				((Octavis) reflection).nextState();

			return;
		}

		double hpPercents = getCurrentHpPercents();
		int npcState = getNpcState();
		if(hpPercents <= 50 && npcState == 0)
		{
			int effect = (int) (hpPercents / 10);
			if(effect > 0 && effect < 5)
				setNpcState(effect);
			else if(effect == 0)
				setNpcState(5);
		}
		else if(hpPercents > 50 && npcState == 1)
		{
			setNpcState(6);
			setNpcState(0);
		}
	}
}