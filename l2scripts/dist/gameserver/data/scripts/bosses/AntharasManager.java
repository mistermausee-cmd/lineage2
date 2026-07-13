package bosses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.BossInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;
import l2s.gameserver.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bosses.EpicBossState.State;
import spawns.TersiManager;

/**
 * @author pchayka
 */

public class AntharasManager implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(AntharasManager.class);

	public static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
				checkAnnihilated();
			else if(self.isNpc() && self.getNpcId() == ANTHARAS_STRONG)
				ThreadPoolManager.getInstance().schedule(new AntharasSpawn(8), 10);
		}
	}

	// Constants
	public static final Location TELEPORT_POSITION = new Location(179700, 113800, -7709);

	private static final int TELEPORT_CUBE_ID = 31859;
	private static final int ANTHARAS_STRONG = 29068;
	private static final Location _teleportCubeLocation = new Location(177615, 114941, -7709, 0);
	private static final Location _antharasLocation = new Location(181911, 114835, -7678, 32542);

	private static final OnDeathListener DEATH_LISTENER = new DeathListener();

	// Models
	private static BossInstance _antharas;
	private static NpcInstance _teleCube;
	private static List<NpcInstance> _spawnedMinions = new ArrayList<NpcInstance>();

	// tasks.
	private static ScheduledFuture<?> _monsterSpawnTask;
	private static ScheduledFuture<?> _intervalEndTask;
	private static ScheduledFuture<?> _socialTask;
	private static ScheduledFuture<?> _moveAtRandomTask;
	private static ScheduledFuture<?> _sleepCheckTask;
	private static ScheduledFuture<?> _onAnnihilatedTask;

	// Vars
	private static EpicBossState _state;
	private static Zone _zone;
	private static long _lastAttackTime = 0;
	private static boolean Dying = false;
	private static boolean _entryLocked = false;

	private static class AntharasSpawn extends RunnableImpl
	{
		private int _distance = 2750;
		private int _taskId = 0;
		private List<Player> _players = getPlayersInside();

		AntharasSpawn(int taskId)
		{
			_taskId = taskId;
		}

		@Override
		public void runImpl()
		{
			if(_taskId == 1)
			{
				_antharas = (BossInstance) NpcUtils.spawnSingle(ANTHARAS_STRONG, _antharasLocation);
				if(_antharas == null)
				{
					_log.warn(AntharasManager.class.getSimpleName() + ": Antharas cannot spawned!");
					return;
				}

				_entryLocked = true;
	
				_antharas.setAggroRange(0);

				_state.setNextRespawnDate(getRespawnTime());
				_state.setState(EpicBossState.State.ALIVE);
				_state.save();

				_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(2), 2000);
				return;
			}

			if(_antharas == null)
			{
				_log.warn(AntharasManager.class.getSimpleName() + ": Cannot find Antharas npc!");
				return;
			}

			switch(_taskId)
			{
				case 1:
					break;
				case 2:
					// set camera.
					for(Player pc : _players)
						if(pc.getDistance(_antharas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_antharas, 700, 13, -19, 0, 20000, 0, 0, 0, 0);
						}
						else
							pc.leaveMovieMode();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(3), 3000);
					break;
				case 3:
					// do social.
					_antharas.broadcastPacket(new SocialActionPacket(_antharas.getObjectId(), 1));

					// set camera.
					for(Player pc : _players)
						if(pc.getDistance(_antharas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_antharas, 700, 13, 0, 6000, 20000, 0, 0, 0, 0);
						}
						else
							pc.leaveMovieMode();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(4), 10000);
					break;
				case 4:
					_antharas.broadcastPacket(new SocialActionPacket(_antharas.getObjectId(), 2));
					// set camera.
					for(Player pc : _players)
						if(pc.getDistance(_antharas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_antharas, 3700, 0, -3, 0, 10000, 0, 0, 0, 0);
						}
						else
							pc.leaveMovieMode();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(5), 200);
					break;
				case 5:
					// set camera.
					for(Player pc : _players)
						if(pc.getDistance(_antharas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_antharas, 1100, 0, -3, 22000, 30000, 0, 0, 0, 0);
						}
						else
							pc.leaveMovieMode();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(6), 10800);
					break;
				case 6:
					// set camera.
					for(Player pc : _players)
						if(pc.getDistance(_antharas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_antharas, 1100, 0, -3, 300, 7000, 0, 0, 0, 0);
						}
						else
							pc.leaveMovieMode();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(7), 7000);
					break;
				case 7:
					// reset camera.
					for(Player pc : _players)
						pc.leaveMovieMode();

					broadcastScreenMessage(NpcString.ANTHARAS_YOU_CANNOT_HOPE_TO_DEFEAT_ME);
					_antharas.broadcastPacket(new PlaySoundPacket(PlaySoundPacket.Type.MUSIC, "BS02_A", 1, _antharas.getObjectId(), _antharas.getLoc()));
					_antharas.setAggroRange(_antharas.getTemplate().aggroRange);
					_antharas.setRunning();
					_antharas.moveToLocation(new Location(179011, 114871, -7704), 0, false);
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);
					break;
				case 8:
					for(Player pc : _players)
						if(pc.getDistance(_antharas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_antharas, 1200, 20, -10, 0, 13000, 0, 0, 0, 0);
						}
						else
							pc.leaveMovieMode();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(9), 13000);
					break;
				case 9:
					for(Player pc : _players)
					{
						pc.leaveMovieMode();
						pc.altOnMagicUse(pc, SkillHolder.getInstance().getSkill(23312, 1));
					}
					broadcastScreenMessage(NpcString.ANTHARAS_THE_EVIL_LAND_DRAGON_ANTHARAS_DEFEATED);
					onAntharasDie();
					break;
			}
		}
	}

	private static class CheckLastAttack extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_state.getState() == EpicBossState.State.ALIVE)
				if(_lastAttackTime + (BossesConfig.ANTHARAS_SLEEP_TIME * 60000) < System.currentTimeMillis())
					sleep();
				else
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000);
		}
	}

	// at end of interval.
	private static class IntervalEnd extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.save();
		}
	}

	private static class onAnnihilated extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			sleep();
		}
	}

	private static void banishForeigners()
	{
		for(Player player : getPlayersInside())
			player.teleToClosestTown();
	}

	private synchronized static void checkAnnihilated()
	{
		if(_onAnnihilatedTask == null && isPlayersAnnihilated())
			_onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5000);
	}

	private static List<Player> getPlayersInside()
	{
		return getZone().getInsidePlayers();
	}

	private static long getRespawnTime()
	{
		return BossesConfig.ANTHARAS_RESPAWN_TIME_PATTERN.next(System.currentTimeMillis());
	}

	public static Zone getZone()
	{
		return _zone;
	}

	private static boolean isPlayersAnnihilated()
	{
		for(Player pc : getPlayersInside())
			if(!pc.isDead())
				return false;
		return true;
	}

	private static void onAntharasDie()
	{
		if(Dying)
			return;

		Dying = true;
		_state.setNextRespawnDate(getRespawnTime());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.save();

		_entryLocked = false;
		_teleCube = NpcUtils.spawnSingle(TELEPORT_CUBE_ID, _teleportCubeLocation);
		Log.add("Antharas died", "bosses");
		Announcements.announceToAll(new ExShowScreenMessage(NpcString.THE_EVIL_LAND_DRAGON_ANTHARAS_HAS_BEEN_DEFEATED_BY_BRAVE_HEROES_, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true));
		TersiManager.spawnTersi();
	}

	private static void setIntervalEndTask()
	{
		setUnspawn();

		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.save();
			return;
		}

		if(!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setNextRespawnDate(getRespawnTime());
			_state.setState(EpicBossState.State.INTERVAL);
			_state.save();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	// clean Antharas's lair.
	private static void setUnspawn()
	{
		// not executed tasks is canceled.
		if(_monsterSpawnTask != null)
		{
			_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = null;
		}
		if(_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if(_socialTask != null)
		{
			_socialTask.cancel(false);
			_socialTask = null;
		}
		if(_moveAtRandomTask != null)
		{
			_moveAtRandomTask.cancel(false);
			_moveAtRandomTask = null;
		}
		if(_sleepCheckTask != null)
		{
			_sleepCheckTask.cancel(false);
			_sleepCheckTask = null;
		}
		if(_onAnnihilatedTask != null)
		{
			_onAnnihilatedTask.cancel(false);
			_onAnnihilatedTask = null;
		}

		// eliminate players.
		banishForeigners();

		if(_antharas != null)
			_antharas.deleteMe();
		for(NpcInstance npc : _spawnedMinions)
			npc.deleteMe();
		if(_teleCube != null)
			_teleCube.deleteMe();

		_entryLocked = false;
	}

	@Override
	public void onInit()
	{
		_state = new EpicBossState(ANTHARAS_STRONG);
		_zone = ReflectionUtils.getZone("[antharas_epic]");

		CharListenerList.addGlobal(DEATH_LISTENER);
		_log.info("AntharasManager: State of Antharas is " + _state.getState() + ".");
		if(!_state.getState().equals(EpicBossState.State.NOTSPAWN))
		{
			setIntervalEndTask();
			_log.info("AntharasManager: Next spawn date of Antharas is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
		}
	}

	private static void sleep()
	{
		setUnspawn();
		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.save();
		}
	}

	public static void setLastAttackTime()
	{
		_lastAttackTime = System.currentTimeMillis();
	}

	// setting Antharas spawn task.
	public synchronized static void setAntharasSpawnTask()
	{
		if(_monsterSpawnTask == null)
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(1), BossesConfig.ANTHARAS_SPAWN_DELAY * 60000);
	}

	public static boolean isSpawnTaskStarted()
	{
		return _monsterSpawnTask != null;
	}

	public static void broadcastScreenMessage(NpcString npcs)
	{
		for(Player p : getPlayersInside())
			p.sendPacket(new ExShowScreenMessage(npcs, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
	}

	public static void addSpawnedMinion(NpcInstance npc)
	{
		_spawnedMinions.add(npc);
	}

	public static boolean isReborned()
	{
		return _state.getState() == EpicBossState.State.NOTSPAWN;
	}

	public static boolean isEntryLocked()
	{
		return _entryLocked;
	}

	public static boolean checkRequiredItems(Player player)
	{
		for(int[] item : BossesConfig.ANTHARAS_ENTERANCE_NECESSARY_ITEMS)
		{
			int itemId = item.length > 0 ? item[0] : 0;
			int itemCount = item.length > 1 ? item[1] : 0;
			if(itemId > 0 && itemCount > 0 && !ItemFunctions.haveItem(player, itemId, itemCount))
				return false;
		}
		return true;
	}

	public static boolean consumeRequiredItems(Player player)
	{
		if(BossesConfig.ANTHARAS_ENTERANCE_CAN_CONSUME_NECESSARY_ITEMS)
		{
			for(int[] item : BossesConfig.ANTHARAS_ENTERANCE_NECESSARY_ITEMS)
			{
				int itemId = item.length > 0 ? item[0] : 0;
				int itemCount = item.length > 1 ? item[1] : 0;
				if(itemId > 0 && itemCount > 0 && !ItemFunctions.deleteItem(player, itemId, itemCount, true))
					return false;
			}
		}
		return true;
	}
}