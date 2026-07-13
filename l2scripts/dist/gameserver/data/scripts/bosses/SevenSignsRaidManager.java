package bosses;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnTeleportedListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;
import l2s.gameserver.utils.ZoneUtils;

/**
 * @author Bonux
 * TODO: Написано все наугад. По возможности переписать под офф.
**/
public abstract class SevenSignsRaidManager
{
	private static enum DungeonStatus
	{
		NONE,
		DESTROY_MONSTERS,	// Стадия убийства монстров в подземелье.
		SEAL_REMNANTS_DESTROY,	// Стадия убийства двух Следов Печати.
		ENTER_TO_RAID,	// Стадия телепортации к рейду.
		RAID_DESTROY,	// Стадия убийства рейда.
		RAID_FINISHED;	// Стадия окончания рейда.

		public static final DungeonStatus[] VALUES = values();
	}

	private class DungeonZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(cha.isPlayer())
			{
				Player player = cha.getPlayer();
				if(zone == _dungeonZone)
				{
					if(_status == DungeonStatus.NONE || player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel())
						expelFromDungeon(player);
					else if(_zigguratNpc == null && (_status == DungeonStatus.ENTER_TO_RAID || _status == DungeonStatus.RAID_DESTROY))
						exitFromDungeon(player);
				}
				else if(zone == _raidZone)
				{
					if(_status != DungeonStatus.ENTER_TO_RAID && _status != DungeonStatus.RAID_DESTROY && _status != DungeonStatus.RAID_FINISHED || player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel())
						expelFromDungeon(player);
				}
			}
			else if(cha.isMonster())
				cha.addListener(_deathListener);
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
			if(self.isNpc())
			{
				if(_status == DungeonStatus.DESTROY_MONSTERS)
				{
					if(!ZoneUtils.checkAliveMonstersInZone(_dungeonZone))
					{
						setStatus(DungeonStatus.SEAL_REMNANTS_DESTROY);
						checkStatus();
					}
				}
				else if(self.getNpcId() == SEAL_REMNANT)
				{
					if(_status == DungeonStatus.SEAL_REMNANTS_DESTROY)
					{
						if(!ZoneUtils.checkAliveMonstersInZone(_dungeonZone, SEAL_REMNANT))
						{
							_zigguratNpc = NpcUtils.spawnSingle(getEnterGatekeeperId(), self.getLoc());
							setStatus(DungeonStatus.ENTER_TO_RAID);
							checkStatus();
						}
					}
				}
				else if(self.getNpcId() == getRaidId())
				{
					if(_status == DungeonStatus.RAID_DESTROY)
					{
						setState(EpicBossState.State.DEAD);
						setStatus(DungeonStatus.RAID_FINISHED);
						checkStatus();
					}
				}
			}
		}
	}

	private class TeleportedListener implements OnTeleportedListener
	{
		@Override
		public void onTeleported(Player player)
		{
			player.removeListener(_teleportedListener);
			if(_status == DungeonStatus.ENTER_TO_RAID)
				player.startScenePlayer(getRaidEnterSceneMovie());
		}
	}

	// Monster's
	private static final int SEAL_REMNANT = 19490;	//	След Печати

	private final OnZoneEnterLeaveListener _zoneListener = new DungeonZoneListener();
	private final OnDeathListener _deathListener = new DeathListener();
	private final OnTeleportedListener _teleportedListener = new TeleportedListener();

	private EpicBossState _state;
	private DungeonStatus _status = DungeonStatus.NONE;
	private Zone _dungeonZone;
	private Zone _raidZone;
	private NpcInstance _zigguratNpc = null;
	private ScheduledFuture<?> _processStatusTask = null;

	public void onInit()
	{
		_state = new EpicBossState(getRaidId());
		_status = DungeonStatus.VALUES[ServerVariables.getInt(getDungeonStatusVar(), 0)];
		_dungeonZone = ReflectionUtils.getZone(getDungeonZoneName());
		_dungeonZone.addListener(_zoneListener);
		_raidZone = ReflectionUtils.getZone(getRaidZoneName());
		_raidZone.addListener(_zoneListener);

		checkStatus();
	}

	private void checkStatus()
	{
		if(_state.getState() == EpicBossState.State.INTERVAL)
		{
			long reuseDate = getReusePattern().next(_state.getRespawnDate());
			if(System.currentTimeMillis() > reuseDate)
			{
				setState(EpicBossState.State.NOTSPAWN);
				setStatus(DungeonStatus.DESTROY_MONSTERS);
			}
			else if(_status != DungeonStatus.NONE)
				setStatus(DungeonStatus.NONE);

			if(_status == DungeonStatus.NONE)
			{
				_processStatusTask = ThreadPoolManager.getInstance().schedule(() ->
				{
					setState(EpicBossState.State.NOTSPAWN);
					setStatus(DungeonStatus.DESTROY_MONSTERS);
					checkStatus();
				}, reuseDate - System.currentTimeMillis());
			}
		}

		if(_state.getState() == EpicBossState.State.NOTSPAWN)
		{
			if(_status != DungeonStatus.DESTROY_MONSTERS && _status != DungeonStatus.SEAL_REMNANTS_DESTROY && _status != DungeonStatus.ENTER_TO_RAID)
				setStatus(DungeonStatus.DESTROY_MONSTERS);
		}
		else if(_state.getState() == EpicBossState.State.ALIVE)
		{
			if(_status != DungeonStatus.RAID_DESTROY)
				setStatus(DungeonStatus.RAID_DESTROY);
		}
		else if(_state.getState() == EpicBossState.State.DEAD)
		{
			if(_status != DungeonStatus.RAID_FINISHED)
				setStatus(DungeonStatus.RAID_FINISHED);
		}

		switch(_status)
		{
			case NONE:
				SpawnManager.getInstance().despawn(getRaidZigguratSpawnGroup());
				break;
			case DESTROY_MONSTERS:
				SpawnManager.getInstance().spawn(getDungeonMonstersSpawnGroup());
				break;
			case SEAL_REMNANTS_DESTROY:
				SpawnManager.getInstance().despawn(getDungeonMonstersSpawnGroup());
				SpawnManager.getInstance().spawn(getSealRemnantsSpawnGroup());
				break;
			case ENTER_TO_RAID:
				SpawnManager.getInstance().despawn(getSealRemnantsSpawnGroup());
				break;
			case RAID_DESTROY:
				SpawnManager.getInstance().spawn(getRaidSpawnGroup());
				break;
			case RAID_FINISHED:
			{
				SpawnManager.getInstance().despawn(getRaidSpawnGroup());
				SpawnManager.getInstance().spawn(getRaidZigguratSpawnGroup());

				if(_zigguratNpc != null)
					_zigguratNpc.deleteMe();

				_processStatusTask = ThreadPoolManager.getInstance().schedule(() ->
				{
					setState(EpicBossState.State.INTERVAL);
					setStatus(DungeonStatus.NONE);
					checkStatus();
				}, 3600000L);
				break;
			}
		}
	}

	private void setState(EpicBossState.State value)
	{
		if(value == EpicBossState.State.ALIVE)
			_state.setNextRespawnDate(getReusePattern().next(System.currentTimeMillis()));
		_state.setState(value);
		_state.save();
	}

	private void setStatus(DungeonStatus value)
	{
		_status = value;
		ServerVariables.set(getDungeonStatusVar(), _status.ordinal());
	}

	public int tryEnterToDungeon(Player player)
	{
		if(_status == DungeonStatus.ENTER_TO_RAID)
		{
			if(getMinMembersCount() > Party.MAX_SIZE)
			{
				if(player.getParty() == null)
					return 4;

				if(player.getParty().getCommandChannel() == null)
					return 5;

				if(!player.getParty().getCommandChannel().isLeaderCommandChannel(player))
					return 6;

				int channelMemberCount = player.getParty().getCommandChannel().getMemberCount();
				if(channelMemberCount > getMaxMembersCount() || channelMemberCount < getMinMembersCount())
					return 7;

				for(Player p : player.getParty().getCommandChannel().getMembers())
				{
					if(p.getLevel() > getMaxLevel() || p.getLevel() < getMinLevel())
						return 8;
				}

				for(Player p : player.getParty().getCommandChannel().getMembers())
				{
					p.addListener(_teleportedListener);
					p.teleToLocation(Location.findPointToStay(getRaidEnterLocation(), 80, p.getGeoIndex()));
				}
			}
			else if(getMinMembersCount() > 1)
			{
				if(player.getParty() == null)
					return 4;

				if(!player.getParty().isLeader(player))
					return 6;

				int partyMemberCount = player.getParty().getMemberCount();
				if(partyMemberCount > getMaxMembersCount() || partyMemberCount < getMinMembersCount())
					return 7;

				for(Player p : player.getParty().getPartyMembers())
				{
					if(p.getLevel() > getMaxLevel() || p.getLevel() < getMinLevel())
						return 8;
				}

				for(Player p : player.getParty().getPartyMembers())
				{
					p.addListener(_teleportedListener);
					p.teleToLocation(Location.findPointToStay(getRaidEnterLocation(), 80, p.getGeoIndex()));
				}
			}
			else if(getMinMembersCount() == 1)
			{
				if(player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel())
					return 8;

				player.addListener(_teleportedListener);
				player.teleToLocation(Location.findPointToStay(getRaidEnterLocation(), 80, player.getGeoIndex()));
			}

			if(_processStatusTask == null)
			{
				_processStatusTask = ThreadPoolManager.getInstance().schedule(() ->
				{
					setState(EpicBossState.State.ALIVE);
					setStatus(DungeonStatus.RAID_DESTROY);
					checkStatus();
				}, getRaidEnterSceneMovie().getDuration());
			}
		}
		else
		{
			if(_status == DungeonStatus.NONE || _status == DungeonStatus.RAID_FINISHED)
				return 1;

			if(player.getLevel() > getMaxLevel())
				return 2;

			if(player.getLevel() < getMinLevel())
				return 3;

			if(_status == DungeonStatus.RAID_DESTROY)
				player.teleToLocation(Location.findPointToStay(getRaidEnterLocation(), 80, player.getGeoIndex()));
			else
				player.teleToLocation(Location.findPointToStay(getEnterLocation(), 80, player.getGeoIndex()));
		}
		return 0;
	}

	public void tryExitFromDungeon(Player player)
	{
		if(_status == DungeonStatus.RAID_FINISHED)
			expelFromDungeon(player);
		else
			exitFromDungeon(player);
	}

	private void exitFromDungeon(Player player)
	{
		player.teleToLocation(Location.findPointToStay(getExitLocation(), 80, player.getGeoIndex()));
	}

	private void expelFromDungeon(Player player)
	{
		player.teleToLocation(Location.findPointToStay(getOutsideLocation(), 80, player.getGeoIndex()));
	}

	protected abstract int getMinLevel();

	protected abstract int getMaxLevel();

	protected abstract int getMinMembersCount();

	protected abstract int getMaxMembersCount();

	protected abstract SchedulingPattern getReusePattern();

	protected abstract Location getEnterLocation();

	protected abstract Location getExitLocation();

	protected abstract Location getOutsideLocation();

	protected abstract Location getRaidEnterLocation();

	protected abstract String getDungeonStatusVar();

	protected abstract String getDungeonZoneName();

	protected abstract String getRaidZoneName();

	protected abstract SceneMovie getRaidEnterSceneMovie();

	protected abstract int getEnterGatekeeperId();

	protected abstract int getRaidId();

	protected abstract String getDungeonMonstersSpawnGroup();

	protected abstract String getSealRemnantsSpawnGroup();

	protected abstract String getRaidSpawnGroup();

	protected abstract String getRaidZigguratSpawnGroup();
}