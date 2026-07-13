package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;


public final class EffectDamageBlock extends Effect
{
	private final boolean _withException;

	public EffectDamageBlock(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_withException = template.getParam().getBool("with_exception", false);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDamageBlocked().start(this);
		if(_withException)
		{
			if(getEffected() == getEffector())
			{
		        if(getSkill() == getEffector().getCastingSkill())
		        	getEffected().setDamageBlockedException(getEffector().getCastingTarget());
		        else if(getSkill() == getEffector().getDualCastingSkill())
		        	getEffected().setDamageBlockedException(getEffector().getDualCastingTarget());
			}
			else
				getEffected().setDamageBlockedException(getEffector());
		}
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDamageBlocked().stop(this);
		getEffected().setDamageBlockedException(null);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}