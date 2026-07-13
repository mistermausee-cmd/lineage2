package l2s.gameserver.skills.effects;

import java.util.List;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.SkillUtils;

public final class p_aura extends Effect
{
    private static final int SOLIDARITY_SKILL_ID = 1955;
    private final Skill _skill;
    
    public p_aura(Abnormal abnormal, Env env, EffectTemplate template)
    {
        super(abnormal, env, template);
        
        final int id = template.getParam().getInteger("id");
        final int level = template.getParam().getInteger("level", 1);
        final int sub_level = template.getParam().getInteger("sub_level", 0);
        
        _skill = SkillHolder.getInstance().getSkill(id, SkillUtils.getSkillLevelMask(level, sub_level));
    }
    
    @Override
    public int getInterval()
    {
        return 7;
    }
    
    @Override
    public boolean onActionTime()
    {
        if(_skill == null)
            return false;
        
        if(!_skill.isAura())
            return false;
        
        final List<Creature> targets = _skill.getTargets(getEffector(), getEffected(), false);
        for(Creature target : targets)
        {
            if(getSkill().calcEffectsSuccess(getEffector(), target, false))
            {
                if(_skill.isSynergy() || !target.getAbnormalList().contains(getSkill()))
                    _skill.getEffects(getEffector(), target);
                
                if(_skill.isSynergy())
                {
                    int synergyCount = 0;
                    if(target.getAbnormalList().contains(AbnormalType.synergy_sigel))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_tir))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_othel))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_yr))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_feoh))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_wynn))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_eolh))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_ranger))
                        synergyCount++;
                    
                    if(target.getAbnormalList().contains(AbnormalType.synergy_ruler))
                        synergyCount++;
                    
                    target.getAbnormalList().stop(SOLIDARITY_SKILL_ID);
                    
                    int solidarityLevel = 0;
                    if (synergyCount == 3 || synergyCount == 4)
                        solidarityLevel = 1;
                    
                    else if (synergyCount == 5)
                        solidarityLevel = 2;
                    
                    else if (synergyCount >= 6)
                        solidarityLevel = 3;
                    
                    if(solidarityLevel > 0)
                    {
                    	Skill skill = SkillHolder.getInstance().getSkill(SOLIDARITY_SKILL_ID, solidarityLevel);
                        if(skill != null)
                            skill.getEffects(target, target);
                    }
                }
            }
        }
        return true;
    }
}