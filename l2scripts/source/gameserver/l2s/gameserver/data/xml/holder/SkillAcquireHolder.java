package l2s.gameserver.data.xml.holder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.MulticlassUtils;
import l2s.gameserver.utils.SkillUtils;


public final class SkillAcquireHolder extends AbstractHolder
{
	private static final SkillAcquireHolder _instance = new SkillAcquireHolder();

	public static SkillAcquireHolder getInstance()
	{
		return _instance;
	}

	
	private TIntObjectMap<Set<SkillLearn>> _normalSkillTree = new TIntObjectHashMap<Set<SkillLearn>>();
	private TIntObjectMap<Set<SkillLearn>> _generalSkillTree = new TIntObjectHashMap<Set<SkillLearn>>();
	private TIntObjectMap<Set<SkillLearn>> _transferSkillTree = new TIntObjectHashMap<Set<SkillLearn>>();
	private TIntObjectMap<Set<SkillLearn>> _dualClassSkillTree = new TIntObjectHashMap<Set<SkillLearn>>();
	private TIntObjectMap<TIntObjectMap<Set<SkillLearn>>> _awakeParentSkillTree = new TIntObjectHashMap<TIntObjectMap<Set<SkillLearn>>>();

	private TIntObjectMap<Set<SkillLearn>> _multiclassCheckSkillTree = new TIntObjectHashMap<Set<SkillLearn>>();
	private TIntObjectMap<TIntObjectMap<Set<SkillLearn>>> _multiclassLearnSkillTree = new TIntObjectHashMap<TIntObjectMap<Set<SkillLearn>>>();
	
	private Set<SkillLearn> _fishingSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _transformationSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _certificationSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _dualCertificationSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _collectionSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _pledgeSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _subUnitSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _noblesseSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _heroSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _gmSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _customSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _chaosSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _dualChaosSkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _abilitySkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _alchemySkillTree = new HashSet<SkillLearn>();
	private Set<SkillLearn> _honorNobleSkillTree = new HashSet<SkillLearn>();
	
    private int _abilitiesMinLevel = 0;
    private long _abilitiesRefreshPrice = 0L;
    private int _maxAbilitiesPoints = 0;

