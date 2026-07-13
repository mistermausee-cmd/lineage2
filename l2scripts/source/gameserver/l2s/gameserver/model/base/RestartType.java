package l2s.gameserver.model.base;


public enum RestartType
{
	TO_VILLAGE,
	TO_CLANHALL,
	TO_CASTLE,
	TO_FORTRESS,
	TO_FLAG,
	FIXED,
	AGATHION,
	ADVENTURES_SONG;

	public static final RestartType[] VALUES = values();
}