package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

public class Quest421FairyTree extends Fighter
{
	public Quest421FairyTree(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getImmobilized().start();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker != null && attacker.isPlayer())
		{
			Skill tempSkill = SkillHolder.getInstance().getSkill(5423, 12);
			tempSkill.getEffects(actor, attacker);
			return;
		}
		if(attacker.isPet())
		{
			super.onEvtAttacked(attacker, skill, damage);
			return;
		}
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		NpcInstance actor = getActor();
		if(attacker != null && attacker.isPlayer())
		{
			Skill skill = SkillHolder.getInstance().getSkill(5423, 12);
			skill.getEffects(actor, attacker);
			return;
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}