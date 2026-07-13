package l2s.gameserver.taskmanager.tasks;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;


public abstract class AutomaticTask extends RunnableImpl
{
	public AutomaticTask()
	{
		init(true);
	}

	public abstract void doTask() throws Exception;

	public abstract long reCalcTime(boolean start);

	public void init(boolean start)
	{
		ThreadPoolManager.getInstance().schedule(this, reCalcTime(start) - System.currentTimeMillis());
	}

	@Override
	public void runImpl() throws Exception
	{
		try
		{
			doTask();
		}
		finally
		{
			init(false);
		}
	}
}