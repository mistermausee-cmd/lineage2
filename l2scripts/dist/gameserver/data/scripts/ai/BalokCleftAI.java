package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

/**
 Obi-Wan
 28.10.2016
 */
public class BalokCleftAI extends Fighter
{
	private long last = 0;

	public BalokCleftAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void runImpl()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(System.currentTimeMillis() < last)
			return;

		last = System.currentTimeMillis() + 2000;

		NpcInstance npc = NpcUtils.spawnSingle(29219, actor.getLoc(), actor.getReflection(), 30000);
		npc.setAI(new BalokCleftMonsterAI(npc));
	}
}
