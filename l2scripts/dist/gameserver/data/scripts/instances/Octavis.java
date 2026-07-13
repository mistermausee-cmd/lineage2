package instances;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.MinionSpawner;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

/**
 * @author Iqman + GW
 * @reworked by Bonux
 */
public class Octavis extends Reflection
{
	public static enum OctavisState
	{
		NONE,
		START,
		FIRST_STAGE,
		SECOND_STAGE,
		THIRD_STAGE,
		FINISH
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			if(actor.getNpcId() == OCTAVIS_HARD_THIRD)
			{
				//TODO: Проверить как правильно должен выдаваться "Кристалл Октависа" на оффе.
				for(Player player : getPlayers())
					ItemFunctions.addItem(player, OCTAVIS_CRYSTAL_ITEM_ID, 1L, true);
			}
		}
	}

	private class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(!cha.isPlayer())
				return;

			if(_state == OctavisState.NONE)
			{
				for(Player player : getPlayers())
				{
					if(!player.isInZone(zone))
						return;
				}
				nextState();
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha) 
		{
			//
		}
	}

	// Instance ID's
	private static final int INSTANCE_ID_LIGHT = 180;
	private static final int INSTANCE_ID_HARD = 181;

	private static final String EPIC_ZONE_NAME = "[octavis_epic]";

	// Location's
	private static final Location LAIR_CENTER = new Location(207190, 120574, -10009);

	// NPC's
	private static final int OCTAVIS_LIGHT_SECOND = 29193;
	private static final int OCTAVIS_HARD_SECOND = 29211;
	private static final int OCTAVIS_LIGHT_THIRD = 29194;
	private static final int OCTAVIS_HARD_THIRD = 29212;
	private static final int OCTAVIS_NPC = 32949;
	private static final int OCTAVIS_GLADIATOR = 22928;
	private static final int ARENA_BEAST = 22929;
	private static final int OCTAVIS_SCIENTIST = 22930;

	// Item's
	private static final int OCTAVIS_CRYSTAL_ITEM_ID = 37505;

	// Door's
	private static final int[] MAIN_DOORS = { 26210001, 26210002 };
	private static final int[] BEAST_DOORS = { 26210101, 26210102, 26210103, 26210104, 26210105, 26210106 };

	// Spawn Group's
	private static final String FIRST_STAGE_SPAWN_GROUP = "octavis_firsts_stage";
	private static final String LIGHT_FIRST_STAGE_SPAWN_GROUP = "octavis_light_firsts_stage";
	private static final String HARD_FIRST_STAGE_SPAWN_GROUP = "octavis_hard_firsts_stage";
	private static final String SECOND_STAGE_SPAWN_GROUP = "octavis_second_stage";
	private static final String LIGHT_SECOND_STAGE_SPAWN_GROUP = "octavis_light_second_stage";
	private static final String HARD_SECOND_STAGE_SPAWN_GROUP = "octavis_hard_second_stage";

	private final DeathListener DEATH_LISTENER = new DeathListener();

	private static final int[][] OCTAVIS_SCIENTIST_SPAWN = {
		{ 207820, 120312, -10008, 28144 },
		{ 207450, 119936, -10008, 19504 },
		{ 207817, 120832, -10008, 36776 },
		{ 206542, 120306, -10008, 4408 },
		{ 206923, 119936, -10008, 12008 },
		{ 207458, 121218, -10008, 44440 },
		{ 206923, 121216, -10008, 53504 },
		{ 206620, 120568, -10008, 800 },
		{ 207194, 121082, -10008, 49000 },
		{ 207197, 120029, -10008, 17080 },
		{ 207776, 120577, -10008, 33016 },
		{ 206541, 120848, -10008, 60320 }
	};
	private static final int[][] ARENA_BEAST_SPAWN = {
		{ 206692, 119375, -10008, 0 },
		{ 208418, 120065, -10008, 0 },
		{ 207700, 121810, -10008, 0 }
	};
	private static final int[][] OUTROOM_LOCATIONS = {
		{ 206680, 119352, -10008 },
		{ 207704, 119352, -10008 },
		{ 208424, 120072, -10008 },
		{ 208440, 121096, -10008 },
		{ 207704, 121816, -10008 },
		{ 206680, 121816, -10008 }
	};

	private final ZoneListener _epicZoneListener = new ZoneListener();

	private boolean _hard = false;

	private int _arenaBeastSpawnNumber = 0;

	private OctavisState _state = OctavisState.NONE;

	@Override
	protected void onCreate()
	{
		super.onCreate();

		Zone zone = getZone(EPIC_ZONE_NAME);
		if(zone != null)
		{
			zone.addListener(_epicZoneListener);
	
			ThreadPoolManager.getInstance().schedule(() ->
			{
				for(int doorId : MAIN_DOORS)
				{
					DoorInstance door = getDoor(doorId);
					door.openMe();
				}
			}
			, 10000L);
		}

		_hard = getInstancedZoneId() == INSTANCE_ID_HARD;
	}

	public OctavisState getState()
	{
		return _state;
	}

	public void nextState()
	{
		switch(_state)
		{
			case NONE:
			{
				_state = OctavisState.START;

				ThreadPoolManager.getInstance().schedule(() ->
				{
					for(Player player : getPlayers())
						player.startScenePlayer(SceneMovie.SCENE_OCTABIS_OPENING);

					ThreadPoolManager.getInstance().schedule(() -> nextState(), SceneMovie.SCENE_OCTABIS_OPENING.getDuration());
				}
				, 1500L);
				break;
			}
			case START:
			{
				_state = OctavisState.FIRST_STAGE;

				for(int doorId : MAIN_DOORS)
				{
					DoorInstance door = getDoor(doorId);
					door.closeMe();
				}

				spawnByGroup(FIRST_STAGE_SPAWN_GROUP);
				spawnByGroup(_hard ? HARD_FIRST_STAGE_SPAWN_GROUP : LIGHT_FIRST_STAGE_SPAWN_GROUP);
				break;
			}
			case FIRST_STAGE:
			{
				_state = OctavisState.SECOND_STAGE;

				despawnByGroup(FIRST_STAGE_SPAWN_GROUP); // TODO: Нужно ли?
				despawnByGroup(_hard ? HARD_FIRST_STAGE_SPAWN_GROUP : LIGHT_FIRST_STAGE_SPAWN_GROUP);

				for(Player player : getPlayers())
					player.startScenePlayer(SceneMovie.SCENE_OCTABIS_phasech_A);

				ThreadPoolManager.getInstance().schedule(() ->
				{
					for(int doorId : BEAST_DOORS)
					{
						DoorInstance door = getDoor(doorId);
						door.openMe();
					}

					spawnByGroup(SECOND_STAGE_SPAWN_GROUP);
					spawnByGroup(_hard ? HARD_SECOND_STAGE_SPAWN_GROUP : LIGHT_SECOND_STAGE_SPAWN_GROUP);

					List<NpcInstance> gladiators = getAllByNpcId(OCTAVIS_GLADIATOR, true);
					for(NpcInstance gladiator : gladiators)
					{
						int[] selectedLoc = null;
						double selectedDistance = 0.0D;
						Location currentLoc = gladiator.getLoc();
						for(int[] outloc : OUTROOM_LOCATIONS)
						{
							if(selectedLoc == null || (selectedDistance > PositionUtils.calculateDistance(currentLoc.getX(), currentLoc.getY(), 0, outloc[0], outloc[1], 0, false)))
							{
								selectedLoc = outloc;
								selectedDistance = PositionUtils.calculateDistance(currentLoc.getX(), currentLoc.getY(), 0, selectedLoc[0], selectedLoc[1], 0, false);
							}
						}

						Location loc = new Location(selectedLoc[0], selectedLoc[1], selectedLoc[2]);
						gladiator.setRandomWalk(false);
						gladiator.setRunning();
						gladiator.setSpawnedLoc(loc);
						gladiator.moveToLocation(loc, 0, true);
					}

					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							if(_state == OctavisState.SECOND_STAGE)
							{
								int offset = _arenaBeastSpawnNumber % 3;
								for(int i = offset; i < offset + 7; i++)
								{
									List<NpcInstance> octavises = getAllByNpcId(_hard ? OCTAVIS_HARD_SECOND : OCTAVIS_LIGHT_SECOND, true);
									if(!octavises.isEmpty())
									{
										NpcInstance octavis = octavises.get(0);
										MinionSpawner spawner = octavis.getMinionList().addMinion(ARENA_BEAST, 1, 0);
										if(spawner != null)
										{
											spawner.setLoc(new Location(ARENA_BEAST_SPAWN[offset][offset], ARENA_BEAST_SPAWN[offset][1], ARENA_BEAST_SPAWN[offset][2], ARENA_BEAST_SPAWN[offset][3]));
											for(NpcInstance beast : spawner.initAndReturn())
											{
												if(octavis.isRunning())
													beast.setRunning();
											}
										}
										break;
									}
								}
								_arenaBeastSpawnNumber += 1;

								ThreadPoolManager.getInstance().schedule(this, 180000L);
							}
						}
					}
					, 1000L);
				}
				, SceneMovie.SCENE_OCTABIS_phasech_A.getDuration()); 
				break;
			}
			case SECOND_STAGE:
			{
				_state = OctavisState.THIRD_STAGE;

				despawnByGroup(SECOND_STAGE_SPAWN_GROUP);
				despawnByGroup(_hard ? HARD_SECOND_STAGE_SPAWN_GROUP : LIGHT_SECOND_STAGE_SPAWN_GROUP);

				for(NpcInstance npc : getNpcs())
				{
					if(npc.getNpcId() == ARENA_BEAST)
					{
						//npc.getSpawn().stopRespawn();
						npc.deleteMe();
					}
				}

				for(Player player : getPlayers())
					player.startScenePlayer(SceneMovie.SCENE_OCTABIS_phasech_B);

				ThreadPoolManager.getInstance().schedule(() ->
				{
					int npcId = _hard ? OCTAVIS_HARD_THIRD : OCTAVIS_LIGHT_THIRD;
					NpcInstance npc = addSpawnWithoutRespawn(npcId, LAIR_CENTER, 0);
					npc.addListener(DEATH_LISTENER);

					for(int[] loc : OCTAVIS_SCIENTIST_SPAWN)
					{
						NpcInstance scientist = addSpawnWithoutRespawn(OCTAVIS_SCIENTIST, new Location(loc[0], loc[1], loc[2], loc[3]), 0);
						//scientist.getSpawn().setRespawnDelay(120);
					}
					_arenaBeastSpawnNumber += 1;
				}
				, 15000L);
				break;
			}
			case THIRD_STAGE:
			{
				_state = OctavisState.FINISH;

				setReenterTime(System.currentTimeMillis());

				for(NpcInstance npc : getNpcs())
				{
					if(npc.getNpcId() == OCTAVIS_SCIENTIST)
					{
						//npc.getSpawn().stopRespawn();
						npc.deleteMe();
					}
				}

				for(Player player : getPlayers())
					player.startScenePlayer(SceneMovie.SCENE_OCTABIS_ENDING);

				ThreadPoolManager.getInstance().schedule(() ->
				{
					for(Player player : getPlayers())
						player.sendPacket(UsmVideo.Q005.packet(player));

					ThreadPoolManager.getInstance().schedule(() ->
					{
						NpcInstance npc = addSpawnWithoutRespawn(OCTAVIS_NPC, LAIR_CENTER, 0);
						npc.doDie(null);
						clearReflection(5, true);
					}
					, 20000L);
				}
				, SceneMovie.SCENE_OCTABIS_ENDING.getDuration());
				break;
			}
		}
	}
}