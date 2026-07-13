package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class InfiltrationOfficerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int ELECTRICITY_GENERATOR = 33216;

	private boolean _attackableByMonsters = false;

	public InfiltrationOfficerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Reflection reflection = getReflection();
		if(!reflection.isDefault())
			reflection.collapse();

		super.onDeath(killer);
	}

	@Override
	public boolean isPeaceNpc()
	{
		return false;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.getNpcId() == ELECTRICITY_GENERATOR || _attackableByMonsters && attacker.isMonster();
	}

	public void setAttackableByMonsters(boolean value)
	{
		_attackableByMonsters = value;
	}
}
