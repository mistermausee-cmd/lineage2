package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.entity.boat.Shuttle;
import l2s.gameserver.utils.Location;


public class ExSuttleGetOffPacket extends L2GameServerPacket
{
	private int _playerObjectId, _shuttleId;
	private Location _loc;

	public ExSuttleGetOffPacket(Playable cha, Shuttle shuttle, Location loc)
	{
		_playerObjectId = cha.getObjectId();
		_shuttleId = shuttle.getBoatId();
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playerObjectId); 
		writeD(_shuttleId); 
		writeD(_loc.x); 
		writeD(_loc.y); 
		writeD(_loc.z); 
	}
}