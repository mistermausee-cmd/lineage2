package instances;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.EventTriggersManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.ai.OnAiEventListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.WalkerRoute;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Bonux
**/
public class EmbryoCommandPost extends Reflection
{
	private class FirstStageDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			for(String group : new String[]{ FIRST_STAGE_MAIN_SPAWN, FIRST_STAGE_CENTER_SPAWN })
			{
				for(Spawner spawn : getSpawners(group))
				{
					for(NpcInstance npc : spawn.getAllSpawned())
					{
						if(npc.isMonster() && !npc.isDead())
							return;
					}
				}
			}
			processNextState(InstanceState.SECOND_STAGE_START);
		}
	}

	private class FirstStageArrivedListener implements OnAiEventListener
	{
		@Override
		public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args)
		{
			if(evt == CtrlEvent.EVT_FINISH_WALKER_ROUTE)
			{
				if(actor.isNpc())
				{
					NpcInstance npc = (NpcInstance) actor;
					Location loc = Location.findAroundPosition(actor.getLoc(), 150, 300, npc.getGeoIndex());
					npc.setWalking();
					npc.setSpawnedLoc(loc);
					npc.moveToLocation(loc, 0, true);
					Functions.npcSay(npc, NpcString.DONT_LET_A_SINGLE_ONE_LEAVE_THIS_PLACE_ALIVE);
				}
				actor.removeListener(this);
			}
		}
	}

	private class SecondStageDeathListener implements OnDeathListener
	{
		private final InstanceState _nextState;
		private final String _spawnGroup;

		public SecondStageDeathListener(InstanceState nextState, String spawnGroup)
		{
			_nextState = nextState;
			_spawnGroup = spawnGroup;
		}

		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			switch(actor.getNpcId())
			{
				case ADOLPH_MONSTER_ID:
				{
					spawnAndMoveNpc(ADOLPH_NPC_ID, actor.getLoc(), ADOLPH_SPAWN_WALKER_ROUTE);
					actor.doDecay();
					break;
				}
				case BARTON_MONSTER_ID:
				{
					spawnAndMoveNpc(BARTON_NPC_ID, actor.getLoc(), BARTON_SPAWN_WALKER_ROUTE);
					actor.doDecay();
					break;
				}
				case HAYUK_MONSTER_ID:
				{
					spawnAndMoveNpc(HAYUK_NPC_ID, actor.getLoc(), HAYUK_SPAWN_WALKER_ROUTE);
					actor.doDecay();
					break;
				}
				case ELISE_MOSNTER_ID:
				{
					spawnAndMoveNpc(ELISE_NPC_ID, actor.getLoc(), ELISE_SPAWN_WALKER_ROUTE);
					actor.doDecay();
					break;
				}
				case ELIYAH_MONSTER_ID:
				{
					spawnAndMoveNpc(ELIYAH_NPC_ID, actor.getLoc(), ELIYAH_SPAWN_WALKER_ROUTE);
					actor.doDecay();
					break;
				}
			}

			for(Spawner spawn : getSpawners(_spawnGroup))
			{
				for(NpcInstance npc : spawn.getAllSpawned())
				{
					if(npc.isMonster() && !npc.isDead())
						return;
				}
			}
			processNextState(_nextState);
		}
	}

	private class SecondStageArrivedListener implements OnAiEventListener
	{
		@Override
		public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args)
		{
			if(evt == CtrlEvent.EVT_FINISH_WALKER_ROUTE)
			{
				if(actor.isNpc())
				{
					NpcInstance npc = (NpcInstance) actor;
					Location loc = Location.findAroundPosition(actor.getLoc(), 50, 100, npc.getGeoIndex());
					npc.setWalking();
					npc.setSpawnedLoc(loc);
					npc.moveToLocation(loc, 0, true);
				}
				actor.removeListener(this);
			}
		}
	}

	private class SecondStageRaidDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			if(actor.getNpcId() == GEORK_MONSTER_ID)
				Functions.npcSay((NpcInstance) actor, NpcString.COMMANDER_BURNSTEIN_I_WASNT_ABLE_TO_COMPLETE_MY_MISSION);

			for(NpcInstance npc : getNpcs())
			{
				if(!npc.isDead() && npc.getNpcId() == GEORK_MONSTER_ID)
					return;
			}
			processNextState(InstanceState.SECOND_STAGE_ADOLPH_TALK);
		}
	}

	private class ThirdStageBurnsteinListeners implements OnDeathListener, OnAiEventListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			for(NpcInstance npc : getNpcs())
			{
				if(!npc.isDead() && npc.getNpcId() == BURNSTEIN_MONSTER_ID)
					return;
			}

			for(NpcInstance npc : getNpcs())
			{
				if(ArrayUtils.contains(BURNSTEIN_MINIONS_IDS, npc.getNpcId()))
					npc.doDie(killer);
				else if(ArrayUtils.contains(SUPPORT_NPC_IDS, npc.getNpcId())) // TODO: Надо ли?
					npc.deleteMe();
			}

			processNextState(InstanceState.THIRD_STAGE_FINISH);
		}

		@Override
		public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args)
		{
			if(evt == CtrlEvent.EVT_FINISH_CASTING)
			{
				if(actor.getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
				{
					Skill skill = (Skill) args[0];
					boolean success = (Boolean) args[2];
					if(skill.getId() == EMBRYOS_CALL_SKILL_ID && success)
					{
						Location loc = Location.findAroundPosition(BURSTEIN_MINIONS_SPAWN_LOC, 100, 150, actor.getGeoIndex());
						NpcUtils.spawnSingle(Rnd.get(BURNSTEIN_MINIONS_IDS), loc, EmbryoCommandPost.this);
					}
				}
			}
		}
	}

	private class PlayerEmergencyWhistleListener implements OnAiEventListener
	{
		@Override
		public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args)
		{
			if(evt == CtrlEvent.EVT_FINISH_CASTING)
			{
				Skill skill = (Skill) args[0];
				boolean success = (Boolean) args[2];
				if(skill.getId() == EMERGENCY_WHISTLE__ADEN_VANGUARD_SKILL_ID && success)
				{
					Location loc = Location.findAroundPosition(actor.getLoc(), 50, 100, actor.getGeoIndex());
					NpcInstance npc = NpcUtils.spawnSingle(Rnd.get(SUPPORT_NPC_IDS), loc, EmbryoCommandPost.this);
					npc.setHasChatWindow(false);
					npc.setAggroRange(1000);
				}
			}
		}
	}

	public static enum InstanceState
	{
		NONE, // Инстанс создан.
		FIRST_STAGE_START, // Старт первого уровня.
		FIRST_STAGE_CENTER, // Выбегают дополнительные монстры в центр.
		SECOND_STAGE_START, // Старт второго уровня.
		SECOND_STAGE_1ST_ROOM, // Выпускаем монстров с 1й комнаты.
		SECOND_STAGE_2ND_ROOM, // Выпускаем монстров с 2й комнаты.
		SECOND_STAGE_3RD_ROOM, // Выпускаем монстров с 3й комнаты.
		SECOND_STAGE_4TH_ROOM, // Выпускаем монстров с 4й комнаты.
		SECOND_STAGE_RAID, // Сражение с Георгом.
		SECOND_STAGE_ADOLPH_TALK, // Разговор с Адольфом.
		THIRD_STAGE_START, // Старт третьего уровня.
		THIRD_STAGE_FINISH, // Бернштайн убит.
		COLLAPSE; // Закрываем инст.
	}

	// NPC's
	private static final int GEORK_MONSTER_ID = 26135;
	private static final int BURNSTEIN_MONSTER_ID = 26136;
	private static final int ADOLPH_MONSTER_ID = 23590;
	private static final int BARTON_MONSTER_ID = 23591;
	private static final int HAYUK_MONSTER_ID = 23592;
	private static final int ELISE_MOSNTER_ID = 23593;
	private static final int ELIYAH_MONSTER_ID = 23594;
	private static final int ADOLPH_NPC_ID = 34090;
	private static final int BARTON_NPC_ID = 34091;
	private static final int HAYUK_NPC_ID = 34092;
	private static final int ELISE_NPC_ID = 34093;
	private static final int ELIYAH_NPC_ID = 34094;
	private static final int[] SUPPORT_NPC_IDS = { ADOLPH_NPC_ID, BARTON_NPC_ID, HAYUK_NPC_ID, ELISE_NPC_ID, ELIYAH_NPC_ID };
	private static final int[] SECOND_STAGE_HIDDEN_ROOM_MONSTER_IDS = { 23595, 23596, 23597 };
	private static final int[] BURNSTEIN_MINIONS_IDS = { 23603, 23604 };

	// Spawn Groups
	private static final String FIRST_STAGE_MAIN_SPAWN = "embyo_259_1st_main";
	private static final String FIRST_STAGE_CENTER_SPAWN = "embyo_259_1st_center";
	private static final String FIRST_STAGE_RAID_SPAWN = "embyo_259_1st_raid";
	private static final String SECOND_STAGE_1ST_ROOM_SPAWN = "embyo_259_2nd_1st_room";
	private static final String SECOND_STAGE_2ND_ROOM_SPAWN = "embyo_259_2nd_2nd_room";
	private static final String SECOND_STAGE_3RD_ROOM_SPAWN = "embyo_259_2nd_3rd_room";
	private static final String SECOND_STAGE_4TH_ROOM_SPAWN = "embyo_259_2nd_4th_room";
	private static final String SECOND_STAGE_RAID_SPAWN = "embyo_259_2nd_raid";
	private static final String THIRD_STAGE_RAID_SPAWN = "embyo_259_3rd_raid";

	// Zones
	private static final String FIRST_TELEPORT_ZONE = "[embyo_259_1st_teleport]";
	private static final String SECOND_TELEPORT_ZONE = "[embyo_259_2nd_teleport]";
	private static final String FIRST_STAIRS_ZONE = "[embyo_259_1st_stairs]";
	private static final String SECOND_STAIRS_ZONE = "[embyo_259_2nd_stairs]";

	// Doors
	private static final int SECOND_STAGE_1ST_ROOM_DOOR = 18190100;
	private static final int SECOND_STAGE_2ND_ROOM_DOOR = 18190102;
	private static final int SECOND_STAGE_3RD_ROOM_DOOR = 18190104;
	private static final int SECOND_STAGE_4TH_ROOM_DOOR = 18190106;

	// Event Triggers
	private static final int FIRST_STAGE_TELEPORT_CIRCLE = 18190810;
	private static final int SECOND_STAGE_TELEPORT_CIRCLE = 18190820;
	private static final int SECOND_STAGE_1ST_SPAWN_ROOM = 18190510;
	private static final int SECOND_STAGE_2ND_SPAWN_ROOM = 18190512;
	private static final int SECOND_STAGE_3RD_SPAWN_ROOM = 18190514;
	private static final int SECOND_STAGE_4TH_SPAWN_ROOM = 18190516;

	// Locations
	private static final Location[] SECOND_STAGE_HIDDEN_ROOM_SPAWN_LOCS = new Location[]{ new Location(-43256, 45352, -8000), new Location(-44808, 45352, -8000) };
	private static final Location BURSTEIN_MINIONS_SPAWN_LOC = new Location(-44040, 44312, -6968);

	// Walker Routes
	private static final WalkerRoute CENTRAL_1ST_STAGE_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44040, 43784, -8792), new Location(-44040, 44792, -8886) }, true);
	private static final WalkerRoute FIRST_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-43544, 44520, -8096) }, false);
	private static final WalkerRoute SECOND_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-43544, 44104, -8096) }, false);
	private static final WalkerRoute THIRD_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44536, 44504, -8096) }, false);
	private static final WalkerRoute FOURTH_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44520, 44104, -8096) }, false);
	private static final WalkerRoute CENTRAL_2ND_STAGE_MONSTERS_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44040, 45352, -8024), new Location(-44040, 44328, -8096) }, true);
	private static final WalkerRoute ADOLPH_SPAWN_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44040, 45016, -8096) }, true);
	private static final WalkerRoute BARTON_SPAWN_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-43928, 44936, -8096) }, true);
	private static final WalkerRoute HAYUK_SPAWN_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-43976, 44936, -8096) }, true);
	private static final WalkerRoute ELISE_SPAWN_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44088, 44936, -8096) }, true);
	private static final WalkerRoute ELIYAH_SPAWN_WALKER_ROUTE = NpcUtils.makeWalkerRoute(new Location[]{ new Location(-44136, 44936, -8096) }, true);

	// Skills
	private static final int EMBRYOS_CALL_SKILL_ID = 16521;
	private static final int REVOLUTIONARIES_PETRIFICATION_SKILL_ID = 16540;
	private static final int EMERGENCY_WHISTLE__ADEN_VANGUARD_SKILL_ID = 18504;

	// Items
	private static final int EMERGENCY_WHISTLE__ADEN_VANGUARD_ITEM_ID = 46404;

	private final FirstStageArrivedListener _firstStageCenterListener = new FirstStageArrivedListener();
	private final FirstStageDeathListener _firstStageDeathListener = new FirstStageDeathListener();
	private final SecondStageArrivedListener _secondStageArrivedListener = new SecondStageArrivedListener();
	private final SecondStageRaidDeathListener _secondStageRaidDeathListener = new SecondStageRaidDeathListener();
	private final ThirdStageBurnsteinListeners _thirdStageBurnsteinListeners = new ThirdStageBurnsteinListeners();
	private final PlayerEmergencyWhistleListener _playerEmergencyWhistleListener = new PlayerEmergencyWhistleListener();

	private InstanceState _state = InstanceState.NONE;

	private ScheduledFuture<?> _secondStageMonstersSpawnTask;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		processNextState(InstanceState.FIRST_STAGE_START);
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		player.addListener(_playerEmergencyWhistleListener);
	}

	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
		player.removeListener(_playerEmergencyWhistleListener);
		ItemFunctions.deleteItemsEverywhere(player, EMERGENCY_WHISTLE__ADEN_VANGUARD_ITEM_ID); // TODO: Надо ли?
	}

	public InstanceState getState()
	{
		return _state;
	}

	public boolean processNextState(InstanceState state)
	{
		if((state.ordinal() - _state.ordinal()) != 1)
			return false;

		_state = state;

		switch(_state)
		{
			case FIRST_STAGE_START:
			{
				getZone(FIRST_STAIRS_ZONE).setActive(true);
				spawnByGroup(FIRST_STAGE_MAIN_SPAWN);
				spawnInvulnerableByGroup(FIRST_STAGE_RAID_SPAWN);
				ThreadPoolManager.getInstance().schedule(() -> processNextState(InstanceState.FIRST_STAGE_CENTER), 60000L);
				return true;
			}
			case FIRST_STAGE_CENTER:
			{
				for(Spawner spawn : spawnByGroup(FIRST_STAGE_CENTER_SPAWN))
				{
					for(NpcInstance npc : spawn.getAllSpawned())
					{
						if(npc.isMonster())
						{
							npc.addListener(_firstStageCenterListener);
							npc.getAI().setWalkerRoute(CENTRAL_1ST_STAGE_WALKER_ROUTE);
						}
					}
				}
				for(NpcInstance npc : getNpcs())
				{
					if(npc.isMonster())
						npc.addListener(_firstStageDeathListener);
				}
				return true;
			}
			case SECOND_STAGE_START:
			{
				broadcastPacket(new ExShowScreenMessage(NpcString.THE_TELEPORT_GATE_TO_THE_2ND_FLOOR_HAS_BEEN_ACTIVATED, 10000, ScreenMessageAlign.TOP_CENTER, true, true));
				EventTriggersManager.getInstance().addTrigger(this, FIRST_STAGE_TELEPORT_CIRCLE);
				EventTriggersManager.getInstance().addTrigger(this, SECOND_STAGE_1ST_SPAWN_ROOM);
				EventTriggersManager.getInstance().addTrigger(this, SECOND_STAGE_2ND_SPAWN_ROOM);
				EventTriggersManager.getInstance().addTrigger(this, SECOND_STAGE_3RD_SPAWN_ROOM);
				EventTriggersManager.getInstance().addTrigger(this, SECOND_STAGE_4TH_SPAWN_ROOM);
				getZone(FIRST_STAIRS_ZONE).setActive(false);
				getZone(SECOND_STAIRS_ZONE).setActive(true);
				getZone(FIRST_TELEPORT_ZONE).setActive(true);
				despawnByGroup(FIRST_STAGE_MAIN_SPAWN);
				despawnByGroup(FIRST_STAGE_CENTER_SPAWN);
				despawnByGroup(FIRST_STAGE_RAID_SPAWN);
				spawnInvulnerableByGroup(SECOND_STAGE_1ST_ROOM_SPAWN);
				spawnInvulnerableByGroup(SECOND_STAGE_2ND_ROOM_SPAWN);
				spawnInvulnerableByGroup(SECOND_STAGE_3RD_ROOM_SPAWN);
				spawnInvulnerableByGroup(SECOND_STAGE_4TH_ROOM_SPAWN);
				spawnInvulnerableByGroup(SECOND_STAGE_RAID_SPAWN);
				ThreadPoolManager.getInstance().schedule(() -> processNextState(InstanceState.SECOND_STAGE_1ST_ROOM), 15000L);
				return true;
			}
			case SECOND_STAGE_1ST_ROOM:
			{
				EventTriggersManager.getInstance().removeTrigger(this, SECOND_STAGE_1ST_SPAWN_ROOM);
				getDoor(SECOND_STAGE_1ST_ROOM_DOOR).openMe();
				moveMonstersFromSecondStageRoom(SECOND_STAGE_1ST_ROOM_SPAWN, InstanceState.SECOND_STAGE_2ND_ROOM, FIRST_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE);
				return true;
			}
			case SECOND_STAGE_2ND_ROOM:
			{
				EventTriggersManager.getInstance().removeTrigger(this, SECOND_STAGE_2ND_SPAWN_ROOM);
				getDoor(SECOND_STAGE_2ND_ROOM_DOOR).openMe();
				moveMonstersFromSecondStageRoom(SECOND_STAGE_2ND_ROOM_SPAWN, InstanceState.SECOND_STAGE_3RD_ROOM, SECOND_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE);
				return true;
			}
			case SECOND_STAGE_3RD_ROOM:
			{
				EventTriggersManager.getInstance().removeTrigger(this, SECOND_STAGE_3RD_SPAWN_ROOM);
				getDoor(SECOND_STAGE_3RD_ROOM_DOOR).openMe();
				moveMonstersFromSecondStageRoom(SECOND_STAGE_3RD_ROOM_SPAWN, InstanceState.SECOND_STAGE_4TH_ROOM, THIRD_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE);
				_secondStageMonstersSpawnTask = ThreadPoolManager.getInstance().scheduleAtFixedDelay(() -> spawnAndMoveNpc(Rnd.get(SECOND_STAGE_HIDDEN_ROOM_MONSTER_IDS), Rnd.get(SECOND_STAGE_HIDDEN_ROOM_SPAWN_LOCS), CENTRAL_2ND_STAGE_MONSTERS_WALKER_ROUTE), 15000L, 15000L);
				return true;
			}
			case SECOND_STAGE_4TH_ROOM:
			{
				EventTriggersManager.getInstance().removeTrigger(this, SECOND_STAGE_4TH_SPAWN_ROOM);
				getDoor(SECOND_STAGE_4TH_ROOM_DOOR).openMe();
				moveMonstersFromSecondStageRoom(SECOND_STAGE_4TH_ROOM_SPAWN, InstanceState.SECOND_STAGE_RAID, FOURTH_ROOM_2ND_STAGE_EXIT_WALKER_ROUTE);
				return true;
			}
			case SECOND_STAGE_RAID:
			{
				getZone(SECOND_STAIRS_ZONE).setActive(false);

				SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(REVOLUTIONARIES_PETRIFICATION_SKILL_ID, 1);
				if(skillEntry != null)
				{
					for(NpcInstance npc : getNpcs())
					{
						int npcId = npc.getNpcId();
						if(npcId == ADOLPH_NPC_ID || npcId == BARTON_NPC_ID || npcId == HAYUK_NPC_ID || npcId == ELISE_NPC_ID || npcId == ELIYAH_NPC_ID)
							skillEntry.getEffects(npc, npc);
					}
				}

				for(Spawner spawn : getSpawners(SECOND_STAGE_RAID_SPAWN))
				{
					for(NpcInstance npc : spawn.getAllSpawned())
					{
						if(npc.isMonster())
						{
							npc.getFlags().getParalyzed().stop(this);
							npc.getFlags().getInvulnerable().stop(this);
							npc.addListener(_secondStageRaidDeathListener);
							npc.getAI().setWalkerRoute(CENTRAL_2ND_STAGE_MONSTERS_WALKER_ROUTE);
							if(npc.getNpcId() == GEORK_MONSTER_ID)
								Functions.npcSay(npc, NpcString.USELESS_BUNCH_ILL_DEAL_WITH_YOU);
						}
					}
				}
				return true;
			}
			case SECOND_STAGE_ADOLPH_TALK:
			{
				_secondStageMonstersSpawnTask.cancel(false);

				for(NpcInstance npc : getNpcs())
				{
					int npcId = npc.getNpcId();
					if(npcId == ADOLPH_NPC_ID || npcId == BARTON_NPC_ID || npcId == HAYUK_NPC_ID || npcId == ELISE_NPC_ID || npcId == ELIYAH_NPC_ID)
						npc.getAbnormalList().stop(REVOLUTIONARIES_PETRIFICATION_SKILL_ID);
				}

				broadcastPacket(new ExShowScreenMessage(NpcString.IT_LOOKS_LIKE_CAPTAIN_ADOLPH_WANTS_TO_TALK_TO_THE_PARTY_LEADER_GO_TALK_TO_ADOLPH, 10000, ScreenMessageAlign.TOP_CENTER, true, true));
				return true;
			}
			case THIRD_STAGE_START:
			{
				broadcastPacket(new ExShowScreenMessage(NpcString.THE_TELEPORT_GATE_TO_THE_3RD_FLOOR_HAS_BEEN_ACTIVATED, 10000, ScreenMessageAlign.TOP_CENTER, true, true));
				EventTriggersManager.getInstance().addTrigger(this, SECOND_STAGE_TELEPORT_CIRCLE);
				getZone(SECOND_TELEPORT_ZONE).setActive(true);

				for(Spawner spawn : spawnByGroup(THIRD_STAGE_RAID_SPAWN))
				{
					for(NpcInstance npc : spawn.getAllSpawned())
					{
						if(npc.getNpcId() == BURNSTEIN_MONSTER_ID)
							npc.addListener(_thirdStageBurnsteinListeners);
					}
				}
				return true;
			}
			case THIRD_STAGE_FINISH:
			{
				setReenterTime(System.currentTimeMillis());
				broadcastPacket(new ExShowScreenMessage(NpcString.YOUVE_SUCCESSFULLY_ATTACKED_THE_COMMAND_POST_AND_DEFEATED_COMMANDER_BURNSTEIN, 10000, ScreenMessageAlign.TOP_CENTER, true, true));
				ThreadPoolManager.getInstance().schedule(() -> processNextState(InstanceState.COLLAPSE), 30000L);
				return true;
			}
			case COLLAPSE:
			{
				startCollapseTimer(60000);
				return true;
			}
		}
		return false;
	}

	private List<Spawner> spawnInvulnerableByGroup(String group)
	{
		List<Spawner> spawns = spawnByGroup(group);
		for(Spawner spawn : spawns)
		{
			for(NpcInstance npc : spawn.getAllSpawned())
			{
				npc.getFlags().getParalyzed().start(this);
				npc.getFlags().getInvulnerable().start(this);
			}
		}
		return spawns;
	}

	private void moveMonstersFromSecondStageRoom(String roomSpawnGroup, InstanceState nextState, WalkerRoute roomExitWalkerRoute)
	{
		SecondStageDeathListener secondStageDeathListener = new SecondStageDeathListener(nextState, roomSpawnGroup);
		for(Spawner spawn : getSpawners(roomSpawnGroup))
		{
			for(NpcInstance npc : spawn.getAllSpawned())
			{
				if(npc.isMonster())
				{
					npc.getFlags().getParalyzed().stop(this);
					npc.getFlags().getInvulnerable().stop(this);
					npc.addListener(secondStageDeathListener);
					npc.addListener(_secondStageArrivedListener);
					npc.getAI().setWalkerRoute(roomExitWalkerRoute);
				}
			}
		}
	}

	private void spawnAndMoveNpc(int npcId, Location spawnLoc, WalkerRoute walkerRoute)
	{
		NpcInstance npc = NpcUtils.spawnSingle(npcId, spawnLoc, this);
		npc.getAI().setWalkerRoute(walkerRoute);
	}
}