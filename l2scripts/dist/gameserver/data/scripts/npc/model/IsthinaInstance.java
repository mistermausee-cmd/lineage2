package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

import instances.Isthina;

//By Evil_dnk
public class IsthinaInstance extends ReflectionBossInstance
{
	private static final long serialVersionUID = 1L;

	public IsthinaInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isDeathImmune()
	{
		return (getReflection() instanceof Isthina);
	}
}