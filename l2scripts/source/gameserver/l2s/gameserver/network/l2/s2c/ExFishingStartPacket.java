package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.utils.Location;


public class ExFishingStartPacket extends L2GameServerPacket
{
	private int _charObjId;
	private Location _loc;
	private int _fishType;
	private boolean _isNightLure;

	public ExFishingStartPacket(Creature character, int fishType, Location loc, boolean isNightLure)
	{
		_charObjId = character.getObjectId();
		_fishType = fishType;
		_loc = loc;
		_isNightLure = isNightLure;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_charObjId);
		writeD(_fishType); 
		writeD(_loc.x); 
		writeD(_loc.y); 
		writeD(_loc.z); 
		writeC(_isNightLure ? 0x01 : 0x00); 
		writeC(0x01); 
	}
}