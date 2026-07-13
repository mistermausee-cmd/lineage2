package l2s.gameserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillEnchantInfoHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.EnchantType;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.SkillEnchantInfo;


public final class SkillUtils
{
    public static int generateSkillHashCode(int id, int level)
    {
        return id * 1000 + level;
    }

    public static int getSubSkillLevel(int enchantType, int enchantLevel)
    {
    	if(enchantLevel > 0 && enchantLevel > 0)
    		return enchantType * 1000 + enchantLevel;
      
    	return 0;
    }
    
    public static int getSkillLevelMask(int skillLevel, int subSkillLevel)
    {
    	return skillLevel | subSkillLevel << 16;
    }
    
    public static boolean isEnchantedSkill(int level)
    {
    	return getSkillEnchantLevel(level) > 0;
    }
    
    public static int getSkillEnchantType(int level)
    {
    	int subSkillLevel = getSubSkillLevelFromMask(level);
    	return subSkillLevel / 1000;
    }
    
    public static int getSkillEnchantLevel(int level)
    {
    	int subSkillLevel = getSubSkillLevelFromMask(level);
    	if(subSkillLevel > 1000)
    		return subSkillLevel % 1000;
      
    	return 0;
    }
    
    public static int getSkillLevelFromMask(int skillLevelMask)
    {
    	final int mask = 0b1111111111111111;
    	return mask & skillLevelMask;
    }
    
    public static int getSubSkillLevelFromMask(int skillLevelMask)
    {
    	final int mask = 0b1111111111111111;
    	return mask & skillLevelMask >>> 16;
    }
    
    public static int getSkillIdFromPTSHash(int hash)
    {
        final int mask = 0b1111111111111111;
        return mask & hash >>> 16;
    }

    public static int getSkillLevelFromPTSHash(int hash)
    {
        final int mask = 0b1111111111111111;
        return mask & hash;
    }

    public static int getSkillIdFromPTSLongHash(long hash)
    {
        final int mask = 0b1111111111111111;
        return (int) (mask & hash >>> 32);
    }

    public static int getSkillLevelFromPTSLongHash(long hash)
    {
        final int mask = 0b1111111111111111;
        return (int) (mask & hash);
    }

    public static boolean checkSkill(Player player, SkillEntry skillEntry)
    {
        if(!Config.ALT_REMOVE_SKILLS_ON_DELEVEL)
            return false;

        SkillLearn learn = SkillAcquireHolder.getInstance().getSkillLearn(player, skillEntry.getId(), skillEntry.getTemplate().getLevelWithoutEnchant(), AcquireType.NORMAL);
        if(learn == null)
            return false;
        
        int subSkillLevel = getSubSkillLevelFromMask(skillEntry.getTemplate().getLevel());
        
        boolean update = false;

        int lvlDiff = learn.isFreeAutoGet(AcquireType.NORMAL) ? 1 : 4;
        if(learn.getMinLevel() >= (player.getLevel() + lvlDiff) || learn.getDualClassMinLvl() >= player.getDualClassLevel() + lvlDiff)
        {
            player.removeSkill(skillEntry, true);

            
            
            for(int i = skillEntry.getTemplate().getLevelWithoutEnchant() - 1; i != 0; i--)
            {
                SkillLearn learn2 = SkillAcquireHolder.getInstance().getSkillLearn(player, skillEntry.getId(), i, AcquireType.NORMAL);
                if(learn2 == null)
                    continue;

                int lvlDiff2 = learn2.isFreeAutoGet(AcquireType.NORMAL) ? 1 : 4;
				if(learn2.getMinLevel() >= (player.getLevel() + lvlDiff2) || learn2.getDualClassMinLvl() >= (player.getDualClassLevel() + lvlDiff2))
                    continue;

                SkillEntry newSkillEntry = SkillHolder.getInstance().getSkillEntry(skillEntry.getId(), getSkillLevelMask(i, subSkillLevel));
                if(newSkillEntry == null)
                    newSkillEntry = SkillHolder.getInstance().getSkillEntry(skillEntry.getId(), i);
                if(newSkillEntry != null)
                {
                    player.addSkill(newSkillEntry, true);
                    break;
                }
            }
            update = true;
        }

        if(player.isTransformed())
        {
            learn = player.getTransform().getAdditionalSkill(skillEntry.getId(), skillEntry.getLevel());
            if(learn == null)
                return false;

            if(learn.getMinLevel() >= player.getLevel() + 1)
            {
                player.removeTransformSkill(skillEntry);
                player.removeSkill(skillEntry, false);

                for(int i = skillEntry.getTemplate().getLevelWithoutEnchant() - 1; i != 0; i--)
                {
                    SkillLearn learn2 = player.getTransform().getAdditionalSkill(skillEntry.getId(), i);
                    if(learn2 == null)
                        continue;

                    if(learn2.getMinLevel() >= player.getLevel() + 1)
                        continue;

                    SkillEntry newSkillEntry = SkillHolder.getInstance().getSkillEntry(skillEntry.getId(), getSkillLevelMask(i, subSkillLevel));
                    if(newSkillEntry == null)
                        newSkillEntry = SkillHolder.getInstance().getSkillEntry(skillEntry.getId(), i);
                    if(newSkillEntry != null)
                    {
                        player.addTransformSkill(newSkillEntry);
                        player.addSkill(newSkillEntry, false);
                        break;
                    }
                }
                update = true;
            }
        }
        return update;
    }
    
