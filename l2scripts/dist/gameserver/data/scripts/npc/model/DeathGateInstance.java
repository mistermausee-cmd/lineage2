package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.SymbolInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class DeathGateInstance extends SymbolInstance
{
	private static final long serialVersionUID = 1L;

	public DeathGateInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return true;
	}

	@Override
	public boolean isPeaceNpc()
	{
		return false;
	}
}
