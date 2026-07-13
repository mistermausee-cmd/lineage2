package l2s.gameserver.model.clansearch.base;


public enum ClanSearchClanSortType
{
	SORT_TYPE_NONE,
	SORT_TYPE_CLAN_NAME,
	SORT_TYPE_LEADER_NAME,
	SORT_TYPE_MEMBER_COUNT,
	SORT_TYPE_CLAN_LEVEL,
	SORT_TYPE_SEARCH_LIST_TYPE;

	public static ClanSearchClanSortType[] VALUES = values();

	public static ClanSearchClanSortType valueOf(int value)
	{
		if(value < VALUES.length)
			return VALUES[value];
		return SORT_TYPE_NONE;
	}
}