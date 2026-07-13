package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

//By Evil_dnk

public final class DarionMinionInstance extends MonsterInstance
{
	public DarionMinionInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isDeathImmune()
	{
		return true;
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

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public void onRandomAnimation()
	{
		return;
	}

}