package ai.Balthus;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import bosses.AntharasManager;

//By Evil_dnk

public class BultasMinion extends Fighter
{
	public BultasMinion(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getDebuffImmunity().start();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().setRunning();
		getActor().setRandomWalk(true);

		if(getActor().getNpcId() == 29226)
		{
			for (Playable p : getActor().getReflection().getPlayers())
				notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(getActor().getNpcId() == 29226)
			getActor().doCast(SkillHolder.getInstance().getSkillEntry(14390, 1), getActor(), true);
		super.onEvtDead(killer);
	}

	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		return false;
	}
}