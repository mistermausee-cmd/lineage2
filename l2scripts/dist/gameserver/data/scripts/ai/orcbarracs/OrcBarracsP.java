package ai.orcbarracs;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.ai.Priest;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.NpcUtils;

public class OrcBarracsP extends Priest
{
	public OrcBarracsP(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(60))
		{
			NpcUtils.spawnSingle(23422, getActor().getLoc(), getActor().getReflection());
			killer.sendPacket(new ExShowScreenMessage(NpcString.A_POWERFUL_MONSTER_HAS_COME_TO_FACE_YOU, 5000, ScreenMessageAlign.TOP_CENTER, false));
		}
		super.onEvtDead(killer);
	}


}