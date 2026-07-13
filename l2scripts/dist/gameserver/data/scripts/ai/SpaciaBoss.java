package ai;

import java.util.concurrent.ScheduledFuture;

import instances.SpaciaNormal;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Skill;
import l2s.gameserver.ThreadPoolManager;
import l2s.commons.threading.RunnableImpl;

/**
 * @author cruel
 */
public class SpaciaBoss extends Fighter
{
	private ScheduledFuture<?> DeadTask;

	//TODO must be 25872-25874 but they invisible
	private int MinionHard = 25780;
	private int MinionNormal = 25780;

	public SpaciaBoss(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		DeadTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnMinion(),1000, 20000);
		Reflection r = actor.getReflection();
		for(Player p : r.getPlayers())
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
		super.onEvtSpawn();
		Skill fp = SkillHolder.getInstance().getSkill(14190, 1);
		fp.getEffects(actor, actor);
	}

	@Override
	public void onEvtDeSpawn()
	{
		super.onEvtDeSpawn();
		if(DeadTask != null)
			DeadTask.cancel(true);
	}


	@Override
	protected void onEvtDead(Creature killer)
	{
		if(DeadTask != null)
			DeadTask.cancel(true);
		super.onEvtDead(killer);
	}
	
	public class SpawnMinion extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if(getActor().getReflection() instanceof SpaciaNormal)
			{
				actor.getReflection().addSpawnWithoutRespawn(MinionNormal, actor.getLoc(), 250);
				for(Player p : actor.getReflection().getPlayers())
					for(NpcInstance minion : actor.getReflection().getAllByNpcId(MinionNormal, true))
						minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
			}
			else
			{
				actor.getReflection().addSpawnWithoutRespawn(MinionHard, actor.getLoc(), 250);
				for(Player p : actor.getReflection().getPlayers())
					for(NpcInstance minion : actor.getReflection().getAllByNpcId(MinionHard, true))
						minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
			}
		}
	}
}
