package npc.model;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.reflection.OnReflectionCollapseListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.MinionData;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;

public class Kama26BossInstance extends KamalokaBossInstance
{
	private static final long serialVersionUID = 1L;

	private ScheduledFuture<?> _spawner;
	private ReflectionCollapseListener _refCollapseListener = new ReflectionCollapseListener();

	public Kama26BossInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		getMinionList().addMinion(18556, 1, 0);
	}

	@Override
	public void notifyMinionDied(NpcInstance minion)
	{
		_spawner = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MinionSpawner(), 60000, 60000);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		
		getReflection().addListener(_refCollapseListener);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		if(_spawner != null)
			_spawner.cancel(false);
		_spawner = null;
		super.onDeath(killer);
	}

	public class MinionSpawner extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!isDead() && !getMinionList().hasAliveMinions())
			{
				getMinionList().addMinion(18556, 1, 0);
				getMinionList().spawnMinions();
				Functions.npcSayCustomMessage(Kama26BossInstance.this, "Kama26Boss.helpme");
			}
		}
	}

	public class ReflectionCollapseListener implements OnReflectionCollapseListener
	{
		@Override
		public void onReflectionCollapse(Reflection ref)
		{
			if(_spawner != null)
				_spawner.cancel(true);
		}
	}
}