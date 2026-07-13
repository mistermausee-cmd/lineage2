package ai.primeval_isle;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 4:57/16.06.2011
 */
public class SprigantStun extends DefaultAI
{
	private final SkillEntry SKILL = SkillHolder.getInstance().getSkillEntry(5085, 1);
	private long _waitTime;

	private static final int TICK_IN_MILISECONDS = 15000;

	public SprigantStun(NpcInstance actor)
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
}
