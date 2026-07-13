package l2s.gameserver.templates.spawn;

import l2s.gameserver.utils.Location;


public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}