package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.entity.boat.Shuttle;


public class ExSuttleMovePacket extends L2GameServerPacket
{
	private final Shuttle _shuttle;

	public ExSuttleMovePacket(Shuttle shuttle)
	{
		_shuttle = shuttle;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_shuttle.getBoatId()); 
		writeD(_shuttle.getMoveSpeed()); 
		writeD(0x00); 
		writeD(_shuttle.getDestination().x); 
		writeD(_shuttle.getDestination().y); 
		writeD(_shuttle.getDestination().z); 
	}
}