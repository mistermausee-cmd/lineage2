package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.templates.jump.JumpPoint;

public class ExFlyMove extends L2GameServerPacket
{
	public static final int MANY_WAY_TYPE = 0;
	public static final int ONE_WAY_TYPE = 2;

	private int _objId;
	private Collection<JumpPoint> _points;
	private int _type;
	private int _trackId;

	public ExFlyMove(int objId, Collection<JumpPoint> points, int trackId)
	{
		_objId = objId;
		_points = points;
		if(_points.size() > 1)
			_type = MANY_WAY_TYPE;
		else
			_type = ONE_WAY_TYPE;
		_trackId = trackId;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objId); 

		writeD(_type); 
		writeD(0x00); 
		writeD(_trackId); 

		writeD(_points.size()); 
		for(JumpPoint point : _points)
		{
			writeD(point.getNextWayId()); 
			writeD(0x00); 
			writeD(point.getLocation().getX());
			writeD(point.getLocation().getY());
			writeD(point.getLocation().getZ());
		}
	}
}
