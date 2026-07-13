package spawns;

import java.util.concurrent.ScheduledFuture;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.SpawnManager;

//By Evil_dnk

public class TersiManager
{
	private static final long DESPAWN_DELAY = 2 * 60 * 60 * 1000L; // Деспавнить Терси через 2 часа.

	private static ScheduledFuture<?> _despawnTask;

	public static void spawnTersi()
	{
		if(_despawnTask != null)
		{
			_despawnTask.cancel(true);
			_despawnTask = null;
		}
		else
			SpawnManager.getInstance().spawn("gerold_tersi");

		_despawnTask = ThreadPoolManager.getInstance().schedule(() -> SpawnManager.getInstance().despawn("gerold_tersi"), DESPAWN_DELAY);
	}
}