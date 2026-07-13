package instances;

import l2s.commons.util.Rnd;
import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//By Evil_dnk

public class SteamCorridor extends Reflection
{
	private long _savedTime;
	private DeathListener _deathListener = new DeathListener();
	private ZoneListener _startZoneListener = new ZoneListener();
	private static final int[] ChestOfDim = {19576, 19577, 19578, 19579, 19580, 19581, 19582, 19583, 19584, 19585, 19586, 19587};
	private boolean spawnedMinions = false;
	private final int[] Kechi = {25797, 26111, 26112, 26113};
	private static final Logger _log = LoggerFactory.getLogger(SteamCorridor.class);

	private static final Location Firstnpc = new Location(146728, 218216, -12163, 0);
	private static final Location Secondtnpc = new Location(149512, 218408, -12163, 0);
	private static final Location Thirdnpc = new Location(152296, 217944, -12163, 0);
	private static final Location Fournpc = new Location(148712, 219880, -12163, 0);
	private static final Location Fiftnpc = new Location(145752, 219816, -12163, 0);
	private static final Location Bosnpc = new Location(151816, 215416, -12163, 0);

	private boolean room1 = false;
	private boolean room2 = false;
	private boolean room3 = false;
	private boolean room4 = false;
	private boolean room5 = false;
	private boolean room6 = false;

	NpcInstance _firtsnpc = null;
	NpcInstance _secondtnpc = null;
	NpcInstance _thirdnpc = null;
	NpcInstance _fournpc = null;
	NpcInstance _fiftnpc = null;
	NpcInstance _bosnpc = null;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		NpcInstance Boss = addSpawnWithoutRespawn(Rnd.get(Kechi), new Location(154088, 215128, -12152, 31900), 0);//Спавним финального босса
		Boss.addListener(_deathListener);

		_firtsnpc = addSpawnWithoutRespawn(23014, Firstnpc, 0);
		_firtsnpc.addListener(_deathListener);
		_secondtnpc = addSpawnWithoutRespawn(23014, Secondtnpc, 0);
		_secondtnpc.addListener(_deathListener);
		_thirdnpc = addSpawnWithoutRespawn(23015, Thirdnpc, 0);
		_thirdnpc.addListener(_deathListener);
		_fournpc = addSpawnWithoutRespawn(23015, Fournpc, 0);
		_fournpc.addListener(_deathListener);
		_fiftnpc = addSpawnWithoutRespawn(23016, Fiftnpc, 0);
		_fiftnpc.addListener(_deathListener);
		_bosnpc = addSpawnWithoutRespawn(23016, Bosnpc, 0);
		_bosnpc.addListener(_deathListener);

		getZone("[Steam1to2]").addListener(_startZoneListener);
		getZone("[Steam2to3]").addListener(_startZoneListener);
		getZone("[Steam3to4]").addListener(_startZoneListener);
		getZone("[Steam4to5]").addListener(_startZoneListener);
		getZone("[Steam5to6]").addListener(_startZoneListener);
		getZone("[Steam6toBoss]").addListener(_startZoneListener);
	}

	@Override
	protected void onCollapse()
	{
		super.onCollapse();
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		_savedTime = System.currentTimeMillis();
		player.sendPacket(new ExSendUIEventPacket(player, 0, 1, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));//Пускаем таймер
	}

	@Override
	public void onPlayerExit(Player player)
	{
		player.sendPacket(new ExSendUIEventPacket(player, 1, 1, 0, 0)); //Отключаем таймер
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && ArrayUtils.contains(Kechi, self.getNpcId()))
			{
				addSpawnWithoutRespawn(Rnd.get(ChestOfDim), self.getLoc(), 0);

				for(Player p : getPlayers())
				{
					p.sendPacket(new ExSendUIEventPacket(p, 1, 1, 0, 0));
					return;
				}

				clearReflection(5, true);

			}
			else if (self == _firtsnpc)
			{
				sayToall();
				room1 = true;
			}
			else if (self == _secondtnpc)
			{
				sayToall();
				room2 = true;
			}
			else if (self == _thirdnpc)
			{
				sayToall();
				room3 = true;
			}else if (self == _fournpc)
			{
				sayToall();
				room4 = true;
			}
			else if (self == _fiftnpc)
			{
				sayToall();
				room5 = true;
			}
			else if (self == _bosnpc)
			{
				sayToall();
				room6 = true;
			}
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)//Вход в зону.
		{
			Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer())
			{
				return;
			}

			if(zone.getName().equalsIgnoreCase("[Steam1to2]"))
			{
				if(zone.isActive() && room1)
					player.teleToLocation(147528, 218200, -12162, player.getReflection());
			}
			else if(zone.getName().equalsIgnoreCase("[Steam2to3]"))
			{
				if(zone.isActive() && room2)
					player.teleToLocation(150152, 218200, -12152, player.getReflection());
			}
			else if(zone.getName().equalsIgnoreCase("[Steam3to4]")) 
			{
				if(zone.isActive() && room3)
					player.teleToLocation(150696, 220072, -12149, player.getReflection());
			}
			else if(zone.getName().equalsIgnoreCase("[Steam4to5]")) 
			{
				if(zone.isActive() && room4)
					player.teleToLocation(148008, 220072, -12148, player.getReflection());
			}
			else if(zone.getName().equalsIgnoreCase("[Steam5to6]")) 
			{
				if(zone.isActive() && room5)
					player.teleToLocation(149784, 215592, -12146, player.getReflection());
			}
			else if(zone.getName().equalsIgnoreCase("[Steam6toBoss]"))
			{
				if(zone.isActive() && room6)
					player.teleToLocation(153384, 215128, -12129, player.getReflection());
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)//На выходе из зоны ничего не происходит
		{
		}
	}

	public void spawnMinions()
	{
		if(!spawnedMinions)
		{
			spawnByGroup("kechiminions");
			spawnedMinions = true;
		}
	}

	private void sayToall()
	{
		for (Player p : getPlayers())
		{
			p.sendPacket(new ExShowScreenMessage(NpcString.THE_PORTAL_TO_THE_NEXT_ROOM_IS_NOW_OPEN, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
		}
	}
}