package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.templates.npc.NpcTemplate;


public class SpecialMonsterInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public SpecialMonsterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}