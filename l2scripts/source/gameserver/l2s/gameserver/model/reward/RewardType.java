package l2s.gameserver.model.reward;


public enum RewardType
{
	RATED_GROUPED, 
	NOT_RATED_NOT_GROUPED, 
	NOT_RATED_GROUPED, 
	SWEEP, 
	EVENT_GROUPED,
	LUCKY;

	public static final RewardType[] VALUES = values();
}