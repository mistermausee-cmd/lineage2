package npc.model.octavis;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.MinionSpawner;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public final class OctavisBeastInstance extends RaidBossInstance
{
	private static final long serialVersionUID = 1L;

	private final int _octavisId;

	private OctavisBeastInstance _beast = null;

	public OctavisBeastInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		_octavisId = getParameter("octavis_id", 0);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		if(_octavisId > 0)
		{
			MinionSpawner spawner = getMinionList().addMinion(_octavisId, 1, 0);
			if(spawner != null)
			{
				spawner.setLoc(new Location(207069, 120580, -9987));
				spawner.init();
			}
		}
	}

	@Override
	public boolean isDeathImmune()
	{
		return true;
	}

	@Override
	public double getHpRegen()
	{
		return getMaxHp() * 0.03;
	}
}