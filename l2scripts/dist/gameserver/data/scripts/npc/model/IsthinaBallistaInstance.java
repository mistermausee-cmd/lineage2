package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class IsthinaBallistaInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public IsthinaBallistaInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isThrowAndKnockImmune()
	{
		return true;
	}

	@Override
	public boolean isTransformImmune()
	{
		return true;
	}

	@Override
	public boolean hasRandomWalk()
	{
		return false;
	}
}