package l2s.gameserver.stats.conditions;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;


public class ConditionPlayerCanLearnSkill extends Condition
{
	private static final AcquireType[] ACQUITE_TYPES_TO_CHECK = { AcquireType.NORMAL, AcquireType.FISHING, AcquireType.TRANSFORMATION, AcquireType.COLLECTION, AcquireType.GENERAL, AcquireType.NOBLESSE, AcquireType.HERO, AcquireType.HONORABLE_NOBLESSE };

	private final int _id;
	private final int _level;

	public ConditionPlayerCanLearnSkill(int id, int level)
	{
		_id = id;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Skill skill = SkillHolder.getInstance().getSkill(_id, _level);
		if(skill == null)
			return false;

		if(!env.character.isPlayer())
			return false;

		Player player = env.character.getPlayer();

		int skillLvl = skill.getLevelWithoutEnchant();
		int haveSkillLvl = 0;

		SkillEntry knownSkillEntry = player.getKnownSkill(skill.getId());
		if(knownSkillEntry != null)
		{
			haveSkillLvl = knownSkillEntry.getTemplate().getLevelWithoutEnchant();
			if(haveSkillLvl >= skillLvl)
				return false;
		}

		if(skillLvl > (haveSkillLvl + 1))
			return false;

		for(AcquireType at : ACQUITE_TYPES_TO_CHECK)
		{
			if(!SkillAcquireHolder.getInstance().isSkillPossible(player, skill, at))
				continue;

			SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), skill.getLevel(), at);
			if(skillLearn == null)
				continue;

			if(SkillAcquireHolder.getInstance().checkLearnCondition(player, skillLearn, player.getLevel(), player.getDualClassLevel()))
				return true;
		}

		return false;
	}
}