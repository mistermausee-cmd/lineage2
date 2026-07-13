package l2s.gameserver.instancemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Zone;
import l2s.gameserver.utils.ReflectionUtils;
import l2s.gameserver.utils.Util;


public class SoHManager
{
	private static final Logger _log = LoggerFactory.getLogger(SoHManager.class);
	private static SoHManager _instance;
	private static final String SPAWN_GROUP_1 = "seed_of_hellfire_all_1";
	private static final String SPAWN_GROUP_2 = "seed_of_hellfire_all_2";
	private static final long SOH_OPEN_TIME = 24 * 60 * 60 * 1000L;
	private static Zone _zone;

	public static SoHManager getInstance()
	{
		if (_instance == null)
			_instance = new SoHManager();
		return _instance;
	}

	public SoHManager()
	{
		_log.info("Seed of Hellfire Manager: Loaded.");
		_zone = ReflectionUtils.getZone("[inner_hellfire01]");
		checkStageAndSpawn();
		if(!isSeedOpen())
			openSeed(getOpenedTime());
	}

	private static Zone getZone()
	{
		return _zone;
	}

	public static long getOpenedTime()
	{
		if(getCurrentStage() != 2)
			return 0;
		return ServerVariables.getLong("SoH_opened", 0) * 1000L - System.currentTimeMillis();
	}

	public static boolean isSeedOpen()
	{
		return getOpenedTime() > 0;
	}

	public static void setCurrentStage(int stage)
	{
		if(getCurrentStage() == stage)
			return;

		if(stage == 2)
			openSeed(SOH_OPEN_TIME);
		else if(isSeedOpen())
			closeSeed();

		ServerVariables.set("SoH_stage", stage);
		checkStageAndSpawn();
		_log.info("Seed of Hellfire Manager: Set to stage " + stage);
	}

	public static int getCurrentStage()
	{
		return ServerVariables.getInt("SoH_stage", 1);
	}

	public static void checkStageAndSpawn()
	{
		SpawnManager.getInstance().spawn(SPAWN_GROUP_1);
	}

	public static void openSeed(long timelimit)
	{
		if(timelimit <= 0)
			return;

		ServerVariables.unset("Tauti_kills");
		ServerVariables.set("SoH_opened", (System.currentTimeMillis() + timelimit) / 1000L);
		_log.info("Seed of Hellfire Manager: Opening the seed for " + Util.formatTime((int) timelimit / 1000));
		SpawnManager.getInstance().spawn(SPAWN_GROUP_1);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			closeSeed();
			setCurrentStage(1);
		}, timelimit);
	}

	public static void closeSeed()
	{
		_log.info("Seed of Hellfire Manager: Closing the seed.");
		ServerVariables.unset("SoH_opened");
		SpawnManager.getInstance().despawn(SPAWN_GROUP_1);

		for(Playable p : getZone().getInsidePlayables())
			p.teleToLocation(getZone().getRestartPoints().get(0));
	}
}