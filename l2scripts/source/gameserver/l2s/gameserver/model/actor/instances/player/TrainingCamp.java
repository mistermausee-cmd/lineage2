package l2s.gameserver.model.actor.instances.player;

import java.util.concurrent.TimeUnit;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;

public class TrainingCamp
{
	private static final long serialVersionUID = 372541022666487591L;
	
	public static final long TRAINING_DIVIDER = TimeUnit.SECONDS.toMinutes(Config.TRAINING_CAMP_MAX_DURATION);

	private final int _objectId;
	private final int _classIndex;
	private final int _level;
	private final long _startTime;
	private long _endTime = -1;

	public TrainingCamp(int objectId, int classIndex, int level, long startTime, long endTime)
	{
		_objectId = objectId;
		_classIndex = classIndex;
		_level = level;
		_startTime = startTime;
		_endTime = endTime;
	}

	public TrainingCamp(int objectId, int classIndex, int level, long startTime)
	{
		_objectId = objectId;
		_classIndex = classIndex;
		_level = level;
		_startTime = startTime;
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public int getClassIndex()
	{
		return _classIndex;
	}

	public int getLevel()
	{
		return _level;
	}

	public long getStartTime()
	{
		return _startTime;
	}

	public void setEndTime(long value)
	{
		_endTime = value;
	}

	public long getEndTime()
	{
		return _endTime;
	}

	public boolean isTraining()
	{
		return _endTime == -1;
	}

	public boolean isValid(Player player)
	{
		return Config.TRAINING_CAMP_ENABLE && player.getObjectId() == _objectId && player.getActiveSubClass().getIndex() == _classIndex;
	}

    public long getTime(long time, TimeUnit unit)
    {
        return Math.max(0L, unit.convert(time - _startTime, TimeUnit.MILLISECONDS));
    }
    
    public long getRemainingTime()
    {
        return Math.max(0L, TimeUnit.SECONDS.toMinutes(Config.TRAINING_CAMP_MAX_DURATION - getTime(System.currentTimeMillis(), TimeUnit.SECONDS)));
    }
    
    public long getRemainingTime(long time)
    {
        return Math.max(0L, TimeUnit.SECONDS.toMinutes(Config.TRAINING_CAMP_MAX_DURATION - time));
    }
    
    public long getTrainingTime(final TimeUnit unit)
    {
        return Math.min(unit.convert(Config.TRAINING_CAMP_MAX_DURATION, TimeUnit.SECONDS), Math.max(0L, unit.convert(_endTime - _startTime, TimeUnit.MILLISECONDS)));
    }
}