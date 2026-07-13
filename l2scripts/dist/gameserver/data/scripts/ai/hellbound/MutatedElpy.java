package ai.hellbound;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.instancemanager.naia.NaiaCoreManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */
public class MutatedElpy extends Fighter
{
	public MutatedElpy(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getImmobilized().start();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NaiaCoreManager.launchNaiaCore();
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		actor.doDie(attacker);
	}

}