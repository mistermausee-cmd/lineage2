package l2s.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.entity.boat.ClanAirShip;
import l2s.gameserver.model.entity.events.objects.BoatPoint;

public class ExAirShipTeleportList extends L2GameServerPacket
{
	private int _fuel;
	private List<BoatPoint> _airports = Collections.emptyList();

	public ExAirShipTeleportList(ClanAirShip ship)
	{
		_fuel = ship.getCurrentFuel();
		_airports = ship.getDock().getTeleportList();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_fuel); 
		writeD(_airports.size());

		for(int i = 0; i < _airports.size(); i++)
		{
			BoatPoint point = _airports.get(i);
			writeD(i - 1); 
			writeD(point.getFuel()); 
			writeD(point.x); 
			writeD(point.y); 
			writeD(point.z); 
		}
	}
}