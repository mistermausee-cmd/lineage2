package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.utils.Location;


public class RadarControlPacket extends L2GameServerPacket
{
	private int _x, _y, _z, _type, _showRadar;

	public RadarControlPacket(int showRadar, int type, Location loc)
	{
		this(showRadar, type, loc.x, loc.y, loc.z);
	}

	public RadarControlPacket(int showRadar, int type, int x, int y, int z)
	{
		_showRadar = showRadar; 
		_type = type; 
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_showRadar);
		writeD(_type); 
		writeD(_x); 
		writeD(_y); 
		writeD(_z); 
	}
}