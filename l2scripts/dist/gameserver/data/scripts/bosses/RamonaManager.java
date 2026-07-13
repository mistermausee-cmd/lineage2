package bosses;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Bonux
**/
public class RamonaManager extends ListenerHook implements OnInitScriptListener
{
	private class CurrentHpListener implements OnCurrentHpDamageListener
	{
		private boolean lock = false;

		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(actor == null || actor.isDead())
				return;

			switch(actor.getNpcId())
			{
				case FIRST_RAMONA_NPC_ID:
					change(actor, damage, 0.75);
					break;
				case SECOND_RAMONA_NPC_ID:
					change(actor, damage, 0.50);
					break;
			}
		}

		private void change(Creature actor, double damage, double percent)
		{
			if(actor.getCurrentHp() - damage < actor.getMaxHp() * percent && !lock)
			{
				lock = true;
	
				if(actor.getNpcId() == FIRST_RAMONA_NPC_ID)
				{
					for(Creature creature : actor.getAroundCharacters(4000, 1000))
					{
						if(creature.isPlayer())
							creature.getPlayer().startScenePlayer(SceneMovie.SC_RAMONA_TRANS_A);
					}

					SpawnManager.getInstance().despawn(FIRST_SPAWN_GROUP);
					ThreadPoolManager.getInstance().schedule(() -> SpawnManager.getInstance().spawn(SECOND_SPAWN_GROUP), SceneMovie.SC_RAMONA_TRANS_A.getDuration());
				}
				else if(actor.getNpcId() == SECOND_RAMONA_NPC_ID)
				{
					for(Creature creature : actor.getAroundCharacters(4000, 1000))
					{
						if(creature.isPlayer())
							creature.getPlayer().startScenePlayer(SceneMovie.SC_RAMONA_TRANS_B);
					}

					SpawnManager.getInstance().despawn(SECOND_SPAWN_GROUP);
					ThreadPoolManager.getInstance().schedule(() -> SpawnManager.getInstance().spawn(THIRD_SPAWN_GROUP), SceneMovie.SC_RAMONA_TRANS_B.getDuration());
				}
			}
		}
	}

	// Spawn Group's
	private static final String START_SPAWN_GROUP = "ramona_start";
	private static final String FIRST_SPAWN_GROUP = "ramona_first";
	private static final String SECOND_SPAWN_GROUP = "ramona_second";
	private static final String THIRD_SPAWN_GROUP = "ramona_third";

	// NPC's
	private static final int MP_CONTROLLER_NPC_ID = 19642;
	private static final int FIRST_RAMONA_NPC_ID = 26141;
	private static final int SECOND_RAMONA_NPC_ID = 26142;
	private static final int THIRD_RAMONA_NPC_ID = 26143;

	// Door's
	private static final int RAID_DOOR_ID = 22230711;

	// Parameter's
	private final static SchedulingPattern RESPAWN_PATTERN = new SchedulingPattern("00 20 * * *"); // TODO: Check this.

	private EpicBossState _state;

	@Override
	public void onInit()
	{
		addHookNpc(ListenerHookType.NPC_KILL, MP_CONTROLLER_NPC_ID);
		addHookNpc(ListenerHookType.NPC_KILL, THIRD_RAMONA_NPC_ID);
		addHookNpc(ListenerHookType.NPC_SPAWN, FIRST_RAMONA_NPC_ID);
		addHookNpc(ListenerHookType.NPC_SPAWN, SECOND_RAMONA_NPC_ID);
		addHookNpc(ListenerHookType.NPC_SPAWN, THIRD_RAMONA_NPC_ID);

		_state = new EpicBossState(THIRD_RAMONA_NPC_ID);

		if(_state.getState().equals(EpicBossState.State.NOTSPAWN))
		{
			ReflectionUtils.getDoor(RAID_DOOR_ID).closeMe();
			SpawnManager.getInstance().spawn(START_SPAWN_GROUP);
		}
		else if(_state.getState().equals(EpicBossState.State.ALIVE))
			refreshRaid();
		else if(_state.getState().equals(EpicBossState.State.INTERVAL) || _state.getState().equals(EpicBossState.State.DEAD))
		{
			if(!_state.getState().equals(EpicBossState.State.INTERVAL))
			{
				_state.setNextRespawnDate(RESPAWN_PATTERN.next(System.currentTimeMillis()));
				_state.setState(EpicBossState.State.INTERVAL);
				_state.save();
			}
			ReflectionUtils.getDoor(RAID_DOOR_ID).openMe();
			ThreadPoolManager.getInstance().schedule(() -> refreshRaid(), _state.getInterval());
		}
	}

	private void refreshRaid()
	{
		_state.setState(EpicBossState.State.NOTSPAWN);
		_state.save();

		ReflectionUtils.getDoor(RAID_DOOR_ID).closeMe();
		SpawnManager.getInstance().spawn(START_SPAWN_GROUP);
	}

	@Override
	public void onNpcSpawn(NpcInstance npc)
	{
		switch(npc.getNpcId())
		{
			case FIRST_RAMONA_NPC_ID:
			case SECOND_RAMONA_NPC_ID:
				npc.addListener(new CurrentHpListener());
				break;
		}

		switch(npc.getNpcId())
		{
			case SECOND_RAMONA_NPC_ID:
				npc.setCurrentHp(npc.getMaxHp() * 0.75, false);
				break;
			case THIRD_RAMONA_NPC_ID:
				npc.setCurrentHp(npc.getMaxHp() * 0.5, false);
				break;
		}
	}

	@Override
	public void onNpcKill(NpcInstance npc, Player killer)
	{
		switch(npc.getNpcId())
		{
			case MP_CONTROLLER_NPC_ID:
			{
				SpawnManager.getInstance().despawn(START_SPAWN_GROUP);

				DoorInstance door = ReflectionUtils.getDoor(RAID_DOOR_ID);
				door.setLockOpen(true);
				door.closeMe();

				ThreadPoolManager.getInstance().schedule(() -> SpawnManager.getInstance().spawn(FIRST_SPAWN_GROUP), 5000);
				break;
			}
			case THIRD_RAMONA_NPC_ID:
			{
				_state.setNextRespawnDate(RESPAWN_PATTERN.next(System.currentTimeMillis()));
				_state.setState(EpicBossState.State.INTERVAL);
				_state.save();

				DoorInstance door = ReflectionUtils.getDoor(RAID_DOOR_ID);
				door.setLockOpen(false);
				door.openMe();
				break;
			}
		}
	}
}