package l2s.gameserver.model.entity.events;


public enum EventType
{
	SIEGE_EVENT,
	PVP_EVENT, 
	MAIN_EVENT, 
	BOAT_EVENT, 
	FUN_EVENT, 
	SHUTTLE_EVENT,
	CUSTOM_PVP_EVENT;

	public static final EventType[] VALUES = values();
}