    public static List<Skill> getSkillsForChangeEnchant(int id, int level)
	{
		final int enchantLevel = getSkillEnchantLevel(level);
		if(enchantLevel <= 0)
			return Collections.emptyList();

		final int skillLevel = getSkillLevelFromMask(level);
		final int enchantType = getSkillEnchantType(level);
		final List<Skill> skills = new ArrayList<Skill>();
		for(SkillEntry skillEntry : SkillHolder.getInstance().getSkills(id))
		{
			Skill skill = skillEntry.getTemplate();
			if(skill.isEnchantable() && enchantType != getSkillEnchantType(skill.getLevel()) && skillLevel == skill.getLevelWithoutEnchant() && getSkillEnchantLevel(skill.getLevel()) == enchantLevel)
				skills.add(skill);
		}
		return skills;
	}

	public static List<Skill> getSkillsForFirstEnchant(int id, int level)
	{
		final int skillLevel = getSkillLevelFromMask(level);

		List<Skill> skills = new ArrayList<Skill>();
		for(SkillEntry skillEntry : SkillHolder.getInstance().getSkills(id))
		{
			Skill skill = skillEntry.getTemplate();
			if(skill.isEnchantable() && skillLevel == skill.getLevelWithoutEnchant() && getSkillEnchantLevel(skill.getLevel()) == 1)
				skills.add(skill);
		}
		return skills;
	}
    
    public static List<Skill> getAvaiableEnchantSkills(Player player)
	{
		List<Skill> enchants = new ArrayList<Skill>();
		for(SkillEntry skillEntry : player.getAllSkills())
		{
			if(skillEntry.getTemplate().isEnchantable())
			{
				if(isEnchantedSkill(skillEntry.getLevel()))
				{
					int skillLevel = getSkillLevelFromMask(skillEntry.getLevel());
					int subSkillLevel = getSubSkillLevelFromMask(skillEntry.getLevel()) + 1;
					int skillLevelMask = getSkillLevelMask(skillLevel, subSkillLevel);
					Skill enchant = SkillHolder.getInstance().getSkill(skillEntry.getId(), skillLevelMask);
					if(enchant != null)
						enchants.add(enchant);
				}
				else
				{
					for(SkillEntry temp : SkillHolder.getInstance().getSkills(skillEntry.getId()))
					{
						Skill enchant = temp.getTemplate();
						if(getSkillEnchantLevel(enchant.getLevel()) == 1)
							enchants.add(enchant);
					}
				}
			}
		}
		return enchants;
	}
    
    public static Skill getNextEnchantSkill(int id, int level)
    {
    	int skillLevel = getSkillLevelFromMask(level);
    	int subSkillLevel = getSubSkillLevelFromMask(level) + 1;
    	int skillLevelMask = getSkillLevelMask(skillLevel, subSkillLevel);
    	Skill skill = SkillHolder.getInstance().getSkill(id, skillLevelMask);
    	if(skill != null && skill.isEnchantable())
    		return skill;

      return null;
    }

	public static boolean isSkillEnchantAvailable(Player player, Skill skill)
	{
		if(player.isTransformed())
			return false;

		return skill.isEnchantable();
	}

    public static boolean isAvailableSkillEnchant(Player player, Skill skill, EnchantType type)
	{
		SkillEntry baseSkillEntry = player.getKnownSkill(skill.getId());
		if(baseSkillEntry == null)
			return false;

		Skill baseSkill = baseSkillEntry.getTemplate();
		if(!baseSkill.isEnchantable())
			return false;

		if(baseSkill.getLevelWithoutEnchant() != baseSkill.getLevelWithoutEnchant())
			return false;

		if(type == EnchantType.NORMAL || type == EnchantType.BLESSED || type == EnchantType.IMMORTAL)
		{
			if(getSkillEnchantLevel(skill.getLevel()) == (getSkillEnchantLevel(baseSkill.getLevel()) + 1))
				return true;
		}
		else if(type == EnchantType.UNTRAIN)
		{
			if(isEnchantedSkill(baseSkill.getLevel()) && (getSkillEnchantLevel(baseSkill.getLevel()) - 1) == getSkillEnchantLevel(skill.getLevel()))
				return true;
		}
		else if(type == EnchantType.CHANGE)
		{
			if(isEnchantedSkill(baseSkill.getLevel()) && isEnchantedSkill(skill.getLevel()))
			{
				if(getSkillEnchantLevel(baseSkill.getLevel()) == getSkillEnchantLevel(skill.getLevel()))
					return true;
			}
		}

		return false;
	}
    
    public static int getSafeEnchantLevel(int enchantLevel)
    {
    	for(int i = enchantLevel - 1; i > 0; i--)
    	{
    		SkillEnchantInfo info = SkillEnchantInfoHolder.getInstance().getInfo(i);
    		if(info != null && info.isSafe())
    			return i;
    	}
    	return 0;
    }
}