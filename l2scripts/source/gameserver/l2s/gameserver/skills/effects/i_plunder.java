package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;


public class i_plunder extends i_spoil
{
    public i_plunder(Abnormal abnormal, Env env, EffectTemplate template)
    {
        super(abnormal, env, template);
    }

    @Override
    protected void doSpoil(boolean success)
    {
        MonsterInstance monster = (MonsterInstance) getEffected();
        if (success)
        {
            if(monster.isRobbed() > 0)
                return;

            monster.setSpoiled(getEffector().getPlayer());
            
            for(RewardList rewardList : monster.getTemplate().getRewards())
                if(rewardList.getType() == RewardType.SWEEP)
                	monster.rollRewards(rewardList, getEffector(), getEffector());
                	
            if(monster.takeSweep(getEffector().getPlayer()))
            {
                monster.setRobbed(2);
                return;
            }
        }
        monster.clearSweep();
        monster.setRobbed(1);
    }
}