package l2s.gameserver.geodata;

import java.util.HashMap;

import l2s.commons.geometry.Shape;

public interface GeoControl
{
	public abstract Shape getGeoShape();

	public abstract HashMap<Long, Byte> getGeoAround();

	public abstract void setGeoAround(HashMap<Long, Byte> value);

	public abstract int getGeoIndex();
}