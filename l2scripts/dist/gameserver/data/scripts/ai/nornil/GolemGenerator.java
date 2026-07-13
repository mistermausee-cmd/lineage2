package ai.nornil;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class GolemGenerator extends Fighter
{
	public GolemGenerator(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(() ->
		{
			getActor().doDie(null);
			NpcUtils.spawnSingle(23269, getActor().getX() + Rnd.get(20, 50), getActor().getY() + Rnd.get(20, 50), getActor().getZ(), 300000);
			NpcUtils.spawnSingle(23269, getActor().getX() + Rnd.get(20, 50), getActor().getY() + Rnd.get(20, 50), getActor().getZ(), 300000);
		}, 10000L);
	}
}