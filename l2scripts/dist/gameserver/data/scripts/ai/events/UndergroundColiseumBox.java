package ai.events;

import java.util.concurrent.Future;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 17:59/19.05.2012
 */
public class UndergroundColiseumBox extends DefaultAI
{
	private Future<?> _despawnTask;

	public UndergroundColiseumBox(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();

		_despawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			protected void runImpl() throws Exception
			{
				getActor().decayOrDelete();
			}
		}, 20000L);
	}

	@Override
	public void onEvtDeSpawn()
	{
		super.onEvtDeSpawn();

		cancel();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		cancel();
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		//
	}

	private void cancel()
	{
		if(_despawnTask != null)
		{
			_despawnTask.cancel(false);
			_despawnTask = null;
		}
	}
}
