package instances;

import ai.BalokAI;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Location;

import java.util.concurrent.atomic.AtomicInteger;

/**
 Obi-Wan
 24.10.2016
 */
public class Balok extends Reflection
{
	private static final int Balok = 29218;

	public Location[] locsPlayer = {
			/* 24220009 */ new Location(152728, 143560, -12704, 53988),
			/* 24220011 */ new Location(151832, 142056, -12704, 0),
			/* 24220012 */ new Location(152104, 141224, -12704, 8191),
			/* 24220014 */ new Location(153576, 140424, -12704, 16383),
			/* 24220015 */ new Location(154424, 140616, -12704, 21220),
			/* 24220016 */ new Location(155064, 141240, -12704, 27931),
			/* 24220017 */ new Location(155288, 142088, -12704, 32767),
			/* 24220019 */ new Location(154424, 143576, -12704, 40959)
	};

	public Location[] locsMonster = {
			/* 24220009 */ new Location(152952, 143160, -12736, 53988),
			/* 24220011 */ new Location(152344, 142088, -12736, 0),
			/* 24220012 */ new Location(152504, 141464, -12736, 8191),
			/* 24220014 */ new Location(153560, 140840, -12736, 16383),
			/* 24220015 */ new Location(154168, 141032, -12736, 21220),
			/* 24220016 */ new Location(154600, 141464, -12736, 27931),
			/* 24220017 */ new Location(154760, 142056, -12736, 32767),
			/* 24220019 */ new Location(154168, 143096, -12736, 40959)
	};

	private Location move1 = new Location(153576, 142072, -12736);

	private NpcInstance vullock = null;

	private AtomicInteger prison = new AtomicInteger(0);
	private boolean firstPrison = true;

	private boolean zoneLocked = false;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		ZoneListener zoneListener = new ZoneListener();
		getZone("[baylor]").addListener(zoneListener);
	}

	public class BalokSpawn extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			for(Player p : getPlayers())
			{
				p.startScenePlayer(SceneMovie.SINEMA_BARLOG_OPENING);
			}

			ThreadPoolManager.getInstance().schedule(()->{
				vullock = addSpawnWithoutRespawn(Balok, move1, 0);
				vullock.setAI(new BalokAI(vullock));
				vullock.addListener(new DeathListener());
			}, 20000);
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(zoneLocked || cha == null || !cha.isPlayer())
			{
				return;
			}
			zoneLocked = true;

			ThreadPoolManager.getInstance().schedule(new BalokSpawn(), 10000);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			//
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			for(NpcInstance npc : self.getReflection().getNpcs())
			{
				npc.deleteMe();
			}

			for(Player p : getPlayers())
			{
				p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
			}

			setReenterTime(System.currentTimeMillis());
			startCollapseTimer(5 * 60 * 1000L);
		}
	}
}