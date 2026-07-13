package bosses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ai.beleth.BelethDarion;
import ai.beleth.LeonelaH;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.BossInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bosses.EpicBossState.State;

//By Evil_dnk

public class NewBelethManager implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(NewBelethManager.class);
	public static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
				checkAnnihilated();
			else if(self.isNpc() && (self.getNpcId() == DARION || self.getNpcId() == BELEF_FINAL))
			{
				onBelethDie();;
				_leonela = NpcUtils.spawnSingle(LEONELAHFINISH, LEONELA_FINISH, 3600000);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					Functions.npcSay(_leonela, NpcString.YOU_HAVE_DEFEATED_THE_FORCES_OF_EVIL_WHILE_I_WAS_GATHERING_REINFORCEMENTS_I_WISH_TO_GIVE_YOU_A_REWARD_SO_PLEASE_COME_HERE);

				}, 1500L);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					Functions.npcSay(_leonela, NpcString.YOU_HAVE_DEFEATED_THE_FORCES_OF_EVIL_WHILE_I_WAS_GATHERING_REINFORCEMENTS_I_WISH_TO_GIVE_YOU_A_REWARD_SO_PLEASE_COME_HERE);
					for(Player pc : getPlayersInside())
					{
						if(pc != null)
							pc.specialCamera(_leonela, -100, 200, 0, 0, 8000, 0, 1, -100, 0);
					}
				}, 5000L);
			}
		}
	}

	// Constants
	private static final int DARION = 29246;
	private static final int BELEF_START = 29244;
	private static final int BELEF_MAIN = 29245;
	private static final int BELEF_FINAL = 29250;
	private final static int CHERVOTOCHINA_NPC = 19518;

	private static final int LEONELA = 33898;
	private static final int LEONELAHFINISH = 33899;

	private static final Location TELEPORT_POSITION = new Location(-18152, 245912, -865);
	private static final Location RETUR_POSITION = new Location(-27960, 254200, -2230);

	private static final Location LEONELA_START = new Location(-18408, 246664, -865);
	private static final Location LEONELA_FINISH = new Location(-17544, 245960, -856);

	private static final Location _rb1spawn = new Location(-16869, 245732, -840, 32767);
	private static final Location _rb2spawn = new Location(-16872, 245928, -840, 32767);
	private static final Location _rb3spawn = new Location(-16904, 246168, -840, 32767);

	private static final Location _helper1spawn = new Location(-16920, 245736, -865, 32767);
	private static final Location _helper2spawn = new Location(-16936, 245928, -865, 32767);
	private static final Location _helper3spawn = new Location(-16952, 246216, -865, 32767);

	private static final OnDeathListener DEATH_LISTENER = new DeathListener();

	// Models
	private static BossInstance _rbmain;
	private static BossInstance _rbend;
	private static BossInstance _rb1;
	private static BossInstance _rb2;
	private static BossInstance _rb3;

	private static NpcInstance _leonela;
	private static NpcInstance _leonelahelper;

	// tasks.
	private static ScheduledFuture<?> _monsterSpawnTask;
	private static ScheduledFuture<?> _sleepCheckTask;
	private static ScheduledFuture<?> _onAnnihilatedTask;
	private static ScheduledFuture<?> _intervalEndTask;

	// Vars
	private static EpicBossState _state;
	private static Zone _zone;
	private static long _lastAttackTime = 0;
	private static final int FWA_LIMITUNTILSLEEP = 15 * 60000;
	private static final int FWA_FIXINTERVALOFBELETH = 7 * 24 * 60 * 60000;
	private static final int FWA_APPTIMEOFBELETH = 6000;
	private static boolean Dying = false;
	private static boolean _entryLocked = false;
	private static int BelethState = 0;
	public static int countOfHunt = 0;
	private static List<NpcInstance> _spawnedClones = new ArrayList<NpcInstance>();

	private static class BelethSpawn extends RunnableImpl
	{
		private int _taskId = 0;
		private List<Player> _players = getPlayersInside();

		BelethSpawn(int taskId)
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
					_state.setRespawnDate(Rnd.get(FWA_FIXINTERVALOFBELETH, FWA_FIXINTERVALOFBELETH));
					_rb1 = (BossInstance) NpcUtils.spawnSingle(BELEF_START, _rb1spawn);
					_rb2 = (BossInstance) NpcUtils.spawnSingle(BELEF_START, _rb2spawn);
					_rb3 = (BossInstance) NpcUtils.spawnSingle(BELEF_START, _rb3spawn);
					setCountOfHunt(0);
					setBelethStage(0);
					_leonela = NpcUtils.spawnSingle(LEONELA, LEONELA_START);
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);

					if(Rnd.chance(30))
					{
						BelethDarion ai1 = (BelethDarion) _rb1.getAI();
						ai1.fakeornot(true);
					}
					else if(Rnd.chance(30))
					{
						BelethDarion ai2 = (BelethDarion) _rb2.getAI();
						ai2.fakeornot(true);
					}
					else
					{
						BelethDarion ai3 = (BelethDarion) _rb3.getAI();
						ai3.fakeornot(true);
					}
					for(Player player : getPlayersInside())
					{
						if(player.getVar("requestbeleth") != null)
							player.unsetVar("requestbeleth");
						if(player.getVar("requestreward") != null)
							player.unsetVar("requestreward");
					}
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

	public static void startMainform(boolean fake)
	{
		if(!fake)
		{
			if (_rb1 != null)
				_rb1.deleteMe();
			if (_rb2 != null)
				_rb2.deleteMe();
			if (_rb3 != null)
				_rb3.deleteMe();
			if (_leonelahelper != null)
				_leonelahelper.deleteMe();
			if (_leonela != null)
				_leonela.deleteMe();

			if(Rnd.chance(30))
			{
				_rbmain = (BossInstance) NpcUtils.spawnSingle(BELEF_MAIN, LEONELA_FINISH);
				 broadcastScreenMessage(NpcString.valueOf(14211716));// Beleth
			}
			else
			{
				_rbmain = (BossInstance) NpcUtils.spawnSingle(DARION, LEONELA_FINISH);
				broadcastScreenMessage(NpcString.valueOf(14211717));// Darion
			}
		}
	}

	public static void startLastForm(Location loc)
	{
		if(_rbmain != null)
			_rbmain.deleteMe();

		_rbend = (BossInstance) NpcUtils.spawnSingle(BELEF_FINAL, loc);
		_rbend.setCurrentHp(_rbend.getMaxHp() * 0.1, false, true);
	}

	public static int getBelethStage()
	{
		return BelethState;
	}

	public static void setBelethStage(int stage)
	{
		BelethState = stage;
	}

	public static void setCountOfHunt(int stage)
	{
		countOfHunt = stage;
		if(countOfHunt >= 50 && _leonelahelper == null)
			spawnHelper();
	}

	private static void spawnHelper()
	{
		BelethDarion ai1 = (BelethDarion) _rb1.getAI();
		BelethDarion ai2 = (BelethDarion) _rb2.getAI();
		BelethDarion ai3 = (BelethDarion) _rb3.getAI();

		if(ai1.getfakeornot())
			_leonelahelper = NpcUtils.spawnSingle(LEONELA, _helper1spawn);
		else if(ai2.getfakeornot())
			_leonelahelper = NpcUtils.spawnSingle(LEONELA, _helper2spawn);
		else if(ai3.getfakeornot())
			_leonelahelper = NpcUtils.spawnSingle(LEONELA, _helper3spawn);

		_leonela.deleteMe();
		_leonelahelper.setBusy(true);
		_leonelahelper.setAI(new LeonelaH(_leonelahelper));
	}

	public static int getCountOfHunt()
	{
		return countOfHunt;
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
		return (int) (Config.ALT_RAID_RESPAWN_MULTIPLIER * FWA_FIXINTERVALOFBELETH);
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

	private static void onBelethDie()
	{
		if(Dying)
			return;

		Dying = true;
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.save();
		_entryLocked = false;
		setCountOfHunt(0);
		setBelethStage(3);
		Log.add("Beleth died", "bosses");
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

	private static void setUnspawn()
	{
		// eliminate players.
		banishForeigners();
		setBelethStage(0);
		setCountOfHunt(0);

		if(_rbmain != null)
			_rbmain.deleteMe();
		if(_rb1 != null)
			_rb1.deleteMe();
		if(_rb2 != null)
			_rb2.deleteMe();
		if(_rb3 != null)
			_rb3.deleteMe();
		if(_rbend != null)
			_rbend.deleteMe();
		if(_rbmain != null)
			_rbmain.deleteMe();
		if(_leonelahelper != null)
			_leonelahelper.deleteMe();
		if(_leonela != null)
			_leonela.deleteMe();

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
		_state = new EpicBossState(BELEF_FINAL);
		_zone = ReflectionUtils.getZone("[new_beleth_zone]");

		CharListenerList.addGlobal(DEATH_LISTENER);

		_log.info("BelethManager: State of New Beleth is " + _state.getState() + ".");

		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.save();
		}
		else if(_state.getState().equals(EpicBossState.State.INTERVAL) || _state.getState().equals(EpicBossState.State.DEAD))
		{
			setIntervalEndTask();
			_log.info("BelethManager: Next spawn date of New Beleth is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
		}
	}

	public static void sleep()
	{
		setUnspawn();
		_state.setState(EpicBossState.State.NOTSPAWN);
		_state.save();
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

	public synchronized static void setBelethSpawnTask()
	{
		if(_monsterSpawnTask == null)
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new BelethSpawn(1), FWA_APPTIMEOFBELETH);
		_entryLocked = true;
	}

	public static void broadcastScreenMessage(NpcString npcs)
	{
		for(Player p : getPlayersInside())
			p.sendPacket(new ExShowScreenMessage(npcs, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
	}

	public static void enterBeleth(Player ccleader)
	{
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
				ccleader.sendMessage("Beleth is still reborning. You cannot invade the nest now");
				return;
			}
			if(_state.getState() == EpicBossState.State.ALIVE)
			{
				ccleader.sendMessage("Beleth has already been reborned and is being attacked. The entrance is sealed.");
			}
			if(cc.getChannelLeader() != ccleader)
			{
				ccleader.sendPacket(SystemMsg.ONLY_THE_ALLIANCE_CHANNEL_LEADER_CAN_ATTEMPT_ENTRY);
				return;
			}
			if(cc.getMemberCount() > 350)
			{
				ccleader.sendMessage("The max of 350 players can invade the Beleth");
				return;
			}

			// checking every member of CC for the proper conditions
			for(Player p : cc)
				if(p.isDead() || p.isFlying() || p.isCursedWeaponEquipped() || !p.isInRange(ccleader, 500) || p.getLevel() < 97)
				{
					ccleader.sendMessage("Command Channel member " + p.getName() + " doesn't meet the requirements to enter");
					return;
				}

			for(Player p : cc)
			{
				p.teleToLocation(TELEPORT_POSITION);

			}
			setBelethSpawnTask();
		}
	}

	public static void leaveTheCastle(Player player)
	{
		player.teleToLocation(RETUR_POSITION);
	}

	@Override
	public void onInit()
	{
		init();
	}

	public static void luckyman(Player player)
	{
		for(Player p : getPlayersInside())
		{
			p.sendPacket(new ExShowScreenMessage(NpcString.LEONA_BLACKBIRD_GAVE_BELETHS_RING_AS_A_GIFT_TO_S1, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
		}
		ItemFunctions.addItem(player, 10314, 1, true);
	}
}