package instances;

import ai.CrystalGolem;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.*;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

//By Evil_dnk

public class CorallGarden extends Reflection
{
	protected final List<Location> _leftRoutes = new ArrayList<Location>();
	protected final List<Location> _rightRoutes = new ArrayList<Location>();
	protected final List<Location> _raidclones = new ArrayList<Location>();
	private ScheduledFuture<?> _waveMovementTask;
	private static final int[] Michael = {25799, 26114, 26115, 26116};
	private static final int Golem1 = 19013;
	private static final int Golem2 = 19014;
	private static final int Doorrb = 24240026;
	private boolean firstgolem = false;
	private boolean secondgolem = false;
	private int countoffear1 = 0;
	private int countoffear2 = 0;
	private static final int[] ChestOfDim = {19576, 19577, 19578, 19579, 19580, 19581, 19582, 19583, 19584, 19585, 19586, 19587};

	private long _savedTime;

	private Location golem1Loc = new Location(139496, 217272, -11805);
	private Location golem2Loc = new Location(143000, 217272, -11805);
    private Location RBLoc = new Location(144312, 220024, -11846, 32767);

	NpcInstance golem1 = null;
	NpcInstance golem2 = null;
	NpcInstance rb = null;
	private int _stage = 0;

	protected Location _monsterMoveNearestPointLoc;

	private DeathListener _deathListener = new DeathListener();
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		_savedTime = System.currentTimeMillis();
		golem1 = addSpawnWithoutRespawn(Golem1, golem1Loc, 0);
		golem2 = addSpawnWithoutRespawn(Golem2, golem2Loc, 0);

		_leftRoutes.add(new Location(140104, 217256, -11800));
		_leftRoutes.add(new Location(140232, 216904, -11800));
		_leftRoutes.add(new Location(140328, 216696, -11800));
		_leftRoutes.add(new Location(140456, 216488, -11800));
		_leftRoutes.add(new Location(140648, 216280, -11800));
		_leftRoutes.add(new Location(140872, 216104, -11800));
		_leftRoutes.add(new Location(141128, 216008, -11800));
		_leftRoutes.add(new Location(141384, 216008, -11800));
		_leftRoutes.add(new Location(141640, 216104, -11800));
		_leftRoutes.add(new Location(141864, 216264, -11800));
		_leftRoutes.add(new Location(142056, 216472, -11800));
		_leftRoutes.add(new Location(142200, 216680, -11800));
		_leftRoutes.add(new Location(142280, 216904, -11800));
		_leftRoutes.add(new Location(142344, 217112, -11800));
		_leftRoutes.add(new Location(142408, 217288, -11800));
		_leftRoutes.add(new Location(142664, 217272, -11800));
		_leftRoutes.add(new Location(143016, 217272, -11800));

		_rightRoutes.add(new Location(142408, 217256, -11800));
		_rightRoutes.add(new Location(142296, 217592, -11800));
		_rightRoutes.add(new Location(142184, 217832, -11800));
		_rightRoutes.add(new Location(142040, 218040, -11800));
		_rightRoutes.add(new Location(141848, 218248, -11800));
		_rightRoutes.add(new Location(141640, 218408, -11800));
		_rightRoutes.add(new Location(141384, 218504, -11800));
		_rightRoutes.add(new Location(141128, 218520, -11800));
		_rightRoutes.add(new Location(140984, 218472, -11800));
		_rightRoutes.add(new Location(140872, 218408, -11800));
		_rightRoutes.add(new Location(140648, 218232, -11800));
		_rightRoutes.add(new Location(140472, 218040, -11800));
		_rightRoutes.add(new Location(140328, 217832, -11800));
		_rightRoutes.add(new Location(140232, 217608, -11800));
		_rightRoutes.add(new Location(140104, 217240, -11800));
		_rightRoutes.add(new Location(139832, 217272, -11800));
		_rightRoutes.add(new Location(139496, 217272, -11800));

		_raidclones.add(new Location(144184, 219576, -11824));
		_raidclones.add(new Location(143944, 219752, -11825));
		_raidclones.add(new Location(143848, 220024, -11825));
		_raidclones.add(new Location(143928, 220296, -11824));
		_raidclones.add(new Location(144168, 220472, -11824));
		_raidclones.add(new Location(144456, 220456, -11824));
		_raidclones.add(new Location(144680, 220296, -11824));
		_raidclones.add(new Location(144776, 220024, -11824));
		_raidclones.add(new Location(144680, 219752, -11824));
		_raidclones.add(new Location(144440, 219576, -11825));

