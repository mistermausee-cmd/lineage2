package ai.nornil;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class LaidoDestructionGolem extends Fighter
{
	public LaidoDestructionGolem(NpcInstance actor)
	{
		super(actor);
	}


	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(Rnd.chance(50))
			NpcUtils.spawnSingle(19296, getActor().getX() + Rnd.get(20, 50), getActor().getY() + Rnd.get(20, 50), getActor().getZ(), 300000);
	}

}