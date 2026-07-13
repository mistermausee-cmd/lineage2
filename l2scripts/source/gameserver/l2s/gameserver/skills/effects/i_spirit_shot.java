package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;


public final class i_spirit_shot extends i_abstract_effect
{
	private final double _power;

	public i_spirit_shot(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_power = template.getParam().getDouble("power", 100);
		
		
	}

	@Override
	public void instantUse()
	{
		getEffected().sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
		getEffected().setChargedSpiritshotPower(_power);
	}
}