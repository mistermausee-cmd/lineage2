package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

public class ExMoveToTargetInAirShipPacket extends L2GameServerPacket
{
	private int char_id, boat_id, target_id, _dist;
	private Location _loc;

	public ExMoveToTargetInAirShipPacket(Player cha, Boat boat, int targetId, int dist, Location origin)
	{
		char_id = cha.getObjectId();
		boat_id = boat.getBoatId();
		target_id = targetId;
		_dist = dist;
		_loc = origin;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(char_id); 
		writeD(target_id); 
		writeD(_dist); 
		writeD(_loc.y); 
		writeD(_loc.z); 
		writeD(_loc.h); 
		writeD(boat_id); 
	}
}