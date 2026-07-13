package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.matching.MatchingRoom;

public class PartyRoomInfoPacket extends L2GameServerPacket
{
	private int _id;
	private int _minLevel;
	private int _maxLevel;
	private int _lootDist;
	private int _maxMembers;
	private int _location;
	private String _title;

	public PartyRoomInfoPacket(MatchingRoom room)
	{
		_id = room.getId();
		_minLevel = room.getMinLevel();
		_maxLevel = room.getMaxLevel();
		_lootDist = room.getLootType();
		_maxMembers = room.getMaxMembersSize();
		_location = room.getLocationId();
		_title = room.getTopic();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_id); 
		writeD(_maxMembers); 
		writeD(_minLevel); 
		writeD(_maxLevel); 
		writeD(_lootDist); 
		writeD(_location); 
		writeS(_title); 
	}
}