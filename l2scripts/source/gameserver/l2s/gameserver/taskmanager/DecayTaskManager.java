package l2s.gameserver.taskmanager;

import java.util.concurrent.Future;

import l2s.commons.threading.SteppingRunnableQueueManager;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Creature;


public class DecayTaskManager extends SteppingRunnableQueueManager
{
	private static final DecayTaskManager _instance = new DecayTaskManager();

	public static final DecayTaskManager getInstance()
	{
		return _instance;
	}

	private DecayTaskManager()
	{
		super(500L);

		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 500L, 500L);

		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> DecayTaskManager.this.purge(), 60000L, 60000L);
	}

	public Future<?> addDecayTask(final Creature actor, long delay)
	{
		return schedule(() -> actor.doDecay(), delay);
	}
}