package instances;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.utils.ItemFunctions;

import java.util.List;

/**
 Obi-Wan
 20.06.2016
 */
public class MysticTavernKelbim extends Reflection
{
	@Override
	protected void onCreate()
	{
		List<Spawner> spawners = spawnByGroup("mystic_tavern_kelbim_0");
		for(Spawner spawner : spawners)
		{
			spawner.getAllSpawned().forEach(npcInstance -> npcInstance.addListener(new DeathListener(23691, "mystic_tavern_kelbim_0_1")));
		}

		ThreadPoolManager.getInstance().schedule(() -> spawnByGroup("mystic_tavern_kelbim_1_1"), 60000);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			List<Spawner> spawners2 = spawnByGroup("mystic_tavern_kelbim_1_2");
			for(Spawner spawner : spawners2)
			{
				spawner.getAllSpawned().stream().filter(npcInstance -> npcInstance.getNpcId() == 23713).forEach(npcInstance -> npcInstance.addListener(new DeathListener(23713, "mystic_tavern_kelbim_1_2_1")));
			}
		}, 60000);

		ThreadPoolManager.getInstance().schedule(() -> spawnByGroup("mystic_tavern_kelbim_1_3"), 60000);

		spawners = spawnByGroup("mystic_tavern_kelbim_2");
		for(Spawner spawner : spawners)
		{
			spawner.getAllSpawned().forEach(npcInstance -> npcInstance.addListener(new DeathListener(23692, "")));
		}
	}

	private class DeathListener implements OnDeathListener
	{
		private final int npcId;
		private final String group;

		public DeathListener(int npcId, String group)
		{
			this.npcId = npcId;
			this.group = group;
		}

		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if(victim.getNpcId() == npcId && !group.isEmpty())
			{
				spawnByGroup(group);
			}
			if(victim.getNpcId() == 23691)
			{
				Player player = killer.getPlayer();
				if(player != null)
				{
					if(player.isInParty())
					{
						ItemFunctions.addItem(player.getParty().getPartyLeader(), 46555, 1);
					}
					else
					{
						ItemFunctions.addItem(player, 46555, 1);
					}
				}
			}
			else if(victim.getNpcId() == 23692)
			{
				Player player = killer.getPlayer();
				if(player != null)
				{
					if(player.isInParty())
					{
						ItemFunctions.addItem(player.getParty().getPartyLeader(), 46556, 1);
					}
					else
					{
						ItemFunctions.addItem(player, 46556, 1);
					}
				}

				ThreadPoolManager.getInstance().execute(() ->
				{
					List<Spawner> spawners2 = spawnByGroup("mystic_tavern_kelbim_2_1");
					for(Spawner spawner : spawners2)
					{
						spawner.getAllSpawned().stream().filter(npcInstance -> npcInstance.getNpcId() == 23713).forEach(npcInstance -> npcInstance.addListener(new DeathListener(23713, "mystic_tavern_kelbim_2_1_1")));
					}
				});

				ThreadPoolManager.getInstance().schedule(() ->
				{
					List<Spawner> spawners2 = spawnByGroup("mystic_tavern_kelbim_2_2");
					for(Spawner spawner : spawners2)
					{
						spawner.getAllSpawned().stream().filter(npcInstance -> npcInstance.getNpcId() == 23713).forEach(npcInstance -> npcInstance.addListener(new DeathListener(23713, "mystic_tavern_kelbim_2_1_1")));
					}
				}, 60000);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					List<Spawner> spawners2 = spawnByGroup("mystic_tavern_kelbim_2_3");
					for(Spawner spawner : spawners2)
					{
						spawner.getAllSpawned().stream().filter(npcInstance -> npcInstance.getNpcId() == 23713).forEach(npcInstance -> npcInstance.addListener(new DeathListener(23713, "mystic_tavern_kelbim_2_1_1")));
					}
				}, 120000);
			}
		}
	}
}