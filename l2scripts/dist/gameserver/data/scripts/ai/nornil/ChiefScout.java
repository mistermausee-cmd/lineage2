package ai.nornil;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class ChiefScout extends Fighter
{
	private boolean _spawned = false;

	public ChiefScout(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(final Creature attacker, Skill skill, int damage)
	{
		final NpcInstance actor = getActor();
		if(attacker != null  && actor.getCurrentHpPercents() < 50 && !_spawned)
		{
			_spawned = true;
			NpcInstance helper1 = NpcUtils.spawnSingle(23270, getActor().getX() + Rnd.get(20, 50), getActor().getY() + Rnd.get(20, 50), getActor().getZ());
			NpcInstance helper2 = NpcUtils.spawnSingle(23271, getActor().getX() + Rnd.get(20, 50), getActor().getY() + Rnd.get(20, 50), getActor().getZ());
			helper1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
			helper2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}