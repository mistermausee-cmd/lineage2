package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.utils.Location;


public class RequestExGetOnAirShip extends L2GameClientPacket
{
	private int _shipId;
	private Location loc = new Location();

	@Override
	protected void readImpl()
	{
		loc.x = readD();
		loc.y = readD();
		loc.z = readD();
		_shipId = readD();
	}

	@Override
	protected void runImpl()
	{
		
	}
}