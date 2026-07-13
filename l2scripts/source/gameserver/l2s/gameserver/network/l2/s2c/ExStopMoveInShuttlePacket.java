package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;


public class ExStopMoveInShuttlePacket extends L2GameServerPacket
{
	private int _playerObjectId, _shuttleId, _playerHeading;
	private Location _loc;

	public ExStopMoveInShuttlePacket(Player cha)
	{
		_playerObjectId = cha.getObjectId();
		_shuttleId = cha.getBoat().getBoatId();
		_loc = cha.getInBoatPosition();
		_playerHeading = cha.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playerObjectId);
		writeD(_shuttleId);
		writeD(_loc.x); 
		writeD(_loc.y); 
		writeD(_loc.z); 
		writeD(_playerHeading); 
	}
}