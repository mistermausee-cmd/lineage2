package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;

/**
 * @author Bonux
**/
public class FortressOfTheDeadQ10752 extends Reflection
{
	// Monster's
	private static final int VON_HELLMANN = 19566;	// Фон Хельман
	private static final int VAMPIRIC_SOLDIER = 19567;	// Солдат Вампиров

	// Spawn groups
	private static final String SPAWN_GROUP_1 = "fortress_of_the_dead_q10752_1";
	private static final String SPAWN_GROUP_2 = "fortress_of_the_dead_q10752_2";
	private static final String SPAWN_GROUP_3 = "fortress_of_the_dead_q10752_3";

	private final List<ScheduledFuture<?>> _tasks = new ArrayList<ScheduledFuture<?>>();
	private final OnDeathListener _deathListener = new DeathListener();

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc())
			{
				int npcId = self.getNpcId();
				if(npcId == VON_HELLMANN)
				{
					despawnByGroup(SPAWN_GROUP_2);

					SceneMovie scene = SceneMovie.ERTHEIA_QUEST_A;
					for(Player player : getPlayers())
						player.startScenePlayer(scene);

					_tasks.add(ThreadPoolManager.getInstance().schedule(new EndFirstScene(), scene.getDuration()));
				}
			}
		}
	}

	public class EndFirstScene extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			spawnByGroup(SPAWN_GROUP_3);
		}
	}

	@Override
	protected void onCollapse()
	{
		super.onCollapse();

		for(ScheduledFuture<?> task : _tasks)
		{
			if(task != null)
				task.cancel(false);
		}
	}

	@Override
	public void addObject(GameObject o)
	{
		super.addObject(o);

		if(o.isCreature())
			((Creature) o).addListener(_deathListener);
	}

	@Override
	public void removeObject(GameObject o)
	{
		super.removeObject(o);

		if(o.isNpc())
		{
			int npcId = ((NpcInstance) o).getNpcId();
			if(npcId == VAMPIRIC_SOLDIER)
			{
				List<NpcInstance> npcs = getAllByNpcId(VAMPIRIC_SOLDIER, false);
				if(npcs.isEmpty())
				{
					despawnByGroup(SPAWN_GROUP_1);

					List<Spawner> spawners = spawnByGroup(SPAWN_GROUP_2);
					for(Spawner spawner : spawners)
					{
						npcs = spawner.getAllSpawned();
						for(NpcInstance npc : npcs)
						{
							if(npc.getNpcId() == VON_HELLMANN)
							{
								for(Player player : getPlayers())
									npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 1000);
							}
						}
					}
				}
			}
		}
	}
}