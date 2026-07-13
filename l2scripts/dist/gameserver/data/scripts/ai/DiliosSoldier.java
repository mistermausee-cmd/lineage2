package ai;

import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;

/**
 * @author Bonux
**/
public class DiliosSoldier extends NpcAI
{
	public DiliosSoldier(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtScriptEvent(String event, Object arg1, Object arg2)
	{
		if(event.equalsIgnoreCase("show_animation"))
			addTimer(1525003, 1000);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();

		if(timerId == 1525003)
		{
			actor.setRHandId(14606);
			actor.broadcastCharInfo();
			addTimer(1525004, 2000);
		}
		if(timerId == 1525004)
		{
			actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 4));
			addTimer(1525005, 4000);
		}
		if(timerId == 1525005)
		{
			actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 4));
			addTimer(1525006, 4000);
		}
		if(timerId == 1525006)
		{
			actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 4));
			addTimer(1525007, 4000);
		}
		if(timerId == 1525007)
		{
			actor.setRHandId(13524);
			actor.broadcastCharInfo();
		}
		super.onEvtTimer(timerId, arg1, arg2);
	}
}