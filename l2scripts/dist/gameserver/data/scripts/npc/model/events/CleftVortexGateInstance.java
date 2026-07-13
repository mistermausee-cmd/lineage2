package npc.model.events;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 21:09/15.07.2011
 */
public class CleftVortexGateInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public CleftVortexGateInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setShowName(false);
	}
}
