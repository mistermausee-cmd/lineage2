package l2s.gameserver.model.clansearch.base;


public enum ClanSearchTargetType
{
	TARGET_TYPE_LEADER_NAME, 
	TARGET_TYPE_CLAN_NAME;

	public static ClanSearchTargetType valueOf(int value)
	{
		switch(value)
		{
			case 0:
				return TARGET_TYPE_LEADER_NAME;
		}
		return TARGET_TYPE_CLAN_NAME;
	}
}