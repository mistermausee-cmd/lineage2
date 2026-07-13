package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.ai.Guard;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class BaltusInstance extends GuardInstance
{

	public BaltusInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster();
	}

	@Override
	public boolean isInvulnerable()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}
}