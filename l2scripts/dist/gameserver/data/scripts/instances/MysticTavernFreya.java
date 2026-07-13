package instances;

import l2s.gameserver.listener.actor.OnAttackListener;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

import java.util.List;

/**
 Eanseen
 08.04.2016
 */
public class MysticTavernFreya extends Reflection
{
	private int stage = 1;

	@Override
	protected void onCreate()
	{
		List<Spawner> spawners = spawnByGroup("mystic_tavern_freya_custom_1");
		for(Spawner spawner : spawners)
		{
			for(NpcInstance npcInstance : spawner.getAllSpawned())
			{
				npcInstance.addListener(new DeathListener("mystic_tavern_freya_custom_1_1"));
			}
		}

		spawners = spawnByGroup("MysticTavernFreyCustom2");
		for(Spawner spawner : spawners)
		{
			for(NpcInstance npcInstance : spawner.getAllSpawned())
			{
				npcInstance.addListener(new AttackListener());
			}
		}

		spawners = spawnByGroup("mystic_tavern_freya_custom_3");
		for(Spawner spawner : spawners)
		{
			for(NpcInstance npcInstance : spawner.getAllSpawned())
			{
				npcInstance.addListener(new DeathListener("mystic_tavern_freya_custom_3_1"));
			}
		}

		spawners = spawnByGroup("MysticTavernFreyCustom4");
		for(Spawner spawner : spawners)
		{
			for(NpcInstance npcInstance : spawner.getAllSpawned())
			{
				npcInstance.addListener(new DeathListener("MysticTavernFreyCustom4_1"));
			}
		}

		spawners = spawnByGroup("mystic_tavern_freya_custom_5");
		for(Spawner spawner : spawners)
		{
			for(NpcInstance npcInstance : spawner.getAllSpawned())
			{
				npcInstance.addListener(new DeathListener("mystic_tavern_freya_custom_5_1"));
			}
		}
	}

	private class DeathListener implements OnDeathListener
	{
		private final String group;

		public DeathListener(String group)
		{
			this.group = group;
		}

		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			switch(victim.getNpcId())
			{
				case 23686:
					spawnByGroup(group);
					if(group.equals("mystic_tavern_freya_custom_3_1"))
					{
						for(Player player : victim.getReflection().getPlayers())
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.AN_INTENSE_COLD_IS_COMING_LOOK_AROUND, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
						}
					}
					break;
			}
		}
	}

	private class AttackListener implements OnAttackListener
	{
		@Override
		public void onAttack(Creature actor, Creature target)
		{
			switch(actor.getNpcId())
			{
				case 23726:
					actor.deleteMe();
					spawnByGroup("mystic_tavern_freya_custom_2_1");
					break;
			}
		}
	}

	public void setStage(int stage)
	{
		this.stage = stage;
	}

	public int getStage()
	{
		return stage;
	}
}