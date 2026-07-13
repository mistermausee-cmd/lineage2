package ai.hermunkus_message;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author : Ragnarok
 * @date : 01.04.12  17:20
 */
public class Transparent_18919 extends DefaultAI
{
	private static final int SKILL_ID = 14649;

	public Transparent_18919(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		addTimer(1, 100);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == 1)
		{
			Skill skill = SkillHolder.getInstance().getSkill(SKILL_ID, 1);
			addTaskBuff(getActor(), skill);
			doTask();
		}
	}

	@Override
	protected void onEvtFinishCasting(Skill skill, Creature target, boolean success)
	{
		if(skill.getId() == SKILL_ID)
			getActor().deleteMe();
	}
}