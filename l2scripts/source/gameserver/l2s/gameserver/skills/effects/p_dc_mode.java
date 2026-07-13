package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class p_dc_mode extends Effect
{
    private final List<Skill> _addedToggles = new ArrayList<Skill>();
    
    public p_dc_mode(Abnormal abnormal, Env env, EffectTemplate template)
    {
        super(abnormal, env, template);
    }
    
    @Override
    public void onStart()
    {
        getEffected().setDualCastEnable(true);
        for(SkillEntry skillEntry : getEffected().getAllSkills())
        {
        	Skill skill = skillEntry.getTemplate();
            if(!skill.isToggleGrouped())
                continue;
            
            if(skill.getToggleGroupId() != 1)
                continue;
            
            if(getEffected().getAbnormalList().contains(skill))
                continue;
            
            skill.getEffects(getEffector(), getEffected(), false);
            _addedToggles.add(skill);
        }
    }
    
    @Override
    public void onExit()
    {
        getEffected().setDualCastEnable(false);
        for(Skill skill : _addedToggles)
            getEffected().getAbnormalList().stop(skill);
    }
}