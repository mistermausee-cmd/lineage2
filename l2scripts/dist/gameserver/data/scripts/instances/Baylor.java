package instances;

import ai.BaylorAI;
import ai.BaylorGolemAI;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Location;

import java.util.concurrent.atomic.AtomicInteger;

public class Baylor extends Reflection
{
	private static final int Baylor = 29213;
	private static final int Golem = 29215;

	private static int[] doors = {
			24220009,
			24220011,
			24220012,
			24220014,
			24220015,
			24220016,
			24220017,
			24220019
	};

	public Location[] locs = {
			new Location(152680, 143576, -12704),
			new Location(151864, 142040, -12704),
			new Location(152104, 141224, -12704),
			new Location(153592, 140344, -12704),
			new Location(154440, 140600, -12704),
			new Location(155016, 141256, -12704),
			new Location(155256, 142088, -12704),
			new Location(154408, 143560, -12704)
	};

	private Location move1 = new Location(153512, 141960, -12736, 10750);
	private Location move2 = new Location(153784, 142232, -12762, 0);

	private DeathListener _deathListener = new DeathListener();

	private NpcInstance baylorf = null;
	private NpcInstance baylors = null;

	private AtomicInteger prison = new AtomicInteger(0);

	private boolean zoneLocked = false;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		ZoneListener zoneListener = new ZoneListener(this);
		getZone("[baylor]").addListener(zoneListener);
	}

	public class BaylorSpawn extends RunnableImpl
	{
		private Reflection reflection;

		public BaylorSpawn(Reflection reflection)
		{
			this.reflection = reflection;
		}

		@Override
		public void runImpl()
		{
			baylorf = addSpawnWithoutRespawn(Baylor, move1, 0);
			baylorf.addListener(_deathListener);

			ThreadPoolManager.getInstance().schedule(() -> baylorf.broadcastPacket(new SocialActionPacket(baylorf.getObjectId(), 1)), 250);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				baylorf.setAI(new BaylorAI(baylorf));
				baylors = addSpawnWithoutRespawn(Baylor, move2, 0);
				baylors.addListener(_deathListener);
				baylors.setAI(new BaylorAI(baylors));
			}, 18000);

			for(int i = 0; i < locs.length; i++)
			{
				Location loc = locs[i];

				DeathListener2 deathListener2 = new DeathListener2(doors[i]);

				for(int j = 0; j < 2; j++)
				{
					SimpleSpawner simpleSpawner = new SimpleSpawner(Golem);
					simpleSpawner.setLoc(Location.findPointToStay(loc, 100, reflection.getGeoIndex()));
					simpleSpawner.setReflection(reflection);
					simpleSpawner.setRespawnDelay(0);
					NpcInstance npc = simpleSpawner.doSpawn(true);
					npc.addListener(deathListener2);
					npc.setAI(new BaylorGolemAI(npc));
				}
			}

			SimpleSpawner simpleSpawner = new SimpleSpawner(18474);
			simpleSpawner.setLoc(Location.findPointToStay(new Location(154360, 142072, -12736), 0, reflection.getGeoIndex()));
			simpleSpawner.setReflection(reflection);
			simpleSpawner.setRespawnDelay(0);
			NpcInstance npc = simpleSpawner.doSpawn(true);
			ThreadPoolManager.getInstance().schedule(npc::deleteMe, 30000);
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && (self == baylorf || self == baylors))
			{
				if(self == baylorf)
				{
					baylorf = null;
				}
				else
				{
					baylors = null;
				}

				if(baylorf == null && baylors == null)
				{
					for(Player p : getPlayers())
					{
						p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
					}

					setReenterTime(System.currentTimeMillis());
					startCollapseTimer(5 * 60 * 1000L);
				}
			}
		}
	}

	private class DeathListener2 implements OnDeathListener
	{
		private int count = 0;
		private int doorId;

		public DeathListener2(int doorId)
		{
			this.doorId = doorId;
		}

		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == Golem)
			{
				count++;
				if(count == 2)
				{
					self.getReflection().getDoor(doorId).openMe(killer.getPlayer(), false);
				}
			}
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		private Reflection reflection;

		public ZoneListener(Reflection reflection)
		{
			this.reflection = reflection;
		}

		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(zoneLocked || cha == null || !cha.isPlayer())
			{
				return;
			}
			zoneLocked = true;

			ThreadPoolManager.getInstance().schedule(new BaylorSpawn(reflection), 10000);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			//
		}
	}
}