		_waveMovementTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MonsterMovementTask(), 3000L, 3000L);

	}

	@Override
	public void onPlayerEnter(Player player) 
	{
        super.onPlayerEnter(player);
		player.sendPacket(new ExSendUIEventPacket(player, 0, 1, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));
    }

	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
		player.sendPacket(new ExSendUIEventPacket(player, 1, 1, 0, 0));
	}	
	
	private class DeathListener implements OnDeathListener
	{
		@Override
        public void onDeath(Creature self, Creature killer) 
		{
            if (self.isNpc() && ArrayUtils.contains(Michael, self.getNpcId()))
            {
		       clearReflection(5, true);
		       addSpawnWithoutRespawn(Rnd.get(ChestOfDim), RBLoc, 0);
	           rb.removeListener(_deathListener);
            }
		}
	}

	public int getStatus()
	{
		return _stage;
	}

	private void setStatus(int set)
	{
		 _stage = set;
	}

	private class MonsterMovementTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(golem1 != null)
			{
				if(golem1.getFollowTarget() != null && (golem1.getFollowTarget().getTarget() != golem1 || golem1.getDistance(golem1.getFollowTarget()) > 200))
				{
					CrystalGolem ai = (CrystalGolem) golem1.getAI();
					golem1.setRunning();
					ai.addTaskMove(Location.findPointToStay(golem1.getLoc(), 100, 100, golem1.getGeoIndex()), true);
					countoffear1++;
					if(countoffear1 > 4)
					{
						golem1.deleteMe();
						golem1 = addSpawnWithoutRespawn(Golem1, golem1Loc, 0);
						countoffear1 = 0;
					}
				}
				else if(golem1.getFollowTarget() != null && golem1.getFollowTarget().getTarget() == golem1 && golem1.getDistance(golem1.getFollowTarget()) < 200)
				{
					if (getStatus() == 0)
					{
						if (!golem1.isMoving && golem1.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST && golem1.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
						{
							List<Location> routes;
							if (golem1.getNpcId() == Golem1)
								routes = _leftRoutes;
							else
								routes = _rightRoutes;
							boolean takeNextRoute = false;
							Location nearestLoc = null;
							Double nearestLocDistance = null;
							Location npcLoc = golem1.getLoc();
							for (Location loc : routes)
							{
								if (takeNextRoute)
								{
									nearestLoc = loc;
									break;
								}
								double distance = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
								if (distance < 100.0D)
									takeNextRoute = true;
								else if (nearestLoc == null)
								{
									nearestLoc = loc;
									nearestLocDistance = Double.valueOf(distance);
								}
								else
								{
									double currentLocDistance = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
									if (currentLocDistance <= nearestLocDistance.doubleValue())
									{
										nearestLoc = loc;
										nearestLocDistance = Double.valueOf(currentLocDistance);
									}
								}
							}
							if (nearestLoc != null)
							{
								if(nearestLoc == routes.get(16))
								{
									for(Player player : getPlayers())
									{
										player.sendPacket(new ExShowScreenMessage(NpcString.GOLEM_ENTERED_THE_REQUIRED_ZONE__, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
									}
									golem1.setFollowTarget(null);
									firstgolem = true;
									ThreadPoolManager.getInstance().schedule(() ->
									{
										golem1.deleteMe();
									}, 5000L);

									if(firstgolem && secondgolem && getStatus() == 0)
									{
										spawnrb();
										if(_waveMovementTask != null)
											_waveMovementTask.cancel(true);
									}
								}
								Creature ftar = golem1.getFollowTarget();
								nearestLoc.setX(nearestLoc.getX());
								nearestLoc.setY(nearestLoc.getY());
								golem1.setWalking();
								golem1.setBusy(true);
								CrystalGolem ai = (CrystalGolem) golem1.getAI();
								golem1.setFollowTarget(ftar);
								ai.addTaskMove(Location.findPointToStay(nearestLoc, 1, 1, golem1.getGeoIndex()), true);
							}
						}
					}
				}
			}
			if(golem2 != null)
			{
				if(golem2.getFollowTarget() != null && golem2.getFollowTarget().getTarget() != golem2 && golem2.getDistance(golem2.getFollowTarget()) > 200)
				{
					CrystalGolem ai = (CrystalGolem) golem2.getAI();
					ai.addTaskMove(Location.findPointToStay(golem2.getLoc(), 100, 100, golem2.getGeoIndex()), true);
					golem2.setRunning();
					countoffear2++;
					if(countoffear2 > 4)
					{
						golem2.deleteMe();
						golem2 = addSpawnWithoutRespawn(Golem1, golem1Loc, 0);
						countoffear2 = 0;
					}
				}
				else if(golem2.getFollowTarget() != null && golem2.getFollowTarget().getTarget() == golem2 && golem2.getDistance(golem2.getFollowTarget()) < 200)
				{

					if (getStatus() == 0)
					{
						if (!golem2.isMoving && golem2.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST && golem2.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
						{
							List<Location> routes;
							if (golem2.getNpcId() == Golem1)
								routes = _leftRoutes;
							else
								routes = _rightRoutes;
							boolean takeNextRoute = false;
							Location nearestLoc = null;
							Double nearestLocDistance = null;
							Location npcLoc = golem2.getLoc();
							for (Location loc : routes)
							{
								if (takeNextRoute)
								{
									nearestLoc = loc;
									break;
								}
								double distance = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
								if (distance < 100.0D)
									takeNextRoute = true;
								else if (nearestLoc == null)
								{
									nearestLoc = loc;
									nearestLocDistance = Double.valueOf(distance);
								}
								else
								{
									double currentLocDistance = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
									if (currentLocDistance <= nearestLocDistance.doubleValue())
									{
										nearestLoc = loc;
										nearestLocDistance = Double.valueOf(currentLocDistance);
									}
								}
							}
							if (nearestLoc != null)
							{
								if(nearestLoc == routes.get(16))
								{
									for(Player player : getPlayers())
									{
										player.sendPacket(new ExShowScreenMessage(NpcString.GOLEM_ENTERED_THE_REQUIRED_ZONE__, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
									}
									ThreadPoolManager.getInstance().schedule(() ->
									{
										golem2.deleteMe();
									}, 5000L);

									golem2.setFollowTarget(null);
									secondgolem = true;
									if(firstgolem && secondgolem && getStatus() == 0)
									{
										spawnrb();
										if(_waveMovementTask != null)
											_waveMovementTask.cancel(true);
									}
								}
								Creature ftar = golem2.getFollowTarget();
								nearestLoc.setX(nearestLoc.getX());
								nearestLoc.setY(nearestLoc.getY());
								golem2.setWalking();
								golem2.setBusy(true);
								CrystalGolem ai = (CrystalGolem) golem2.getAI();
								golem2.setFollowTarget(ftar);
								ai.addTaskMove(Location.findPointToStay(nearestLoc, 1, 1, golem2.getGeoIndex()), true);
							}
						}
					}
				}
			}
		}
	}
	private void spawnrb()
	{
		if(rb == null)
		{
			openDoor(Doorrb);
			rb = addSpawnWithoutRespawn(Rnd.get(Michael), RBLoc, 0);
			rb.addListener(_deathListener);
			setStatus(1);
		}
	}

	public void deleteMinions(int npcId)
	{
		for(NpcInstance mob : getAllByNpcId(npcId, true))
			mob.deleteMe();
	}

	public void spawnMinions(int npcId)
	{
		List<Player> players = new ArrayList<Player>();
		for(Player pl : World.getAroundPlayers(rb, 1000, 500))
			players.add(pl);

		List<NpcInstance> minions = new ArrayList<NpcInstance>();
		for(int i = 0; i <= 9; i++)
		{
			NpcInstance minion = addSpawnWithoutRespawn(npcId, _raidclones.get(i), 0);
			if(minion != null)
				minions.add(minion);
		}

		if(!players.isEmpty())
		{
			for(NpcInstance minion : minions)
			{
				DefaultAI ai = (DefaultAI) minion.getAI();
				minion.setRunning();
				minion.getAggroList().addDamageHate(Rnd.get(players), 10000, 0);
				ai.addTaskMove(Location.findPointToStay(Rnd.get(players).getLoc(), 1, 1, minion.getGeoIndex()), true);
			}
		}
	}
}