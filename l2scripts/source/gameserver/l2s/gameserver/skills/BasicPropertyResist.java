package l2s.gameserver.skills;

import java.time.Duration;
import java.time.Instant;


public class BasicPropertyResist
{
	private static final Duration RESIST_DURATION = Duration.ofSeconds(15); 

	private volatile Instant _resistanceEndTime = Instant.MIN;
	private volatile int _resistanceLevel;

	
	public boolean isExpired()
	{
		return Instant.now().isAfter(_resistanceEndTime);
	}

	
	public Duration getRemainTime()
	{
		return Duration.between(Instant.now(), _resistanceEndTime);
	}

	
	public int getResistLevel()
	{
		return !isExpired() ? _resistanceLevel : 0;
	}

	
	public synchronized void increaseResistLevel()
	{
		
		if(isExpired())
		{
			_resistanceLevel = 1;
			_resistanceEndTime = Instant.now().plus(RESIST_DURATION);
		}
		else
			_resistanceLevel++;
	}
}