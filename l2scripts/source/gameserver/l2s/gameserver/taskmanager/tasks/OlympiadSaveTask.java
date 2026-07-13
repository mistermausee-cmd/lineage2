package l2s.gameserver.taskmanager.tasks;

import l2s.gameserver.model.entity.olympiad.OlympiadDatabase;


public class OlympiadSaveTask extends AutomaticTask
{
	public OlympiadSaveTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		

		
		OlympiadDatabase.save();
		
	}

	@Override
	public long reCalcTime(boolean start)
	{
		return System.currentTimeMillis() + 600000L;
	}
}