	private Collection<SkillLearn> getSkills(Player player, ClassId classId, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = getNormalSkillTree(player);
				if(skills == null)
				{
					info("Skill tree for class " + player.getActiveClassId() + " is not defined !");
					return Collections.emptyList();
				}
				break;
			case COLLECTION:
				skills = _collectionSkillTree;
				if(skills == null)
				{
					info("Collection skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				if(skills == null)
				{
					info("Transformation skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_CARDINAL:
				skills = _transferSkillTree.get(type.transferClassId());
				if(skills == null)
				{
					info("Transfer skill tree for class " + type.transferClassId() + " is not defined !");
					return Collections.emptyList();
				}
				break;
			case FISHING:
				skills = _fishingSkillTree;
				if(skills == null)
				{
					info("Fishing skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case CLAN:
				skills = _pledgeSkillTree;
				if(skills == null)
				{
					info("Pledge skill tree is not defined !");
					return Collections.emptyList();
				}
				return checkLearnsConditions(player, skills, player.getClan() != null ? player.getClan().getLevel() : 0, 0);
			case SUB_UNIT:
				skills = _subUnitSkillTree;
				if(skills == null)
				{
					info("Sub-unit skill tree is not defined !");
					return Collections.emptyList();
				}
				return checkLearnsConditions(player, skills, player.getClan() != null ? player.getClan().getLevel() : 0, 0);
			case CERTIFICATION:
				skills = _certificationSkillTree;
				if(skills == null)
				{
					info("Certification skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				if(skills == null)
				{
					info("Dual certification skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case NOBLESSE:
				skills = _noblesseSkillTree;
				if(skills == null)
				{
					info("Noblesse skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case HERO:
				skills = _heroSkillTree;
				if(skills == null)
				{
					info("Hero skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case GM:
				skills = _gmSkillTree;
				if(skills == null)
					return Collections.emptyList();
				break;
			case CUSTOM:
				skills = _customSkillTree;
				if(skills == null)
					return Collections.emptyList();
				break;
			case CHAOS:
				skills = _chaosSkillTree;
				if(skills == null)
				{
					info("Chaos skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case DUAL_CHAOS:
				skills = _dualChaosSkillTree;
				if(skills == null)
				{
					info("Dual chaos skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case ABILITY:
				skills = _abilitySkillTree;
				if(skills == null)
				{
					info("Ability skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case ALCHEMY:
				skills = _alchemySkillTree;
				if(skills == null)
				{
					info("Alchemy skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case HONORABLE_NOBLESSE:
				skills = _honorNobleSkillTree;
				if(skills == null)
				{
					info("Honorable noblesse skill tree is not defined !");
					return Collections.emptyList();
				}
				break;
			case MULTICLASS:
				if(Config.MULTICLASS_SYSTEM_ENABLED)
				{
					if(classId != null)
					{
						TIntObjectMap<Set<SkillLearn>> map = _multiclassLearnSkillTree.get(player.getActiveClassId());
						if(map == null)
						{
							info("Skill tree for learn multiclass " + player.getActiveClassId() + " is not defined !");
							return Collections.emptyList();
						}

						skills = map.get(classId.getId());
						if(skills == null)
						{
							info("Skill tree for learn multiclass " + player.getActiveClassId() + ":" + classId.getId() + " is not defined !");
							return Collections.emptyList();
						}
					}
					else
					{
						skills = _multiclassCheckSkillTree.get(player.getActiveClassId());
						if(skills == null)
						{
							info("Skill tree for check multiclass " + player.getActiveClassId() + " is not defined !");
							return Collections.emptyList();
						}
					}
				}
				else
					return Collections.emptyList();
				break;
			default:
				return Collections.emptyList();
		}

		if(player == null)
			return skills;

		return checkLearnsConditions(player, skills, player.getLevel(), player.getDualClassLevel());
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type)
	{
		return getAvailableSkills(player, null, type, null);
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type, SubUnit subUnit)
	{
		return getAvailableSkills(player, null, type, subUnit);
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, ClassId classId, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills = getSkills(player, classId, type, subUnit);
		switch(type)
		{
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_CARDINAL:
				if(player == null)
					return skills;
				else
				{
					Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
					for(SkillLearn temp : skills)
					{
						int knownLevel = player.getSkillLevel(temp.getId());
						if(knownLevel == -1)
							skillLearnMap.put(temp.getId(), temp);
					}
					return skillLearnMap.values();
				}
			case CLAN:
				Collection<SkillEntry> clanSkills = player.getClan().getSkills();
				return getAvaliableList(skills, clanSkills.toArray(new SkillEntry[clanSkills.size()]));
			case SUB_UNIT:
				Collection<SkillEntry> subUnitSkills = subUnit.getSkills();
				return getAvaliableList(skills, subUnitSkills.toArray(new SkillEntry[subUnitSkills.size()]));
			case ALCHEMY:
				if(player != null)
					return getAvaliableList(skills, player.getAllAlchemySkillsArray());
		}

		if(player == null)
			return skills;

		return getAvaliableList(skills, player.getAllSkillsArray());
	}

	private Collection<SkillLearn> getAvaliableList(Collection<SkillLearn> skillLearns, SkillEntry[] skills)
	{
		TIntIntMap skillLvls = new TIntIntHashMap();
		for(SkillEntry skillEntry : skills)
		{
			if(skillEntry == null)
				continue;
			skillLvls.put(skillEntry.getId(), skillEntry.getTemplate().getLevelWithoutEnchant());
		}

		Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
		for(SkillLearn temp : skillLearns)
		{
			int skillId = temp.getId();
			int skillLvl = temp.getLevel();
			if(!skillLvls.containsKey(skillId) && skillLvl == 1 || skillLvls.containsKey(skillId) && (skillLvl - skillLvls.get(skillId)) == 1)
				skillLearnMap.put(temp.getId(), temp);
		}

		return skillLearnMap.values();
	}

	public Collection<SkillLearn> getAvailableNextLevelsSkills(Player player, AcquireType type)
	{
		return getAvailableNextLevelsSkills(player, null, type, null);
	}

	public Collection<SkillLearn> getAvailableNextLevelsSkills(Player player, AcquireType type, SubUnit subUnit)
	{
		return getAvailableNextLevelsSkills(player, null, type, subUnit);
	}

	public Collection<SkillLearn> getAvailableNextLevelsSkills(Player player, ClassId classId, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills = getSkills(player, classId, type, subUnit);
		switch(type)
		{
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_CARDINAL:
				if(player == null)
					return skills;
				else
				{
					Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
					for(SkillLearn temp : skills)
					{
						int knownLevel = player.getSkillLevel(temp.getId());
						if(knownLevel == -1)
							skillLearnMap.put(temp.getId(), temp);
					}
					return skillLearnMap.values();
				}
			case CLAN:
				Collection<SkillEntry> clanSkills = player.getClan().getSkills();
				return getAvailableNextLevelsList(skills, clanSkills.toArray(new SkillEntry[clanSkills.size()]));
			case SUB_UNIT:
				Collection<SkillEntry> subUnitSkills = subUnit.getSkills();
				return getAvailableNextLevelsList(skills, subUnitSkills.toArray(new SkillEntry[subUnitSkills.size()]));
			case ALCHEMY:
				if(player != null)
					return getAvailableNextLevelsList(skills, player.getAllAlchemySkillsArray());
		}

		if(player == null)
			return skills;

		return getAvailableNextLevelsList(skills, player.getAllSkillsArray());
	}

	private Collection<SkillLearn> getAvailableNextLevelsList(Collection<SkillLearn> skillLearns, SkillEntry[] skills)
	{
		TIntIntMap skillLvls = new TIntIntHashMap();
		for(SkillEntry skillEntry : skills)
		{
			if(skillEntry == null)
				continue;
			skillLvls.put(skillEntry.getId(), skillEntry.getTemplate().getLevelWithoutEnchant());
		}

		Set<SkillLearn> skillLearnsList = new HashSet<SkillLearn>();
		for(SkillLearn temp : skillLearns)
		{
			int skillId = temp.getId();
			int skillLvl = temp.getLevel();
			if(!skillLvls.containsKey(skillId) || skillLvls.containsKey(skillId) && skillLvl > skillLvls.get(skillId))
				skillLearnsList.add(temp);
		}

		return skillLearnsList;
	}

	public Collection<SkillLearn> getAvailableMaxLvlSkills(Player player, AcquireType type)
	{
		return getAvailableMaxLvlSkills(player, null, type, null);
	}

	public Collection<SkillLearn> getAvailableMaxLvlSkills(Player player, AcquireType type, SubUnit subUnit)
	{
		return getAvailableMaxLvlSkills(player, null, type, subUnit);
	}

	public Collection<SkillLearn> getAvailableMaxLvlSkills(Player player, ClassId classId, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills = getSkills(player, classId, type, subUnit);
		switch(type)
		{
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_CARDINAL:
				if(player == null)
					return skills;
				else
				{
					Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
					for(SkillLearn temp : skills)
					{
						int knownLevel = player.getSkillLevel(temp.getId());
						if(knownLevel == -1)
							skillLearnMap.put(temp.getId(), temp);
					}
					return skillLearnMap.values();
				}
			case CLAN:
				Collection<SkillEntry> clanSkills = player.getClan().getSkills();
				return getAvaliableMaxLvlSkillList(skills, clanSkills.toArray(new SkillEntry[clanSkills.size()]));
			case SUB_UNIT:
				Collection<SkillEntry> subUnitSkills = subUnit.getSkills();
				return getAvaliableMaxLvlSkillList(skills, subUnitSkills.toArray(new SkillEntry[subUnitSkills.size()]));
			case ALCHEMY:
				if(player != null)
					return getAvaliableMaxLvlSkillList(skills, player.getAllAlchemySkillsArray());
		}

		if(player == null)
			return skills;

		return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray());
	}

	private Collection<SkillLearn> getAvaliableMaxLvlSkillList(Collection<SkillLearn> skillLearns, SkillEntry[] skills)
	{
		Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
		for(SkillLearn temp : skillLearns)
		{
			int skillId = temp.getId();
			if(!skillLearnMap.containsKey(skillId) || temp.getLevel() > skillLearnMap.get(skillId).getLevel())
				skillLearnMap.put(skillId, temp);
		}

		for(SkillEntry skillEntry : skills)
		{
			int skillId = skillEntry.getId();
			if(!skillLearnMap.containsKey(skillId))
				continue;

			SkillLearn temp = skillLearnMap.get(skillId);
			if(temp == null)
				continue;

			if(temp.getLevel() <= skillEntry.getTemplate().getLevelWithoutEnchant())
				skillLearnMap.remove(skillId);
		}

		return skillLearnMap.values();
	}

	public Collection<Skill> getLearnedSkills(Player player, AcquireType type)
	{
		switch(type)
		{
			case ABILITY:
				return getLearnedList(_abilitySkillTree, player.getAllSkillsArray());
			default:
				return Collections.emptyList();
		}
	}

	private Collection<Skill> getLearnedList(Collection<SkillLearn> skillLearns, SkillEntry[] skills)
	{
		TIntSet skillLvls = new TIntHashSet();
		for(SkillLearn temp : skillLearns)
			skillLvls.add(SkillUtils.generateSkillHashCode(temp.getId(), temp.getLevel()));

		Set<Skill> learned = new HashSet<Skill>();
		for(SkillEntry skillEntry : skills)
		{
			if(skillEntry == null)
				continue;

			if(skillLvls.contains(SkillUtils.generateSkillHashCode(skillEntry.getId(), skillEntry.getTemplate().getLevelWithoutEnchant())))
				learned.add(skillEntry.getTemplate());
		}

		return learned;
	}

	public Collection<SkillLearn> getAcquirableSkillListByClass(Player player)
	{
		Map<Integer, SkillLearn> skillListMap = new TreeMap<Integer, SkillLearn>();

		Collection<SkillLearn> skills = getNormalSkillTree(player);
		Collection<SkillLearn> currentLvlSkills = getAvaliableList(skills, player.getAllSkillsArray());
		currentLvlSkills = checkLearnsConditions(player, currentLvlSkills, player.getLevel(), player.getDualClassLevel());
		for(SkillLearn temp : currentLvlSkills)
		{
			if(!temp.isFreeAutoGet(AcquireType.NORMAL))
				skillListMap.put(temp.getId(), temp);
		}

		Collection<SkillLearn> nextLvlsSkills = getAvaliableList(skills, player.getAllSkillsArray());
		nextLvlsSkills = checkLearnsConditions(player, nextLvlsSkills, player.getMaxLevel(), player.getMaxLevel());
		for(SkillLearn temp : nextLvlsSkills)
		{
			if(!temp.isFreeAutoGet(AcquireType.NORMAL) && !skillListMap.containsKey(temp.getId()))
				skillListMap.put(temp.getId(), temp);
		}

		return skillListMap.values();
	}

	private Collection<SkillLearn> getNormalSkillTree(Player player)
	{
		Collection<SkillLearn> skills = new HashSet<SkillLearn>();
		skills.addAll(_normalSkillTree.get(player.getActiveClassId()));
		if((player.isBaseClassActive() || player.isDualClassActive()) && player.getDualClassLevel() > 0) 
		{
			if(_dualClassSkillTree.containsKey(player.getActiveClassId()))
				skills.addAll(_dualClassSkillTree.get(player.getActiveClassId()));
		}
		skills.addAll(getAwakeParentSkillTree(player));
		return skills;
	}

	public Collection<SkillLearn> getAwakeParentSkillTree(Player player)
	{
		return getAwakeParentSkillTree(player.getClassId(), ClassId.VALUES[player.getActiveDefaultClassId()]);
	}

	public Collection<SkillLearn> getAwakeParentSkillTree(ClassId classId, ClassId parentClassId)
	{
		if(classId == null || parentClassId == null)
			return Collections.emptyList();

		TIntObjectMap<Set<SkillLearn>> awakeParentSkillTree = _awakeParentSkillTree.get(classId.getBaseId());
		if(awakeParentSkillTree == null || awakeParentSkillTree.isEmpty())
			return Collections.emptyList();

		ClassId awakeParentId = classId.getAwakeParent(parentClassId);
		if(awakeParentId == null || !awakeParentSkillTree.containsKey(awakeParentId.getId()))
			return Collections.emptyList();

		return awakeParentSkillTree.get(awakeParentId.getId());
	}

	public SkillLearn getSkillLearn(Player player, int id, int level, AcquireType type)
	{
		return getSkillLearn(player, null, id, level, type);
	}

	public SkillLearn getSkillLearn(Player player, ClassId classId, int id, int level, AcquireType type)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = getNormalSkillTree(player);
				break;
			case COLLECTION:
				skills = _collectionSkillTree;
				break;
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				break;
			case TRANSFER_CARDINAL:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_EVA_SAINTS:
				skills = _transferSkillTree.get(player.getActiveClassId());
				break;
			case FISHING:
				skills = _fishingSkillTree;
				break;
			case CLAN:
				skills = _pledgeSkillTree;
				break;
			case SUB_UNIT:
				skills = _subUnitSkillTree;
				break;
			case CERTIFICATION:
				skills = _certificationSkillTree;
				break;
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				break;
			case GENERAL:
				skills = _generalSkillTree.get(player.getActiveClassId());
				break;
			case NOBLESSE:
				skills = _noblesseSkillTree;
				break;
			case HERO:
				skills = _heroSkillTree;
				break;
			case GM:
				skills = _gmSkillTree;
				break;
			case CUSTOM:
				skills = _customSkillTree;
				break;
			case CHAOS:
				skills = _chaosSkillTree;
				break;
			case DUAL_CHAOS:
				skills = _dualChaosSkillTree;
				break;
			case ABILITY:
				skills = _abilitySkillTree;
				break;
			case ALCHEMY:
				skills = _alchemySkillTree;
				break;
			case HONORABLE_NOBLESSE:
				skills = _honorNobleSkillTree;
				break;
			case MULTICLASS:
				if(Config.MULTICLASS_SYSTEM_ENABLED)
				{
					if(classId != null)
					{
						TIntObjectMap<Set<SkillLearn>> map = _multiclassLearnSkillTree.get(player.getActiveClassId());
						if(map == null)
							return null;

						skills = map.get(classId.getId());
					}
					else
						skills = _multiclassCheckSkillTree.get(player.getActiveClassId());
				}
				else
					return null;
				break;
			default:
				return null;
		}

		if(skills == null)
			return null;

		for(SkillLearn temp : skills)
		{
			if(temp.isOfRace(player.getRace()) && temp.getLevel() == level && temp.getId() == id)
				return temp;
		}

		return null;
	}

	public boolean isSkillPossible(Player player, Skill skill, AcquireType type)
	{
		return isSkillPossible(player, null, skill, type);
	}

	public boolean isSkillPossible(Player player, ClassId classId, Skill skill, AcquireType type)
	{
		switch(type)
		{
			case TRANSFER_CARDINAL:
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
				if(player.getActiveClassId() != type.transferClassId())
					return false;
				break;
			case CLAN:
			case SUB_UNIT:
				if(player.getClan() == null)
					return false;
				break;
			case NOBLESSE:
				if(!player.isNoble() || skill.getId() == Skill.SKILL_WYVERN_AEGIS && (!player.isClanLeader() || player.getClan().getCastle() == 0))
					return false;
				break;
			case HERO:
				if(!player.isHero() || !player.isBaseClassActive())
					return false;
				break;
			case GM:
				if(!player.isGM())
					return false;
				break;
			case CHAOS:
				if(!player.isBaseClassActive())
					return false;
				break;
			case DUAL_CHAOS:
				if(!player.isDualClassActive())
					return false;
				break;
			case ABILITY:
				if(!player.isAllowAbilities())
					return false;
				break;
			case ALCHEMY:
				if(player.getRace() != Race.ERTHEIA)
					return false;
				break;
			case HONORABLE_NOBLESSE:
				if(!player.isHonorableNoble() || !player.isBaseClassActive() && !player.isDualClassActive())
					return false;
				break;
			case MULTICLASS:
				if(!Config.MULTICLASS_SYSTEM_ENABLED)
					return false;
				break;
		}

		SkillLearn learn = getSkillLearn(player, classId, skill.getId(), skill.getLevelWithoutEnchant(), type);
		if(learn == null)
			return false;

		return learn.testCondition(player);
	}

	public boolean isSkillPossible(Player player, Skill skill)
	{
		for(AcquireType aq : AcquireType.VALUES)
		{
			if(aq == AcquireType.ALCHEMY)
				continue;

			if(isSkillPossible(player, skill, aq))
				return true;
		}
		return false;
	}

	public boolean containsInTree(Skill skill, AcquireType type)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = new HashSet<SkillLearn>();
				for(Set<SkillLearn> temp : _normalSkillTree.valueCollection())
					skills.addAll(temp);
				
                for(Set<SkillLearn> temp : _dualClassSkillTree.valueCollection())
                    skills.addAll(temp);
                    
				for(TIntObjectMap<Set<SkillLearn>> map : _awakeParentSkillTree.valueCollection())
				{
					for(Set<SkillLearn> temp : map.valueCollection())
						skills.addAll(temp);
				}
				break;
			case COLLECTION:
				skills = _collectionSkillTree;
				break;
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				break;
			case FISHING:
				skills = _fishingSkillTree;
				break;
			case TRANSFER_CARDINAL:
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
				skills = _transferSkillTree.get(type.transferClassId());
				break;
			case CLAN:
				skills = _pledgeSkillTree;
				break;
			case SUB_UNIT:
				skills = _subUnitSkillTree;
				break;
			case CERTIFICATION:
				skills = _certificationSkillTree;
				break;
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				break;
			case GENERAL:
				skills = new HashSet<SkillLearn>();
				for(Set<SkillLearn> temp : _generalSkillTree.valueCollection())
					skills.addAll(temp);
				break;
			case NOBLESSE:
				skills = _noblesseSkillTree;
				break;
			case HERO:
				skills = _heroSkillTree;
				break;
			case GM:
				skills = _gmSkillTree;
				break;
			case CUSTOM:
				skills = _customSkillTree;
				break;
			case CHAOS:
				skills = _chaosSkillTree;
				break;
			case DUAL_CHAOS:
				skills = _dualChaosSkillTree;
				break;
			case ABILITY:
				skills = _abilitySkillTree;
				break;
			case ALCHEMY:
				skills = _alchemySkillTree;
				break;
			case HONORABLE_NOBLESSE:
				skills = _honorNobleSkillTree;
				break;
			case MULTICLASS:
				if(Config.MULTICLASS_SYSTEM_ENABLED)
				{
					skills = new HashSet<SkillLearn>();
					for(Set<SkillLearn> temp : _multiclassCheckSkillTree.valueCollection())
						skills.addAll(temp);
				}
				else
					return false;
				break;
			default:
				return false;
		}

		for(SkillLearn learn : skills)
		{
			if(learn.getId() == skill.getId() && learn.getLevel() == skill.getLevel())
				return true;
		}
		return false;
	}

    public Collection<SkillLearn> getSkillTree(AcquireType type)
    {
        switch (type)
        {
            case CHAOS:
                return this._chaosSkillTree;
            case DUAL_CHAOS:
                return this._dualChaosSkillTree;
            default:
                return Collections.emptyList();
        }
    }
    
	public boolean checkLearnCondition(Player player, SkillLearn skillLearn, int level, int dualClassLevel)
	{
		if(skillLearn == null)
			return false;

		if(player == null)
			return true;

		if(skillLearn.getMinLevel() > level)
			return false;
		
		if(skillLearn.getDualClassMinLvl() > dualClassLevel)
            return false;
            
		if(!skillLearn.isOfRace(player.getRace()))
			return false;

		return skillLearn.testCondition(player);
	}

	private Collection<SkillLearn> checkLearnsConditions(Player player, Collection<SkillLearn> skillLearns, int level, int dualClassLevel)
	{
		if(skillLearns == null)
			return null;

		if(player == null)
			return skillLearns;

		Set<SkillLearn> skills = new HashSet<SkillLearn>();
		for(SkillLearn skillLearn : skillLearns)
		{
			if(checkLearnCondition(player, skillLearn, level, dualClassLevel))
				skills.add(skillLearn);
		}
		return skills;
	}

	public void addAllNormalSkillLearns(int classId, Set<SkillLearn> s)
	{
		Set<SkillLearn> set = _normalSkillTree.get(classId);
		if (set == null)
		{
			set = new HashSet<SkillLearn>();
			_normalSkillTree.put(classId, set);
		}
		set.addAll(s);
	}

	public void initNormalSkillLearns()
	{
		TIntObjectMap<Set<SkillLearn>> map = new TIntObjectHashMap<Set<SkillLearn>>(_normalSkillTree);

		_normalSkillTree.clear();

		for(ClassId classId : ClassId.VALUES)
		{
			if(classId.isDummy())
				continue;

			Set<SkillLearn> skills = map.get(classId.getId());
			if(skills == null)
			{
				info("Not found NORMAL skill learn for class " + classId.getId());
				continue;
			}

			_normalSkillTree.put(classId.getId(), skills);

			if(classId.isOfLevel(ClassLevel.AWAKED))
			{
				TIntObjectMap<SkillLearn> tempMap = new TIntObjectHashMap<SkillLearn>();
	            skills.forEach(sk ->
	            {
	            	SkillLearn tempSk = tempMap.get(sk.getId());
	            	if(tempSk == null || tempSk.getLevel() > sk.getLevel())
	                    tempMap.put(sk.getId(), sk); 
	            });
	            for(SkillLearn sk : tempMap.valueCollection())
	            {
	            	if(sk.getLevel() > 1)
	            		skills.add(new SkillLearn(sk.getId(), sk.getLevel() - 1, 1, 0, 0, 0, true, null, 0, sk.getClassLevel())); 
	            } 
			}
			else
			{
				ClassId secondparent = classId.getParent(1);
				if(secondparent == classId.getParent(0))
					secondparent = null;

				ClassId tempClassId = classId.getParent(0);
				while(tempClassId != null)
				{
					if(_normalSkillTree.containsKey(tempClassId.getId()))
						skills.addAll(_normalSkillTree.get(tempClassId.getId()));

					tempClassId = tempClassId.getParent(0);
					if(tempClassId == null && secondparent != null)
					{
						tempClassId = secondparent;
						secondparent = secondparent.getParent(1);
					}
				}
			}
		}

		if(Config.MULTICLASS_SYSTEM_ENABLED)
		{
			for(ClassId classId : ClassId.VALUES)
			{
				if(classId.isDummy())
					continue;

				TIntObjectMap<Set<SkillLearn>> multiMap = new TIntObjectHashMap<Set<SkillLearn>>();
				Set<SkillLearn> multiSet = new HashSet<SkillLearn>();
				for(ClassId sameLevelClassId : ClassId.VALUES)
				{
					if(MulticlassUtils.checkMulticlass(classId, sameLevelClassId))
					{
						Set<SkillLearn> skills = new HashSet<SkillLearn>();
						loop: for (SkillLearn sl : _normalSkillTree.get(sameLevelClassId.getId()))
						{
							for(SkillLearn temp : _normalSkillTree.get(classId.getId()))
							{
								if(sl.getId() == temp.getId() && sl.getLevel() == temp.getLevel())
									continue loop;
							}

							double spModifier;
							int costItemIdBasedOnSp;
							double costItemCountModifierBasedOnSp;
							int costItemId;
							long costItemCount;
	                        ClassLevel skillClassLevel = sl.getClassLevel();
	                        
	                        if(sameLevelClassId.isOfRace(Race.ERTHEIA))
	                        {
	                            if(sameLevelClassId.isOfLevel(ClassLevel.THIRD))
	                                skillClassLevel = ClassLevel.AWAKED;
	                            else if(sameLevelClassId.isOfLevel(ClassLevel.SECOND))
	                                skillClassLevel = ClassLevel.THIRD;
	                            else if(sameLevelClassId.isOfLevel(ClassLevel.FIRST))
	                                skillClassLevel = ClassLevel.SECOND;
	                            else if(sameLevelClassId.isOfLevel(ClassLevel.NONE) && sl.getMinLevel() > 20)
	                                skillClassLevel = ClassLevel.FIRST;
	                        }

							if(skillClassLevel == ClassLevel.FIRST)
							{
								spModifier = Config.MULTICLASS_SYSTEM_1ST_CLASS_SP_MODIFIER;
								costItemIdBasedOnSp = Config.MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID_BASED_ON_SP;
								costItemCountModifierBasedOnSp = Config.MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
								costItemId = Config.MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID;
								costItemCount = Config.MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT;
							}
							else if(skillClassLevel == ClassLevel.SECOND)
							{
								spModifier = Config.MULTICLASS_SYSTEM_2ND_CLASS_SP_MODIFIER;
								costItemIdBasedOnSp = Config.MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID_BASED_ON_SP;
								costItemCountModifierBasedOnSp = Config.MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
								costItemId = Config.MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID;
								costItemCount = Config.MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT;
							}
							else if(skillClassLevel == ClassLevel.THIRD)
							{
								spModifier = Config.MULTICLASS_SYSTEM_3RD_CLASS_SP_MODIFIER;
								costItemIdBasedOnSp = Config.MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID_BASED_ON_SP;
								costItemCountModifierBasedOnSp = Config.MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
								costItemId = Config.MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID;
								costItemCount = Config.MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT;
							}
							else if(skillClassLevel == ClassLevel.AWAKED)
							{
								spModifier = Config.MULTICLASS_SYSTEM_4TH_CLASS_SP_MODIFIER;
								costItemIdBasedOnSp = Config.MULTICLASS_SYSTEM_4TH_CLASS_COST_ITEM_ID_BASED_ON_SP;
								costItemCountModifierBasedOnSp = Config.MULTICLASS_SYSTEM_4TH_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
								costItemId = Config.MULTICLASS_SYSTEM_4TH_CLASS_COST_ITEM_ID;
								costItemCount = Config.MULTICLASS_SYSTEM_4TH_CLASS_COST_ITEM_COUNT;
							}
							else
							{
								spModifier = Config.MULTICLASS_SYSTEM_NON_CLASS_SP_MODIFIER;
								costItemIdBasedOnSp = Config.MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID_BASED_ON_SP;
								costItemCountModifierBasedOnSp = Config.MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
								costItemId = Config.MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID;
								costItemCount = Config.MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT;
							}

							SkillLearn skillLearn = new SkillLearn(sl.getId(), sl.getLevel(), sl.getMinLevel(), (int) (Math.max(1, sl.getCost()) * spModifier), sl.getItemId(), sl.getItemCount(), false, sl.getRace(), sl.getDualClassMinLvl(), sl.getClassLevel());
							if(costItemIdBasedOnSp > 0 && costItemCountModifierBasedOnSp > 0)
								skillLearn.addAdditionalRequiredItem(costItemIdBasedOnSp, Math.max(1, (long) (skillLearn.getCost() * costItemCountModifierBasedOnSp)));

							if(costItemId > 0 && costItemCount > 0)
								skillLearn.addAdditionalRequiredItem(costItemId, costItemCount);

							skills.add(skillLearn);
						}

						multiMap.put(sameLevelClassId.getId(), skills);
						multiSet.addAll(skills);
					}
				}
				_multiclassCheckSkillTree.put(classId.getId(), multiSet);
				_multiclassLearnSkillTree.put(classId.getId(), multiMap);
			}
		}
	}

	public void addAllGeneralSkillLearns(int classId, Set<SkillLearn> s)
	{
		Set<SkillLearn> set = _generalSkillTree.get(classId);
		if(set == null)
		{
			set = new HashSet<SkillLearn>();
			_generalSkillTree.put(classId, set);
		}
		set.addAll(s);
	}

	public void initGeneralSkillLearns()
	{
		TIntObjectMap<Set<SkillLearn>> map = new TIntObjectHashMap<Set<SkillLearn>>(_generalSkillTree);
		Set<SkillLearn> globalList = map.remove(-1); 

		_generalSkillTree.clear();

		for(ClassId classId : ClassId.VALUES)
		{
			if(classId.isDummy())
				continue;

			Set<SkillLearn> tempList = map.get(classId.getId());
			if(tempList == null)
				tempList = new HashSet<SkillLearn>();

			Set<SkillLearn> skills = new HashSet<SkillLearn>();
			_generalSkillTree.put(classId.getId(), skills);

			if(!classId.isOfLevel(ClassLevel.AWAKED))
			{
				ClassId secondparent = classId.getParent(1);
				if(secondparent == classId.getParent(0))
					secondparent = null;

				ClassId tempClassId = classId.getParent(0);
				while(tempClassId != null)
				{
					if(_generalSkillTree.containsKey(tempClassId.getId()))
						tempList.addAll(_generalSkillTree.get(tempClassId.getId()));

					tempClassId = tempClassId.getParent(0);
					if(tempClassId == null && secondparent != null)
					{
						tempClassId = secondparent;
						secondparent = secondparent.getParent(1);
					}
				}
			}

			tempList.addAll(globalList);

			skills.addAll(tempList);
		}
	}

	public void addAllDualClassSkillLearns(int classId, Set<SkillLearn> s)
	{
		Set<SkillLearn> set = _dualClassSkillTree.get(classId);
        if (set == null) {
            set = new HashSet<SkillLearn>();
            _dualClassSkillTree.put(classId, set);
        }
        set.addAll(s);
	}

	public void addAllAwakeParentSkillLearns(int awakeClassId, int parentClassId, Set<SkillLearn> s)
	{
		TIntObjectMap<Set<SkillLearn>> map = _awakeParentSkillTree.get(awakeClassId);
        if(map == null)
        {
            map = new TIntObjectHashMap<Set<SkillLearn>>();
            _awakeParentSkillTree.put(awakeClassId, map);
        }
        Set<SkillLearn> set = map.get(parentClassId);
        if(set == null)
        {
            set = new HashSet<SkillLearn>();
            map.put(parentClassId, set);
        }
        set.addAll(s);
	}

	public void addAllTransferLearns(int classId, Set<SkillLearn> s)
	{
		Set<SkillLearn> set = _transferSkillTree.get(classId);
        if(set == null)
        {
            set = new HashSet<SkillLearn>();
            _transferSkillTree.put(classId, set);
        }
        set.addAll(s);
	}

	public void addAllTransformationLearns(Set<SkillLearn> s)
	{
		_transformationSkillTree.addAll(s);
	}

	public void addAllFishingLearns(Set<SkillLearn> s)
	{
		_fishingSkillTree.addAll(s);
	}

	public void addAllCertificationLearns(Set<SkillLearn> s)
	{
		_certificationSkillTree.addAll(s);
	}

	public void addAllDualCertificationLearns(Set<SkillLearn> s)
	{
		_dualCertificationSkillTree.addAll(s);
	}

	public void addAllCollectionLearns(Set<SkillLearn> s)
	{
		_collectionSkillTree.addAll(s);
	}

	public void addAllSubUnitLearns(Set<SkillLearn> s)
	{
		_subUnitSkillTree.addAll(s);
	}

	public void addAllPledgeLearns(Set<SkillLearn> s)
	{
		_pledgeSkillTree.addAll(s);
	}

	public void addAllNoblesseLearns(Set<SkillLearn> s)
	{
		_noblesseSkillTree.addAll(s);
	}

	public void addAllHeroLearns(Set<SkillLearn> s)
	{
		_heroSkillTree.addAll(s);
	}

	public void addAllGMLearns(Set<SkillLearn> s)
	{
		_gmSkillTree.addAll(s);
	}

	public void addAllCustomLearns(Set<SkillLearn> s)
	{
		_customSkillTree.addAll(s);
	}

	public void addAllChaosSkillLearns(Set<SkillLearn> s)
	{
		_chaosSkillTree.addAll(s);
	}

	public void addAllDualChaosSkillLearns(Set<SkillLearn> s)
	{
		_dualChaosSkillTree.addAll(s);
	}

	public void addAllAbilitySkillLearns(Set<SkillLearn> s)
	{
		_abilitySkillTree.addAll(s);
	}

	public void addAllAlchemySkillLearns(Set<SkillLearn> s)
	{
		_alchemySkillTree.addAll(s);
	}

	public void addAllHonorNobleSkillLearns(Set<SkillLearn> s)
	{
		_honorNobleSkillTree.addAll(s);
	}

    public void setAbilitiesMinLevel(int value)
    {
        _abilitiesMinLevel = value;
    }
    
    public int getAbilitiesMinLevel() 
    {
        return _abilitiesMinLevel;
    }
    
	public void setAbilitiesRefreshPrice(long value)
	{
		_abilitiesRefreshPrice = value;
	}

	public long getAbilitiesRefreshPrice()
	{
		return _abilitiesRefreshPrice;
	}

	public void setMaxAbilitiesPoints(int value)
	{
		_maxAbilitiesPoints = value;
	}

	public int getMaxAbilitiesPoints()
	{
		return _maxAbilitiesPoints;
	}

	@Override
	public void log()
	{
        info("load " + sizeTroveMap(_normalSkillTree) + " normal learns for " + _normalSkillTree.size() + " classes.");
        info("load " + sizeTroveMapMap(_awakeParentSkillTree) + " awake parent learns for " + _awakeParentSkillTree.size() + " classes.");
        info("load " + sizeTroveMap(_transferSkillTree) + " transfer learns for " + _transferSkillTree.size() + " classes.");
        info("load " + sizeTroveMap(_generalSkillTree) + " general skills learns for " + _generalSkillTree.size() + " classes.");
        info("load " + sizeTroveMap(_dualClassSkillTree) + " dual class skills learns for " + _dualClassSkillTree.size() + " classes.");
        
        info("load " + _transformationSkillTree.size() + " transformation learns.");
        info("load " + _fishingSkillTree.size() + " fishing learns.");
        info("load " + _certificationSkillTree.size() + " certification learns.");
        info("load " + _dualCertificationSkillTree.size() + " dual certification learns.");
        info("load " + _collectionSkillTree.size() + " collection learns.");
        info("load " + _pledgeSkillTree.size() + " pledge learns.");
        info("load " + _subUnitSkillTree.size() + " sub unit learns.");
        info("load " + _noblesseSkillTree.size() + " noblesse skills learns.");
        info("load " + _heroSkillTree.size() + " hero skills learns.");
        info("load " + _gmSkillTree.size() + " GM skills learns.");
        info("load " + _customSkillTree.size() + " custom skills learns.");
        info("load " + _chaosSkillTree.size() + " chaos skill learns.");
        info("load " + _dualChaosSkillTree.size() + " dual-chaos skill learns.");
        info("load " + _abilitySkillTree.size() + " abilities skill learns.");
        info("load " + _alchemySkillTree.size() + " alchemy skill learns.");
        info("load " + _honorNobleSkillTree.size() + " honorable noblesse skill learns.");
	}

	
	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
        _normalSkillTree.clear();
        _fishingSkillTree.clear();
        _transferSkillTree.clear();
        _dualClassSkillTree.clear();
        _certificationSkillTree.clear();
        _dualCertificationSkillTree.clear();
        _collectionSkillTree.clear();
        _pledgeSkillTree.clear();
        _subUnitSkillTree.clear();
        _generalSkillTree.clear();
        _awakeParentSkillTree.clear();
        _noblesseSkillTree.clear();
        _heroSkillTree.clear();
        _gmSkillTree.clear();
        _customSkillTree.clear();
        _chaosSkillTree.clear();
        _dualChaosSkillTree.clear();
        _abilitySkillTree.clear();
        _alchemySkillTree.clear();
        _honorNobleSkillTree.clear();
        _abilitiesMinLevel = 0;
        _abilitiesRefreshPrice = 0;
        _maxAbilitiesPoints = 0;
	}

	private int sizeTroveMapMap(TIntObjectMap<TIntObjectMap<Set<SkillLearn>>> a)
	{
		int i = 0;
		for(TIntObjectIterator<TIntObjectMap<Set<SkillLearn>>> iterator = a.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			i += sizeTroveMap(iterator.value());
		}

		return i;
	}

	private int sizeTroveMap(TIntObjectMap<Set<SkillLearn>> a)
	{
		int i = 0;
		for(TIntObjectIterator<Set<SkillLearn>> iterator = a.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			i += iterator.value().size();
		}

		return i;
	}
}