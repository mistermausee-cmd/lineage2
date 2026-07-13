package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

public class LostCaptainInstance extends ReflectionBossInstance
{
	private static final long serialVersionUID = 1L;

	private static final int TELE_DEVICE_ID = 4314;

	public LostCaptainInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Reflection r = getReflection();
		r.setReenterTime(System.currentTimeMillis());

		super.onDeath(killer);

		InstantZone iz = r.getInstancedZone();
		if(iz != null)
		{
			String tele_device_loc = iz.getAddParams().getString("tele_device_loc", null);
			if(tele_device_loc != null)
			{
				NpcUtils.spawnSingle(TELE_DEVICE_ID, Location.parseLoc(tele_device_loc), r);
			}
		}
	}
}
