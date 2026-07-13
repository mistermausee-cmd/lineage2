package instances;

import l2s.gameserver.utils.Location;

/**
 * @author Evil_dnk
 * @reworked by Bonux
**/
public class KartiaGroup extends Kartia
{
	@Override
	protected void onCreate()
	{
		_roomDoorId = 16170012;
		_raidDoorId = 16170013;

		_excludedZoneTeleportLoc = new Location(-119832, -10424, -11949);

		_rulerSpawnLoc = new Location(-120864, -15872, -11400, 15596);
		_supportTroopsSpawnLoc = new Location(-120901, -14562, -11424, 47595);

		_kartiaAltharSpawnLoc = new Location(-119684, -10453, -11307, 0);
		_ssqCameraLightSpawnLoc = new Location(-119684, -10453, -11307, 0);
		_ssqCameraZoneSpawnLoc = new Location(-119907, -10443, -11924, 0);

		_instanceZone = getZone("[kartia_instance_party]");
		_excludedInstanceZone = getZone("[kartia_excluded_zone_party]");

		_aggroStartPointLoc = new Location(-120854, -13928, -11462);
		_aggroMovePointLoc = new Location(-120854, -13928, -11462);

		_monsterMoveNearestPointLoc = new Location(-120888, -10456, -11710);
		_monsterMovePointLoc = new Location(-120872, -15080, -11452);

		_leftKillerRoutes.add(new Location(-120888, -10456, -11710));
		_leftKillerRoutes.add(new Location(-120008, -10472, -11926));
		_leftKillerRoutes.add(new Location(-119653, -10876, -11920));
		_leftKillerRoutes.add(new Location(-118750, -10791, -11920));
		_leftKillerRoutes.add(new Location(-118730, -10453, -11926));
		_leftKillerRoutes.add(new Location(-119501, -10451, -11688));
		_leftKillerRoutes.add(new Location(-119501, -10451, -11688));
		_leftKillerRoutes.add(new Location(-120888, -10456, -11710));

		_rightKillerRoutes.add(new Location(-120008, -10472, -11926));
		_rightKillerRoutes.add(new Location(-119588, -9980, -11920));
		_rightKillerRoutes.add(new Location(-118725, -10009, -11920));
		_rightKillerRoutes.add(new Location(-118730, -10453, -11926));
		_rightKillerRoutes.add(new Location(-119501, -10451, -11688));
		_rightKillerRoutes.add(new Location(-119501, -10451, -11688));

		super.onCreate();

		startChallenge();
	}
}