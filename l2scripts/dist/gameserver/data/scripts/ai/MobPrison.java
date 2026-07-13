package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.NpcUtils;

public class MobPrison extends Fighter
{
	private int time = 18;
	
	public MobPrison(NpcInstance actor)
	{
		super(actor);
		_activeAITaskDelay = 1000;
	}
	
	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		actor.setTitle(String.valueOf(time));
		time--;

		actor.broadcastCharInfoImpl(NpcInfoType.TITLE);

		if (time <= 0)
		{
			if(actor.getNpcId() == 22980)
				NpcUtils.spawnSingle(22966, actor.getLoc());
			else if(actor.getNpcId() == 22979)
				NpcUtils.spawnSingle(22965, actor.getLoc());
			else if(actor.getNpcId() == 22981)
				NpcUtils.spawnSingle(22967, actor.getLoc());
			actor.deleteMe();
		}

		return super.thinkActive();
	}

}