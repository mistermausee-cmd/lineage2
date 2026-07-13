package bosses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javolution.io.Struct;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.BossInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;
import l2s.gameserver.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bosses.EpicBossState.State;

//By Evil_dnk
public class KelbimManager implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(KelbimManager.class);
	private static Zone _gazzZone = ReflectionUtils.getZone("[kelbin_zone]");
	public static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
				checkAnnihilated();
			else if(self.isNpc() && self.getNpcId() == KELBIM)
			{
				setZoneGazz(false);
				ThreadPoolManager.getInstance().schedule(new KelbimSpawn(4), 10);
				for(Player player : World.getAroundPlayers(self, 2000, 500))
				{
					if(player.isPlayer())
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.KELBIM_HAS_BEEN_DEFEATED_BY_THE_COURAGEOUS_WARRIORS, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
					}
				}
			}
		}
	}

	// Constants
	private static final int KELBIM = 26124;
	private static final int ASTATIN = 26125;
	private static final Location TELEPORT_POSITION = new Location(-54552, 58648, -292);
	private static final Location RETUR_POSITION = new Location(-55512, 55912, -1972);
	private static final Location _KelbimLocation = new Location(-56264, 60680, -288, 56296);

	private static final OnDeathListener DEATH_LISTENER = new DeathListener();

	// Models
	private static BossInstance _kelbim;
	private static NpcInstance _astatin;

	// tasks.
	private static ScheduledFuture<?> _monsterSpawnTask;
	private static ScheduledFuture<?> _socialTask;
	private static ScheduledFuture<?> _sleepCheckTask;
	private static ScheduledFuture<?> _onAnnihilatedTask;
	private static ScheduledFuture<?> _intervalEndTask;

	// Vars
	private static EpicBossState _state;
	private static Zone _zone;
	private static long _lastAttackTime = 0;
	private static final int FWA_LIMITUNTILSLEEP = 15 * 60000;
	private static final int FWA_FIXINTERVALOFKELBIM = 7 * 24 * 60 * 60000; // 7 суток +-
	private static final int FWA_APPTIMEOFKELBIM = 1 * 60000; // 2 минут ожидание перед респом
	private static boolean Dying = false;
	private static boolean _entryLocked = false;
	private static int KelbimState = 0;
	private static List<NpcInstance> _spawnedMinions = new ArrayList<NpcInstance>();

	private static class KelbimSpawn extends RunnableImpl
	{
		private int _taskId = 0;
		private List<Player> _players = getPlayersInside();

		KelbimSpawn(int taskId)
		{
			_taskId = taskId;
		}

		@Override
		public void runImpl()
		{
			switch(_taskId)
			{
				case 1:
					_state.setState(EpicBossState.State.ALIVE);
					_state.save();
					_socialTask = ThreadPoolManager.getInstance().schedule(new KelbimSpawn(2), 2000);
					_state.setRespawnDate(Rnd.get(FWA_FIXINTERVALOFKELBIM, FWA_FIXINTERVALOFKELBIM));
					break;
				case 2:
					for(Player pc : _players)
					{
						pc.startScenePlayer(SceneMovie.SC_KELBIM_OPENING);
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new KelbimSpawn(3), 20000);
					break;
				case 3:
					_kelbim = (BossInstance) NpcUtils.spawnSingle(KELBIM, _KelbimLocation);
					if(_kelbim != null)
					{
						List<NpcInstance> _around = _kelbim.getAroundNpc(1000, 300);
						if(_around != null && !_around.isEmpty())
						{
							for(NpcInstance npc : _around)
							{
								if (npc.getNpcId() == ASTATIN)
									_astatin = npc;
							}
						}
					}
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);
					break;
				case 4:
					broadcastScreenMessage(NpcString.valueOf(1802948));
					onKelbimDie();
					if(_astatin != null)
						_astatin.setNpcState(0);
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
				if(_lastAttackTime + FWA_LIMITUNTILSLEEP < System.currentTimeMillis())
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

	public static int getKelbimStage()
	{
		return KelbimState;
	}

	public static void setKelbimStage(int stage)
	{
		if(_astatin != null)
			_astatin.setNpcState(stage);
		if(_kelbim != null)
			_kelbim.setNpcState(stage);
		KelbimState = stage;
	}

	private static void banishForeigners()
	{
		for(Player player : getPlayersInside())
			player.teleToLocation(RETUR_POSITION);
	}

	private synchronized static void checkAnnihilated()
	{
		if(_onAnnihilatedTask == null && isPlayersAnnihilated())
			_onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5 * 60000);
	}

	public static List<Player> getPlayersInside()
	{
		return getZone().getInsidePlayers();
	}

	private static int getRespawnInterval()
	{
		return (int) (Config.ALT_RAID_RESPAWN_MULTIPLIER * FWA_FIXINTERVALOFKELBIM);
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

	private static void onKelbimDie()
	{
		if(Dying)
			return;

		Dying = true;
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.save();

		_entryLocked = false;
		Log.add("Kelbim died", "bosses");
	}

	public static void addSpawnedMinion(NpcInstance npc)
	{
		_spawnedMinions.add(npc);
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
			_state.setRespawnDate(getRespawnInterval());
			_state.setState(EpicBossState.State.INTERVAL);
			_state.save();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	// clean Kelbim's lair.
	private static void setUnspawn()
	{
		// eliminate players.
		banishForeigners();

		if(_kelbim != null)
			_kelbim.deleteMe();
		for(NpcInstance npc : _spawnedMinions)
			npc.deleteMe();
		_entryLocked = false;

		// not executed tasks is canceled.
		if(_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
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
		if(_monsterSpawnTask != null)
		{
			_monsterSpawnTask.cancel(true);
			_monsterSpawnTask = null;
		}
	}

	private void init()
	{
		_state = new EpicBossState(KELBIM);
		_zone = ReflectionUtils.getZone("[asatin_zone]");

		CharListenerList.addGlobal(DEATH_LISTENER);
		_log.info("KelbimManager: State of Kelbim is " + _state.getState() + ".");
		if(!_state.getState().equals(EpicBossState.State.NOTSPAWN))
			setIntervalEndTask();

		_log.info("KelbimManager: Next spawn date of Kelbim is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
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
		if(_onAnnihilatedTask != null)
		{
			_onAnnihilatedTask.cancel(true);
			_onAnnihilatedTask = null;
		}
		_lastAttackTime = System.currentTimeMillis();
	}

	// setting Kelbim spawn task.
	public synchronized static void setKelbimSpawnTask()
	{
		if(_monsterSpawnTask == null)
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new KelbimSpawn(1), FWA_APPTIMEOFKELBIM);
		_entryLocked = true;
	}

	public static void broadcastScreenMessage(NpcString npcs)
	{
		for(Player p : getPlayersInside())
			p.sendPacket(new ExShowScreenMessage(npcs, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
	}

	public static void enterTheCastle(Player ccleader)
	{
		if(ccleader == null)
			return;

		if(ccleader.getParty() == null || !ccleader.getParty().isInCommandChannel())
		{
			ccleader.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL);
			return;
		}
		CommandChannel cc = ccleader.getParty().getCommandChannel();

		if(_state.getState() != EpicBossState.State.NOTSPAWN)
		{
			ccleader.sendMessage("Kelbim is still reborning. You cannot invade the nest now");
			return;
		}
		if(_state.getState() == EpicBossState.State.ALIVE)
		{

			if(getKelbimStage() >= 2 && (!ccleader.isDead() || !ccleader.isFlying() || !ccleader.isCursedWeaponEquipped() || ccleader.getLevel() >= 100))
			{
				ccleader.teleToLocation(TELEPORT_POSITION);
				return;
			}
			else
			{
				ccleader.sendMessage("Kelbim has already been reborned and is being attacked. The entrance is sealed.");
				return;
			}
		}
		if(cc.getChannelLeader() != ccleader)
		{
			ccleader.sendPacket(SystemMsg.ONLY_THE_ALLIANCE_CHANNEL_LEADER_CAN_ATTEMPT_ENTRY);
			return;
		}
		if(cc.getMemberCount() > 35)
		{
			ccleader.sendMessage("The maxim of 35 players can invade the Kelbim Castle");
			return;
		}
		if(cc.getMemberCount() < 21)
		{
			ccleader.sendMessage("The minimum of 21 players can invade the Kelbim Castle");
			return;
		}
		// checking every member of CC for the proper conditions
		for(Player p : cc)
			if(p.isDead() || p.isFlying() || p.isCursedWeaponEquipped() || !p.isInRange(ccleader, 500) || p.getLevel() < 100)
			{
				ccleader.sendMessage("Command Channel member " + p.getName() + " doesn't meet the requirements to enter the Castle");
				return;
			}

		for(Player p : cc)
			p.teleToLocation(TELEPORT_POSITION);
		setKelbimSpawnTask();
	}

	public static void leaveTheCastle(Player player)
	{
		player.teleToLocation(RETUR_POSITION);
	}
	public static void setZoneGazz(boolean value)
	{
		_gazzZone.setActive(value);
	}

	@Override
	public void onInit()
	{
		init();
	}


}