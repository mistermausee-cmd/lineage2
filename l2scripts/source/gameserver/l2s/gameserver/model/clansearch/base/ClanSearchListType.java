package l2s.gameserver.model.clansearch.base;


public enum ClanSearchListType
{
	SLT_FRIEND_LIST,
	SLT_PLEDGE_MEMBER_LIST,
	SLT_ADDITIONAL_FRIEND_LIST,
	SLT_ADDITIONAL_LIST,
	SLT_ANY;

	public static final ClanSearchListType[] VALUES = values();

	public static ClanSearchListType getType(int value)
	{
		return (value == -1 || value >= VALUES.length) ? SLT_ANY : VALUES[value];
	}
}