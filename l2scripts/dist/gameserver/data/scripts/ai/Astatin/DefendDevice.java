package ai.Astatin;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;

//By Evil_dnk

public class DefendDevice extends DefaultAI
{
	private final SkillEntry SKILL = SkillHolder.getInstance().getSkillEntry(23699, 1);
	private long _waitTime;
	private static final int TICK_IN_MILISECONDS = 15000;

	public DefendDevice(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if(System.currentTimeMillis() > _waitTime)
		{
			NpcInstance actor = getActor();

			actor.doCast(SKILL, actor, false);

			_waitTime = System.currentTimeMillis() + TICK_IN_MILISECONDS;
			return true;
		}
		return false;
	}

	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
	}
}
