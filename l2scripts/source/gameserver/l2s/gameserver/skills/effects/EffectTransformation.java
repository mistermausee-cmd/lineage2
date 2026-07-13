package l2s.gameserver.skills.effects;

import l2s.commons.string.StringArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.player.transform.TransformTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectTransformation extends Effect
{
	private final int[] _transformIds;
	private int _transformedId = 0;
	
	public EffectTransformation(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_transformIds = StringArrayUtils.stringToIntArray(template.getParam().getString("id", String.valueOf((int)template.getValue())), "[\\s,;]+");
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected() != getEffector())
			return false;

		if(!getEffected().isPlayer())
			return false;

	    for(int transformId : _transformIds)
	    {
	    	if(transformId > 0)
	    	{
	    		TransformTemplate template = TransformTemplateHolder.getInstance().getTemplate(getEffected().getSex(), transformId);
	    		if(template == null)
	    			return false;
	        
	        if(template.getType() == TransformType.FLYING && getEffected().getX() > -166168)
	        	return false;
	    	}
	    }
	    return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		_transformedId = Rnd.get(_transformIds);
	    getEffected().setTransform(_transformedId);
	}

	@Override
	public void onExit()
	{
		if(_transformedId > 0)
		      getEffected().setTransform(null);
	}
}