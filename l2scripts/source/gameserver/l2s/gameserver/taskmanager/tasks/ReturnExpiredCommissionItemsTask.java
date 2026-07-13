package l2s.gameserver.taskmanager.tasks;

import l2s.gameserver.instancemanager.CommissionManager;


public class ReturnExpiredCommissionItemsTask extends AutomaticTask
{
	public ReturnExpiredCommissionItemsTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		CommissionManager.getInstance().returnExpiredItems();
	}

	@Override
	public long reCalcTime(boolean start)
	{
		
		return System.currentTimeMillis() + 60000L;
	}
}