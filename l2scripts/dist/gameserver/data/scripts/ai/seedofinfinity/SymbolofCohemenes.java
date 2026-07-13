package ai.seedofinfinity;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */

public class SymbolofCohemenes extends DefaultAI
{

	public SymbolofCohemenes(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getImmobilized().start();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}