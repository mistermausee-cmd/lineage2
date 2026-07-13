package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Shuttle;
import l2s.gameserver.utils.Location;


public class ExMTLInSuttlePacket extends L2GameServerPacket
{
	private int _playableObjectId, _shuttleId;
	private Location _origin, _destination;

	public ExMTLInSuttlePacket(Player player, Shuttle shuttle, Location origin, Location destination)
	{
		_playableObjectId = player.getObjectId();
		_shuttleId = shuttle.getBoatId();
		_origin = origin;
		_destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playableObjectId); 
		writeD(_shuttleId); 
		writeD(_destination.x); 
		writeD(_destination.y); 
		writeD(_destination.z); 
		writeD(_origin.x); 
		writeD(_origin.y); 
		writeD(_origin.z); 
	}
}