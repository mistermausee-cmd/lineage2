package l2s.gameserver.model;

import l2s.commons.lang.reference.HardReference;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.actor.instances.creature.Abnormal;


public class SkillChain
{
	private final HardReference<? extends Creature> _target;
	private final HardReference<? extends Player> _actor;
	private final int _castingSkill;
	private final Skill _chainSkill;

	public SkillChain(Player actor,Creature  target, int castingId, int chainId)
	{
		_actor = actor.getRef();
		_target = target.getRef();
		_castingSkill = castingId;
		_chainSkill = SkillHolder.getInstance().getSkill(actor.getKnownSkill(chainId).getTemplate().getChainSkillId(), 1);
	}

	public Creature getTarget()
	{
		return _target.get();
	}

	public int getCastingSkill()
	{
		return _castingSkill;
	}

	public Skill getChainSkill()
	{
		return _chainSkill;
	}

	public boolean isActive()
	{
		if(_target.get() != null && _actor.get() != null)
		{
			for(Abnormal e : _target.get().getAbnormalList())
			{
				if(e.getSkill().getId() == _castingSkill && e.getEffector() == _actor.get())
					return true;
			}
		}
		return false;
	}
}