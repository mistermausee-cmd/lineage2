package ai.ShadowCapm;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.AbnormalEffect;

//By Evil_dnk

public class AshesCaptive extends DefaultAI
{
	public AshesCaptive(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().getFlags().getParalyzed().start();
		getActor().startAbnormalEffect(AbnormalEffect.FLESH_STONE);
	}

}