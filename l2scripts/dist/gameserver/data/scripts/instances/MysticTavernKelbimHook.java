package instances;

import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 Obi-Wan
 03.07.2016
 */
public class MysticTavernKelbimHook extends ListenerHook implements OnInitScriptListener
{
	private int stage = 0;

	@Override
	public void onInit()
	{
		addHookGlobal(ListenerHookType.PLAYER_FINISH_CAST_SKILL);
		addHookNpc(ListenerHookType.NPC_KILL, 23693);
		addHookNpc(ListenerHookType.NPC_KILL, 23706, 23707);
	}

	@Override
	public void onPlayerFinishCastSkill(Player player, int skillId)
	{
		switch(skillId)
		{
			case 18514:
				ItemFunctions.deleteItem(player, 46555, 1);
				player.getReflection().broadcastPacket(new ExShowScreenMessage(NpcString.YOUVE_SUCCESSFULLY_SEALED_THE_ALTAR_OF_EARTH, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				for(Creature creature : player.getAroundCharacters(750, 500))
				{
					if(creature.isMonster())
					{
						creature.deleteMe();
					}
				}

				NpcInstance npc = NpcUtils.spawnSingle(19611, new Location(207064, -88328, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(207128, -88712, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(207112, -88280, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(207128, -88712, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(207080, -88280, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(207128, -88712, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(207128, -88296, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(207128, -88712, 101), 100, true);

				stage++;
				if(stage == 2)
				{
					stage2(player);
				}

				break;
			case 18516:
				ItemFunctions.deleteItem(player, 46556, 1);
				player.getReflection().broadcastPacket(new ExShowScreenMessage(NpcString.YOUVE_SUCCESSFULLY_SEALED_THE_ALTAR_OF_WIND, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				for(Creature creature : player.getAroundCharacters(750, 500))
				{
					if(creature.isMonster())
					{
						creature.deleteMe();
					}
				}

				npc = NpcUtils.spawnSingle(19611, new Location(210056, -87672, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(210072, -88872, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(210136, -87672, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(210072, -88872, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(210120, -87544, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(210072, -88872, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(210056, -87576, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(210072, -88872, 101), 100, true);

				npc = NpcUtils.spawnSingle(19611, new Location(210088, -87640, 101), player.getReflection());
				npc.setRunning();
				npc.moveToLocation(new Location(210072, -88872, 101), 100, true);

				stage++;
				if(stage == 2)
				{
					stage2(player);
				}

				break;
		}
	}

	private void stage2(Player player)
	{
		player.getReflection().broadcastPacket(new ExShowScreenMessage(NpcString.THE_DOOR_TO_THE_DUNGEON_HAS_BEEN_OPENED, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
		player.getReflection().openDoor(26150104);
		player.getReflection().spawnByGroup("mystic_tavern_kelbim_3");

		SimpleSpawner simpleSpawner = new SimpleSpawner(23693);
		simpleSpawner.setLoc(new Location(208600, -87208, -933));
		simpleSpawner.setReflection(player.getReflection());
		simpleSpawner.setRespawnDelay(0);

		NpcInstance npcSpawn = simpleSpawner.doSpawn(true);
		npcSpawn.addListener(new CurrentHpListener());
	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		boolean lock = false;

		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(actor == null || actor.isDead())
			{
				return;
			}

			switch(actor.getNpcId())
			{
				case 23693:
					if(actor.getCurrentHp() + damage < actor.getMaxHp() * 0.5 && !lock)
					{
						lock = true;
						actor.getReflection().spawnByGroup("mystic_tavern_kelbim_4");
					}
					break;
				case 23690:
					if(actor.getCurrentHp() + damage < actor.getMaxHp() * 0.15 && !lock)
					{
						lock = true;

						actor.getFlags().getInvulnerable().start();

						for(Player player : actor.getReflection().getPlayers())
						{
							player.startScenePlayer(SceneMovie.EPIC_KELBIM_SCENE);
						}

						actor.getReflection().startCollapseTimer(26000);

						actor.deleteMe();
					}
					break;
			}
		}
	}

	@Override
	public void onNpcKill(NpcInstance npc, Player killer)
	{
		switch(npc.getNpcId())
		{
			case 23693:
				NpcInstance old = killer.getReflection().getAllByNpcId(34176, true).get(0);
				NpcUtils.spawnSingle(34177, old.getSpawnedLoc(), old.getReflection());
				old.deleteMe();
				killer.getReflection().broadcastPacket(new ExShowScreenMessage(NpcString.THE_DOOR_TO_KELBIMS_THRONE_HAS_OPENED, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				killer.getReflection().openDoor(26150106);
				killer.getReflection().openDoor(26150108);

				NpcInstance kelbim = killer.getReflection().getAllByNpcId(23690, true).get(0);
				kelbim.addListener(new CurrentHpListener());
				break;
//			case 23706:
//			case 23707:
//				int count = killer.getReflection().getAllByNpcId(23706, true).size();
//				count += killer.getReflection().getAllByNpcId(23707, true).size();
//				if(count == 0)
//				{
//					NpcInstance kelbim = killer.getReflection().getAllByNpcId(23690, true).get(0);
//					kelbim.setRunning();
//					kelbim.moveToLocation(new Location(208616, -87336, -570), 0, true);
//					kelbim.addListener(new CurrentHpListener());
//				}
		}
	}
}