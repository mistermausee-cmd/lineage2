package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.NpcUtils;

public class Windvortex extends Fighter
{
	public Windvortex(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcUtils.spawnSingle(Rnd.get(23419, 23420), getActor().getLoc(), getActor().getReflection());
		killer.sendPacket(new ExShowScreenMessage(NpcString.A_POWERFUL_MONSTER_HAS_COME_TO_FACE_YOU, 5000, ScreenMessageAlign.TOP_CENTER, false));
		super.onEvtDead(killer);
	}


}