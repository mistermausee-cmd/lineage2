package l2s.gameserver.stats;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.data.xml.holder.HitCondBonusHolder;
import l2s.gameserver.data.xml.holder.KarmaIncreaseDataHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.HitCondBonusType;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMagicAttackInfo;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.BasicProperty;
import l2s.gameserver.skills.BasicPropertyResist;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.skills.SkillTrait;
import l2s.gameserver.skills.SkillTraitType;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.utils.PositionUtils;

public class Formulas
{
	private static final double CRAFTING_MASTERY_CHANCE = 1.5; 

	public static class AttackInfo
	{
		public double damage = 0;
		public double defence = 0;
		public boolean crit = false;
		public boolean shld = false;
		public boolean miss = false;
		public boolean blow = false;
	}

	
	public static AttackInfo calcPhysDam(Creature attacker, Creature target, Skill skill, boolean dual, boolean blow, boolean useShot, boolean onCrit)
	{
		return calcPhysDam(attacker, target, skill, 1., skill == null ? 0. : skill.getPower(target), dual, blow, useShot, onCrit, true);
	}

	public static AttackInfo calcPhysDam(Creature attacker, Creature target, Skill skill, double pAtkMod, double power, boolean dual, boolean blow, boolean useShot, boolean onCrit, boolean canCrit)
	{
		AttackInfo info = new AttackInfo();

		info.damage = attacker.getPAtk(target) * pAtkMod;
		info.defence = target.getPDef(attacker);
		info.blow = blow;
		info.crit = (onCrit || canCrit) && calcPCrit(attacker, target, skill, info.blow);
		info.shld = (skill == null || !skill.getShieldIgnore()) && calcShldUse(attacker, target);
		info.miss = false;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
		boolean isPvE = attacker.isPlayable() && target.isNpc();

		if(info.shld)
		{
			double shldDef = target.getShldDef();
			if(skill != null && skill.getShieldIgnorePercent() > 0)
				shldDef -= shldDef * skill.getShieldIgnorePercent() / 100.;
			info.defence += shldDef;
		}

		if(skill != null && skill.getDefenceIgnorePercent() > 0)
			info.defence *= (1. - (skill.getDefenceIgnorePercent() / 100.));

		if(skill != null)
		{
			
			if(power == 0)
				return new AttackInfo();	

			if(info.damage > 0 && skill.canBeEvaded() && Rnd.chance(target.calcStat(Stats.P_SKILL_EVASION, 100, attacker, skill) - 100))
			{
				
				info.miss = true;
				info.damage = 0;
				return info;
			}

			info.damage *= attacker.getLevelBonus();

			if(info.blow && !skill.isBehind() && useShot) 
				info.damage *= ((100 + attacker.getChargedSoulshotPower()) / 100.);

			double skillPowerMod = 1;

			if(skill.getId() == 10510)
			{
				if(target.getAbnormalList().contains(AbnormalType.bleeding))
			          skillPowerMod *= 1.2D;
			}
			else if(skill.getId() == 30500)
			{
		        if(attacker.isStunned())
		        	skillPowerMod *= 1.2D;
		    }
			
			if(skill.getNumCharges() > 0)
				skillPowerMod *= attacker.calcStat(Stats.CHARGED_P_SKILL_POWER, 1.);

			info.damage += attacker.calcStat(Stats.P_SKILL_POWER, (attacker.isServitor() ? Config.SERVITOR_P_SKILL_POWER_MODIFIER : 1.) * power) * skillPowerMod;

			info.damage += attacker.calcStat(Stats.P_SKILL_POWER_STATIC);

			if(info.blow && skill.isBehind() && useShot) 
				info.damage *= ((100 + (attacker.getChargedSoulshotPower() / 2.)) / 100.);

			
			if(!skill.isChargeBoost())
				info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100.;

			if(info.blow)
			{
				double critDmg = info.damage;
				critDmg *= 0.01 * attacker.calcStat(Stats.P_CRITICAL_DAMAGE_PER, target, skill) * 0.66;
				critDmg += 6 * attacker.calcStat(Stats.P_CRITICAL_DAMAGE_DIFF, target, skill);
				critDmg -= info.damage;
				critDmg -= (critDmg - target.calcStat(Stats.P_CRIT_DAMAGE_RECEPTIVE, critDmg)) / 2.;
				critDmg = Math.max(0, critDmg);
				info.damage += critDmg;
			}

			if(skill.isChargeBoost())
			{
				int force = attacker.getIncreasedForce();
				if(force > 5 && skill.getId() == 10269)
			        force = 5;
			    else if(force > 3)
					force = 3;

				
				info.damage *= 1 + 0.1 * force;
			}
			else if(skill.isSoulBoost())
				info.damage *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);

			if(info.crit)
			{
				double critDmg = info.damage;
				critDmg *= 2 + (attacker.calcStat(Stats.P_SKILL_CRITICAL_DAMAGE_PER, target, skill) * 0.01 - 1);
				critDmg += attacker.calcStat(Stats.P_SKILL_CRITICAL_DAMAGE_DIFF, target, null);
				critDmg -= info.damage;
				info.damage += critDmg;
			}
		}
		else
		{
			info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100.;

			if(dual)
				info.damage /= 2.;

			if(info.crit)
			{
				double critDmg = info.damage;
				critDmg *= 2 + (attacker.calcStat(Stats.P_CRITICAL_DAMAGE_PER, target, null) * 0.01 - 1);
				critDmg += attacker.calcStat(Stats.P_CRITICAL_DAMAGE_DIFF, target, null);
				critDmg -= info.damage;
				critDmg = target.calcStat(Stats.P_CRIT_DAMAGE_RECEPTIVE, critDmg);
				critDmg = Math.max(0, critDmg);
				info.damage += critDmg;
			}
		}

		if(info.crit)
		{
			
			int chance = attacker.getSkillLevel(Skill.SKILL_SOUL_MASTERY);
			if(chance > 0)
			{
				if(chance >= 21)
					chance = 30;
				else if(chance >= 15)
					chance = 25;
				else if(chance >= 9)
					chance = 20;
				else if(chance >= 4)
					chance = 15;
				if(Rnd.chance(chance))
					attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
			}
		}

		if(attacker.isDistortedSpace())
			info.damage *= 1.2;
		else
		{
			switch(PositionUtils.getDirectionTo(target, attacker))
			{
				case BEHIND:
					info.damage *= 1.2;
					break;
				case SIDE:
					info.damage *= 1.1;
					break;
			}
		}

		if(useShot && !info.blow)
			info.damage *= ((100 + attacker.getChargedSoulshotPower()) / 100.);

		info.damage *= 77. / info.defence;

		info.damage *= calcWeaponTraitBonus(attacker, target);
		info.damage *= skill != null ? calcGeneralTraitBonus(attacker, target, skill.getTraitType(), false) : 1.;
		info.damage *= calcAttributeBonus(attacker, target, skill);

		info.damage = attacker.calcStat(Stats.INFLICTS_P_DAMAGE_POWER, info.damage, target, skill);
		info.damage = target.calcStat(Stats.RECEIVE_P_DAMAGE_POWER, info.damage, attacker, skill);

		if(info.shld)
		{
			if(Rnd.chance(Config.EXCELLENT_SHIELD_BLOCK_CHANCE))
			{
				info.damage = Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE;
				return info;
			}
		}

		if(isPvP)
		{
			if(skill == null)
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1);
			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1);
			}
		}
		else if(isPvE)
		{
			if(skill == null)
			{
				info.damage *= attacker.calcStat(Stats.PVE_PHYS_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVE_PHYS_DEFENCE_BONUS, 1);
			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVE_PHYS_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVE_PHYS_SKILL_DEFENCE_BONUS, 1);
			}
		}

		if(info.crit)
			info.damage = info.damage * getPCritDamageMode(attacker, skill == null);
		if(info.blow)
			info.damage = info.damage * Config.ALT_BLOW_DAMAGE_MOD;
		if(!info.crit && !info.blow)
			info.damage = info.damage * getPDamModifier(attacker);

		
		if(skill != null)
		{
			if(info.damage > 0 && skill.isDeathlink())
				info.damage *= 1.8 * ((skill.isPhysic() ? 2.0 : 1.0) - attacker.getCurrentHpRatio());

			if(onCrit)
			{
				if(attacker.isCriticalBlowCastingSkill() && attacker.getCastingSkill() == skill)
					info.crit = true;
				else if(attacker.isCriticalBlowDualCastingSkill() && attacker.getDualCastingSkill() == skill)
			        info.crit = true;
				else
					return null;
			}

			if(info.damage > 0)
			{
				WeaponTemplate weaponItem = attacker.getActiveWeaponTemplate();
				if(skill.getIncreaseOnPole() > 0. && weaponItem != null && weaponItem.getItemType() == WeaponType.POLE)
					info.damage *= skill.getIncreaseOnPole();
				if(skill.getDecreaseOnNoPole() > 0. && weaponItem != null && weaponItem.getItemType() != WeaponType.POLE)
					info.damage *= skill.getDecreaseOnNoPole();

				if(calcStunBreak(info.crit, true, false))
				{
					target.getAbnormalList().stop(AbnormalType.stun);
					target.getAbnormalList().stop(AbnormalType.turn_flee);					
				}

				if(calcCastBreak(target, info.crit))
					target.abortCast(false, true);
			}
		}

		return info;
	}

	public static double calcLethalDamage(Creature attacker, Creature target, Skill skill)
	{
		if(skill == null)
			return 0.;

		if(target.isLethalImmune())
			return 0.;

		final double deathRcpt = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, attacker, skill);
		final double lethal1Chance = skill.getLethal1(attacker) * deathRcpt;
		final double lethal2Chance = skill.getLethal2(attacker) * deathRcpt;

		double damage = 0.;

		if(Rnd.chance(lethal2Chance))
		{
			if(target.isPlayer())
			{
				damage = target.getCurrentHp() + target.getCurrentCp() - 1.1; 
				target.sendPacket(SystemMsg.LETHAL_STRIKE);
			}
			else
				damage = target.getCurrentHp() - 1;
			attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
		}
		else if(Rnd.chance(lethal1Chance))
		{
			if(target.isPlayer())
			{
				damage = target.getCurrentCp();
				target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
			}
			else
				damage = target.getCurrentHp() / 2.;
			attacker.sendPacket(SystemMsg.CP_SIPHON);
		}
		return damage;
	}

	private static double getMSimpleDamageMode(Creature attacker) 
	{
		if(!attacker.isPlayer())
			return Config.ALT_M_SIMPLE_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL;
			case 140:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_EOL_HEALER;
				
			case 148:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_AEORE_EVAS_SAINT;
			case 181:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD;
		}
	}

	public static double getMCritDamageMode(Creature attacker) 
	{
		if(!attacker.isPlayer())
			return Config.ALT_M_CRIT_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL;
			case 140:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_M_CRIT_DAMAGE_MOD_EOL_HEALER;
				
			case 148:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_M_CRIT_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_M_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_M_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_M_CRIT_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_M_CRIT_DAMAGE_MOD;
		}
	}

	private static double getPDamModifier(Creature attacker) 
	{
		if(!attacker.isPlayer())
			return Config.ALT_P_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_P_DAMAGE_MOD_SIGEL;
			case 140:
				return Config.ALT_P_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_P_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_P_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_P_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_P_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_P_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_P_DAMAGE_MOD_EOL_HEALER;
				
			case 148:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_P_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_P_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_P_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_P_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_P_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_P_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_P_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_P_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_P_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_P_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_P_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_P_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_P_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_P_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_P_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_P_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_P_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_P_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_P_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_P_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_P_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_P_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_P_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_P_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_P_DAMAGE_MOD_AEORE_EVAS_SAINT;
			case 181:
				return Config.ALT_P_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_P_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_P_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_P_DAMAGE_MOD;
		}
	}

	private static double getPCritDamageMode(Creature attacker, boolean notSkill) 
	{
		if(!attacker.isPlayer())
			return Config.ALT_P_CRIT_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL;
			case 140:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_P_CRIT_DAMAGE_MOD_EOL_HEALER;
				
			case 148:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_P_CRIT_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_P_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_P_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_EVISCERATOR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SAHYAS_SEER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_P_CRIT_DAMAGE_MOD;
		}
	}

	private static double getPCritChanceMode(Creature attacker) 
	{
		if(!attacker.isPlayer())
			return Config.ALT_P_CRIT_CHANCE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL;
			case 140:
				return Config.ALT_P_CRIT_CHANCE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_P_CRIT_CHANCE_MOD_EOL_HEALER;
				
			case 148:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_P_CRIT_CHANCE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_P_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_P_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_P_CRIT_CHANCE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_P_CRIT_CHANCE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_P_CRIT_CHANCE_MOD;
		}
	}
	
	private static double getMCritChanceMode(Creature attacker) 
	{
		if(!attacker.isPlayer())
			return Config.ALT_M_CRIT_CHANCE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL;
			case 140:
				return Config.ALT_M_CRIT_CHANCE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_M_CRIT_CHANCE_MOD_EOL_HEALER;
				
			case 148:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_M_CRIT_CHANCE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_M_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_M_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_M_CRIT_CHANCE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_M_CRIT_CHANCE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_M_CRIT_CHANCE_MOD;
		}
	}	

	public static AttackInfo calcMagicDam(Creature attacker, Creature target, Skill skill, boolean useShot)
	{
		return calcMagicDam(attacker, target, skill, skill.getPower(target), useShot);
	}

	public static AttackInfo calcMagicDam(Creature attacker, Creature target, Skill skill, double power, boolean useShot)
	{
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
		boolean isPvE = attacker.isPlayable() && target.isNpc();
		
		boolean shield = !skill.getShieldIgnore() && calcShldUse(attacker, target);

		double mAtk = attacker.getMAtk(target, skill);

		if(useShot)
			mAtk *= ((100 + attacker.getChargedSpiritshotPower()) / 100.);

		double mdef = target.getMDef(null, skill);

		if(shield)
		{
			double shldDef = target.getShldDef();
			if(skill.getShieldIgnorePercent() > 0)
				shldDef -= shldDef * skill.getShieldIgnorePercent() / 100.;
			mdef += shldDef;
		}

		if(skill.getDefenceIgnorePercent() > 0)
			mdef *= (1. - (skill.getDefenceIgnorePercent() / 100.));

		mdef = Math.max(mdef, 1);

		AttackInfo info = new AttackInfo();
		if(power == 0)
			return info;

		if(skill.isSoulBoost())
			power *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);

		info.damage = (91 * power * Math.sqrt(mAtk)) / mdef;

		if(target.isTargetUnderDebuff())
			info.damage *= skill.getPercentDamageIfTargetDebuff();

        if(info.damage > 0.0 && !skill.hasEffects(EffectUseType.NORMAL) && calcMagicHitMiss(skill, attacker, target))
        {
            info.miss = true;
            info.damage = 0.0;
            return info;
        }
        
		info.damage *= 1 + (((Rnd.get() * attacker.getRandomDamage() * 2) - attacker.getRandomDamage()) / 100.);

		info.damage += Math.max(0., attacker.calcStat(Stats.M_SKILL_POWER, (attacker.isServitor() ? Config.SERVITOR_M_SKILL_POWER_MODIFIER : 1.0) * power));

		info.crit = calcMCrit(attacker, target, skill);

		if(info.crit)
		{
			
			if (Config.ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC)
			{
				double critDmg = info.damage;
				critDmg *= 2 + (attacker.calcStat(Stats.P_MAGIC_CRITICAL_DMG_PER, target, skill) * 0.01 - 1);
				critDmg += attacker.calcStat(Stats.P_MAGIC_CRITICAL_DMG_DIFF, target, skill);
				critDmg *= getMCritDamageMode(attacker);
				critDmg -= info.damage;
				double tempDamage = target.calcStat(Stats.M_CRIT_DAMAGE_RECEPTIVE, critDmg, attacker, skill);
				critDmg = Math.min(tempDamage, critDmg);
				critDmg = Math.max(0, critDmg);
				info.damage += critDmg;
			}
			else
			{
				info.damage *= 2 + (attacker.calcStat(Stats.P_MAGIC_CRITICAL_DMG_PER, target, skill) * 0.01 - 1);
				info.damage += attacker.calcStat(Stats.P_MAGIC_CRITICAL_DMG_DIFF, target, skill);
				info.damage *= getMCritDamageMode(attacker);
			}
		}
		else
			info.damage = info.damage * getMSimpleDamageMode(attacker);

		info.damage *= calcGeneralTraitBonus(attacker, target, skill.getTraitType(), false);
		info.damage *= calcAttributeBonus(attacker, target, skill);

		info.damage = attacker.calcStat(Stats.INFLICTS_M_DAMAGE_POWER, info.damage, target, skill);
		info.damage = target.calcStat(Stats.RECEIVE_M_DAMAGE_POWER, info.damage, attacker, skill);

		if(shield)
		{
			info.shld = true;
			if(Rnd.chance(Config.EXCELLENT_SHIELD_BLOCK_CHANCE))
			{
				info.damage = Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE;
				return info;
			}
		}

		int levelDiff = target.getLevel() - attacker.getLevel(); 

		if(info.damage > 0 && skill.isDeathlink())
			info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());

		if(info.damage > 0 && skill.isBasedOnTargetDebuff())
			info.damage *= 1 + 0.05 * target.getAbnormalList().size();

		if(skill.getSkillType() == SkillType.MANADAM)
			info.damage = Math.max(1, info.damage / 4.);
		else if(info.damage > 0)
		{
			if(isPvP)
			{
				info.damage *= attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1);
			}
			else if(isPvE)
			{
				info.damage *= attacker.calcStat(Stats.PVE_MAGIC_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVE_MAGIC_SKILL_DEFENCE_BONUS, 1);
			}
		}

		double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, attacker, skill) - attacker.calcStat(Stats.MAGIC_POWER, target, skill);
		double failChance = 4. * Math.max(1., levelDiff) * (1. + magic_rcpt / 100.);
		if(Rnd.chance(failChance))
		{
			if(levelDiff > 9)
			{
				info.damage = 0;
				SystemMessagePacket msg = new SystemMessagePacket(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
				attacker.sendPacket(new ExMagicAttackInfo(attacker.getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
				target.sendPacket(new ExMagicAttackInfo(attacker.getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
			}
			else
			{
				info.damage /= 2;
				SystemMessagePacket msg = new SystemMessagePacket(SystemMsg.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
		}

		if(calcCastBreak(target, info.crit))
			target.abortCast(false, true);

		if(calcStunBreak(info.crit, true, true) && info.damage > 0)
		{
			target.getAbnormalList().stop(AbnormalType.stun);
			target.getAbnormalList().stop(AbnormalType.turn_flee);			
		}

		return info;
	}

	
	public static boolean calcStunBreak(boolean crit, boolean isSkill, boolean isMagic)
	{
		if (!Config.ENABLE_STUN_BREAK_ON_ATTACK)
			return false;

		if (isSkill)
		{
			if (isMagic)
				return Rnd.chance(crit ? Config.CRIT_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL : Config.NORMAL_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL);
			return Rnd.chance(crit ? Config.CRIT_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL : Config.NORMAL_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL);
		}
		return Rnd.chance(crit ? Config.CRIT_STUN_BREAK_CHANCE_ON_REGULAR_HIT : Config.NORMAL_STUN_BREAK_CHANCE_ON_REGULAR_HIT);
	}

	
	public static boolean calcBlow(Creature activeChar, Creature target, Skill skill)
	{
		double vulnMod = target.calcStat(Stats.BLOW_RESIST, activeChar, skill);
		double profMod = activeChar.calcStat(Stats.BLOW_POWER, target, skill);
		if(vulnMod == Double.POSITIVE_INFINITY || profMod == Double.NEGATIVE_INFINITY)
			return false;

		if(vulnMod == Double.NEGATIVE_INFINITY || profMod == Double.POSITIVE_INFINITY)
			return true;

		WeaponTemplate weapon = activeChar.getActiveWeaponTemplate();

		double base_weapon_crit = weapon == null ? 4. : weapon.getCritical();
		double crit_height_bonus = 1;
		if (Config.ENABLE_CRIT_HEIGHT_BONUS)
			crit_height_bonus = 0.008 * Math.min(25, Math.max(-25, target.getZ() - activeChar.getZ())) + 1.1;
		double buffs_mult = activeChar.calcStat(Stats.FATALBLOW_RATE, target, skill);
		
		double skill_mod = skill.isBehind() ? Config.BLOW_SKILL_CHANCE_MOD_ON_BEHIND : Config.BLOW_SKILL_CHANCE_MOD_ON_FRONT;

		double chance = base_weapon_crit * buffs_mult * crit_height_bonus * skill_mod;

		double modDiff = profMod - vulnMod;
		if(modDiff != 1)
			chance *= 1.+ (80 + modDiff) / 200;

		if(!target.isInCombat())
			chance *= 1.1;

		if(activeChar.isDistortedSpace())
			chance *= 1.3;
		else
		{
			switch(PositionUtils.getDirectionTo(target, activeChar))
			{
				case BEHIND:
					chance *= 1.3;
					break;
				case SIDE:
					chance *= 1.1;
					break;
				case FRONT:
					if(skill.isBehind())
						chance = 3.0;
					break;
			}
		}
		
		chance = Math.min(skill.isBehind() ? Config.MAX_BLOW_RATE_ON_BEHIND : Config.MAX_BLOW_RATE_ON_FRONT_AND_SIDE, chance);
		return Rnd.chance(chance);
	}

	
	public static boolean calcPCrit(Creature attacker, Creature target, Skill skill, boolean blow)
	{
		if(attacker.isPlayer() && attacker.getActiveWeaponTemplate() == null)
			return false;
		if(skill != null)
		{
			
			boolean dexDep = attacker.calcStat(Stats.P_SKILL_CRIT_RATE_DEX_DEPENDENCE) > 0;
			double skillRate = skill.getCriticalRate() * 0.01 * attacker.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, target, skill);

			
			if(dexDep && attacker.getDEX() > attacker.getTemplate().getBaseDEX())
			{
				int statModifier = 100 + attacker.getDEX() - attacker.getTemplate().getBaseDEX();
				if(blow)
					statModifier *= Config.BLOW_SKILL_DEX_CHANCE_MOD;
				else
					statModifier *= Config.NORMAL_SKILL_DEX_CHANCE_MOD;
				skillRate *= statModifier * 0.01;
			}
			if(blow)
				skillRate *= Config.ALT_BLOW_CRIT_RATE_MODIFIER;
			return Rnd.chance(skillRate * getPCritChanceMode(attacker));
		}
		double rate = attacker.getPCriticalHit(target) * 0.01 * target.calcStat(Stats.P_CRIT_CHANCE_RECEPTIVE, attacker, skill);

		if(attacker.isDistortedSpace())
			rate *= 1.4;
		else
		{
			switch(PositionUtils.getDirectionTo(target, attacker))
			{
				case BEHIND:
					rate *= 1.4;
					break;
				case SIDE:
					rate *= 1.2;
					break;
			}
		}

		return Rnd.chance(rate / 10 * getPCritChanceMode(attacker));
	}

	public static boolean calcMCrit(Creature attacker, Creature target, Skill skill)
	{
		double rate = attacker.getMCriticalHit(target, skill) * skill.getCriticalRateMod() * 0.01 * target.calcStat(Stats.M_CRIT_CHANCE_RECEPTIVE, attacker, skill);
		
		return Rnd.chance(rate / 15 * getMCritChanceMode(attacker));
	}

	public static boolean calcCastBreak(Creature target, boolean crit)
	{
		if(target == null || target.isInvulnerable() || target.isRaid() || (!target.isCastingNow() && !target.isDualCastingNow()))
			return false;

		Skill skill = target.getCastingSkill();
		if(skill != null && (skill.isPhysic() || skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == Skill.SkillType.TAKEFORTRESS))
			return false;

	    skill = target.getDualCastingSkill();
	    if(skill != null && (skill.isPhysic() || skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == Skill.SkillType.TAKEFORTRESS))
	      return false;
	      
		return Rnd.chance(target.calcStat(Stats.CAST_INTERRUPT, crit ? 75 : 10, null, skill));
	}

	
	public static int calcPAtkSpd(double rate)
	{
		return (int) (500000 / rate); 
	}

	
	public static int calcSkillCastSpd(Creature attacker, Skill skill, double skillTime)
	{
		if(skill.isMagic())
			return (int) (skillTime * 333 / Math.max(attacker.getMAtkSpd(), 1));
		if(skill.isPhysic())
			return (int) (skillTime * 333 / Math.max(attacker.getPAtkSpd(), 1));
		return (int) skillTime;
	}

	
	public static long calcSkillReuseDelay(Creature actor, Skill skill)
	{
		long reuseDelay = skill.getReuseDelay();
		if(actor.isMonster())
			reuseDelay = skill.getReuseForMonsters();
		if(skill.isHandler() || skill.isItemSkill())
			return reuseDelay;
	    if(actor.getSkillMastery(skill.getId()) == 1 && (!skill.isReuseDelayPermanent() || skill.getMasteryLevel() != -1))
	    {
	      actor.removeSkillMastery(skill.getId());
	      return 0;
	    }
		if(skill.isReuseDelayPermanent())
			return reuseDelay;
		if(skill.isMusic())
			return (long) actor.calcStat(Stats.MUSIC_REUSE_RATE, reuseDelay, null, skill);
		if(skill.isMagic())
			return (long) actor.calcStat(Stats.MAGIC_REUSE_RATE, reuseDelay, null, skill);
		return (long) actor.calcStat(Stats.PHYSIC_REUSE_RATE, reuseDelay, null, skill);
	}

	private static double getConditionBonus(Creature attacker, Creature target)
	{
		double mod = 100;
		
		if((attacker.getZ() - target.getZ()) > 50)
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.HIGH);
		else if ((attacker.getZ() - target.getZ()) < -50)
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.LOW);
		
		
		if(GameTimeController.getInstance().isNowNight())
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.DARK);

		

		if(attacker.isDistortedSpace())
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.BACK);
		else
		{
			PositionUtils.TargetDirection direction = PositionUtils.getDirectionTo(attacker, target);
			switch(direction)
			{
				case BEHIND:
					mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.BACK);
					break;
				case SIDE:
					mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.SIDE);
					break;
				default:
					mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.AHEAD);
					break;
			}
		}

		
		return Math.max(mod / 100, 0);
	}

	
	public static boolean calcHitMiss(Creature attacker, Creature target)
	{
		double chanceToHit = 100. - (10 * Math.pow(1.1, target.getPEvasionRate(attacker) - attacker.getPAccuracy()));

		chanceToHit *= getConditionBonus(attacker, target);
		chanceToHit = Math.max(chanceToHit, Config.PHYSICAL_MIN_CHANCE_TO_HIT);
		chanceToHit = Math.min(chanceToHit, Config.PHYSICAL_MAX_CHANCE_TO_HIT);

		return !Rnd.chance(chanceToHit);
	}

	private static boolean calcMagicHitMiss(Skill skill, Creature attacker, Creature target)
	{
		double chanceToHitMiss = target.getMEvasionRate(attacker) - attacker.getMAccuracy();

		chanceToHitMiss = Math.max(chanceToHitMiss, Config.MAGIC_MIN_CHANCE_TO_HIT_MISS);
		chanceToHitMiss = Math.min(chanceToHitMiss, Config.MAGIC_MAX_CHANCE_TO_HIT_MISS);
		chanceToHitMiss = Math.min(skill.getMaxMissChance(), chanceToHitMiss);

		return Rnd.chance(chanceToHitMiss);
	}

	
	public static boolean calcShldUse(Creature attacker, Creature target)
	{
		WeaponTemplate template = target.getSecondaryWeaponTemplate();
		if(template == null || template.getItemType() != WeaponType.NONE)
			return false;
		int angle = (int) target.calcStat(Stats.SHIELD_ANGLE, attacker, null);
		if(angle < 360 && !PositionUtils.isFacing(target, attacker, angle))
			return false;
		return Rnd.chance((int) target.calcStat(Stats.SHIELD_RATE, attacker, null));
	}

	public static boolean calcEffectsSuccess(Creature caster, Creature target, Skill skill, int activateRate)
	{
		if(activateRate == -1)
			return true;

		boolean debugCaster = false;
		boolean debugTarget = false;
		boolean debugGlobal = false;
		if(Config.ALT_DEBUG_ENABLED)
		{
			
			debugCaster = caster.getPlayer() != null && caster.getPlayer().isDebug();
			
			debugTarget = target.getPlayer() != null && target.getPlayer().isDebug();
			
			final boolean debugPvP = Config.ALT_DEBUG_PVP_ENABLED && (debugCaster && debugTarget) && (!Config.ALT_DEBUG_PVP_DUEL_ONLY || (caster.getPlayer().isInDuel() && target.getPlayer().isInDuel()));
			
			debugGlobal = debugPvP || (Config.ALT_DEBUG_PVE_ENABLED && ((debugCaster && target.isMonster()) || (debugTarget && caster.isMonster())));
		}

		int magicLevel = skill.getMagicLevel();
		if(magicLevel <= -1)
			magicLevel = target.getLevel() + 3;

		double targetBasicProperty = getAbnormalResist(skill.getBasicProperty(), target);
		double baseMod = (magicLevel - target.getLevel() + 3) * skill.getLevelBonusRate() + activateRate + 30. - targetBasicProperty;
		double elementMod = calcAttributeBonus(caster, target, skill);
		double traitMod = calcGeneralTraitBonus(caster, target, skill.getTraitType(), false);
		double basicPropertyResist = getBasicPropertyResistBonus(skill.getBasicProperty(), target);
		double buffDebuffMod = 1. + (skill.isOffensive() ? target.calcStat(Stats.RESIST_ABNORMAL_DEBUFF, 0) : target.calcStat(Stats.RESIST_ABNORMAL_BUFF, 0)) / 100.;
		double rate = baseMod * elementMod * traitMod * buffDebuffMod;
		double finalRate = traitMod > 0 ? Math.min(skill.getMaxChance(), Math.max(rate, skill.getMinChance())) * basicPropertyResist : 0;

		final boolean result = finalRate > Rnd.get(100);

		if(debugGlobal)
		{
			StringBuilder stat = new StringBuilder(100);
			stat.append("Effects chance debug: ");
			stat.append(skill.getName());
			stat.append("\nactivateRate: ");
			stat.append(activateRate);
			stat.append("\nbaseMod: ");
			stat.append(String.format("%1.1f", baseMod));
			stat.append("\nelementMod: ");
			stat.append(String.format("%1.1f", elementMod));
			stat.append("\ntraitMod: ");
			stat.append(String.format("%1.1f", traitMod));
			stat.append("\nbuffDebuffMod: ");
			stat.append(String.format("%1.1f", buffDebuffMod));
			stat.append("\nrate: ");
			stat.append(String.format("%1.1f", rate));
			stat.append("\nfinalRate: ");
			stat.append(String.format("%1.1f", finalRate));

			if(!result)
				stat.append(" failed");

			
			if(debugCaster)
				caster.getPlayer().sendMessage(stat.toString());
			if(debugTarget)
				target.getPlayer().sendMessage(stat.toString());
		}
		return result;
	}

	public static void calcSkillMastery(Skill skill, Creature activeChar)
	{
		if(skill.isHandler())
			return;

		boolean calcSkillMastery = false;

		boolean activateINT = activeChar.calcStat(Stats.ACTIVATE_SKILL_MASTERY_INT, 0) > 0;
		if(activateINT)
		{
			
			double chanceINT = activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getINT() / Config.INT_SKILLMASTERY_MODIFIER) / 10; 
			calcSkillMastery = Rnd.chance(chanceINT);
		}

		if(!calcSkillMastery)
		{
			boolean activateSTR = activeChar.calcStat(Stats.ACTIVATE_SKILL_MASTERY_STR, 0) > 0;
			if(activateSTR)
			{
				
				double chanceSTR = activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getSTR() / Config.STR_SKILLMASTERY_MODIFIER) / 10; 
				calcSkillMastery = Rnd.chance(chanceSTR);
			}
		}

		if(calcSkillMastery && !skill.isToggle())
		{
			
			int masteryLevel = 0;
			
			if(Config.OFFLIKE_MASTERY_SYSTEM)
			{
				int skillMastery = skill.getMasteryLevel();	
				if(skillMastery == 0)
					return;

				if(skill.isToggle())
					return;

				switch(skill.getSkillType())
				{
					case CHANGE_CLASS:
					case CLAN_GATE:
					case CRAFT:
					case DEATH_PENALTY:
					case ENCHANT_ARMOR:
					case ENCHANT_WEAPON:
					case EXTRACT_STONE:
					case HARDCODED:
					case KAMAEL_WEAPON_EXCHANGE:
					case LUCK:
					case ADD_PC_BANG:
					case NOTDONE:
					case NOTUSED:
					case PASSIVE:
					case BEAST_FEED:
					case PET_FEED:
					case REFILL:
					case CHAIN_CALL:
					case SOWING:
					case SUMMON_FLAG:
					case RESTORATION:
					case TAKECASTLE:
					case TAKEFORTRESS:
					case TAMECONTROL:
					case WATCHER_GAZE:
					case VITALITY_HEAL:
						return;
				}

				if(skillMastery == -1)
				{
					
					
					if(skill.hasEffects(EffectUseType.NORMAL))
						masteryLevel = 2;
					else
						masteryLevel = 1;	
				}
				else
					masteryLevel = skillMastery;	

				activeChar.setSkillMastery(skill.getId(), masteryLevel);
			}
			else
			{
				if(skill.hasEffects(EffectUseType.NORMAL))
					masteryLevel = 2;
				else
					masteryLevel = 1;

				if(masteryLevel > 0)
					activeChar.setSkillMastery(skill.getId(), masteryLevel);
			}
		}
	}

	public static double calcDamageResists(Skill skill, Creature attacker, Creature defender, double value)
	{
		if(attacker == defender) 
			return value; 
		if(attacker.isBoss())
			value *= Config.RATE_EPIC_ATTACK;
		else if(attacker.isRaid() || attacker instanceof ReflectionBossInstance)
			value *= Config.RATE_RAID_ATTACK;

		if(defender.isBoss())
			value /= Config.RATE_EPIC_DEFENSE;
		else if(defender.isRaid() || defender instanceof ReflectionBossInstance)
			value /= Config.RATE_RAID_DEFENSE;

		Player pAttacker = attacker.getPlayer();

		
		int diff = defender.getLevel() - (pAttacker != null ? pAttacker.getLevel() : attacker.getLevel());
		if(attacker.isPlayable() && defender.isMonster() && defender.getLevel() >= 78 && diff > 2)
			value *= .7 / Math.pow(diff - 2, .25);

		return value;
	}

	
	private static double getElementMod(double defense, double attack)
	{
		double diff = attack - defense;
		if(diff > 0)
			diff = 1.025 + Math.sqrt(Math.pow(Math.abs(diff), 3) / 2.) * 0.0001;
		else if(diff < 0)
			diff = 0.975 - Math.sqrt(Math.pow(Math.abs(diff), 3) / 2.) * 0.0001;
		else
			diff = 1;

		diff = Math.max(diff, 0.75);
		diff = Math.min(diff, 1.25);
		return diff;
	}

	
	public static Element getAttackElement(Creature attacker, Creature target)
	{
		double val, max = Double.MIN_VALUE;
		Element result = Element.NONE;
		for(Element e : Element.VALUES)
		{
			val = attacker.calcStat(e.getAttack(), 0.);
			if(val <= 0.)
				continue;

			if(target != null)
				val -= target.calcStat(e.getDefence(), 0.);

			if(val > max)
			{
				result = e;
				max = val;
			}
		}

		return result;
	}

	public static int calculateKarmaLost(Player player, long exp)
	{
		if(Config.RATE_KARMA_LOST_STATIC != -1)
			return Config.RATE_KARMA_LOST_STATIC;

		double karmaLooseMul = KarmaIncreaseDataHolder.getInstance().getData(player.getLevel());
		if(exp > 0) 
			exp /= Config.KARMA_RATE_KARMA_LOST == -1 ? Config.RATE_XP_BY_LVL[player.getLevel()] : Config.KARMA_RATE_KARMA_LOST;
		return (int) ((Math.abs(exp) / karmaLooseMul) / 15);
	}

	public static boolean calcCraftingMastery(Player player)
	{
		if(player.getSkillLevel(Skill.SKILL_CRAFTING_MASTERY) > 0)
		{
			if(Rnd.chance(CRAFTING_MASTERY_CHANCE))
				return true;
		}
		return false;
	}

	public static boolean tryLuck(Player player)
	{
		
		double luc = player.getLUC() * Config.LUC_BONUS_MODIFIER;
		if(luc > 0)
			return Rnd.chance(luc);
		return false;
	}

	public static double calcCancelChance(Creature attacker, Creature target, double baseChance, Skill skill, Abnormal abnormal)
	{
		int cancelLevel = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel();
		int abnormalLevel = abnormal.getSkill().getMagicLevel() > 0 ? abnormal.getSkill().getMagicLevel() : target.getLevel();
		int abnormalTime = abnormal.getSkill().getAbnormalTime();
		double cancelPower = attacker.calcStat(Stats.CANCEL_POWER, 100., null, null);
		double cancelResist = target.calcStat(Stats.CANCEL_RESIST, 100., null, null);
		double chance = (2 * (cancelLevel - abnormalLevel) + baseChance + abnormalTime / 1200000) * 0.01 * cancelPower * 0.01 * cancelResist;
		return Math.max(Math.min(Config.CANCEL_SKILLS_HIGH_CHANCE_CAP, chance), Config.CANCEL_SKILLS_LOW_CHANCE_CAP);
	}

	public static double getAbnormalResist(BasicProperty basicProperty, Creature target)
	{
		switch(basicProperty)
		{
			case PHYSICAL_ABNORMAL_RESIST:
				return target.getPhysicalAbnormalResist();
			case MAGIC_ABNORMAL_RESIST:
				return target.getMagicAbnormalResist();
			default:
				return 0;
		}
	}

	
	public static double calcAttributeBonus(Creature attacker, Creature target, Skill skill)
	{
		int attack_attribute;
		int defence_attribute;

		if(skill != null)
		{
			if(skill.getElement() == Element.NONE || skill.getElement() == Element.NONE_ARMOR)
			{
				attack_attribute = 0;
				defence_attribute = target.getDefence(Element.NONE_ARMOR);
			}
			else
			{
				if(attacker.getAttackElement() == skill.getElement())
				{
					attack_attribute = attacker.getAttack(attacker.getAttackElement()) + skill.getElementPower();
					defence_attribute = target.getDefence(attacker.getAttackElement());
				}
				else
				{
					attack_attribute = skill.getElementPower();
					defence_attribute = target.getDefence(skill.getElement());
				}
			}
		}
		else
		{
			attack_attribute = attacker.getAttack(attacker.getAttackElement());
			defence_attribute = target.getDefence(attacker.getAttackElement());
		}

		final int diff = attack_attribute - defence_attribute;
		if(diff > 0)
			return Math.min(1.025 + (Math.sqrt(Math.pow(diff, 3) / 2) * 0.0001), 1.25);
		else if (diff < 0)
			return Math.max(0.975 - (Math.sqrt(Math.pow(-diff, 3) / 2) * 0.0001), 0.75);

		return 1;
	}

	public static double calcGeneralTraitBonus(Creature attacker, Creature target, SkillTrait trait, boolean ignoreResistance)
	{
		if(trait == SkillTrait.NONE)
			return 1.0;

		double targetDefence = target.calcStat(trait.getDefence());
		if(targetDefence == Double.POSITIVE_INFINITY)
			return 0.;

		double targetDefenceModifier = (targetDefence + 100.) / 100.;
		double attackerAttackModifier = (attacker.calcStat(trait.getAttack()) + 100.) / 100.;

		switch(trait.getType())
		{
			case WEAKNESS:
				if(attackerAttackModifier == 1 || targetDefenceModifier == 1)
					return 1.0;
				break;
			case RESISTANCE:
				if(ignoreResistance)
					return 1.0;
				break;
			default:
				return 1.0;
		}

		final double result = attackerAttackModifier - targetDefenceModifier + 1.0;
		return Math.max(0.05, Math.min(2.0, result));
	}

	public static double calcWeaponTraitBonus(Creature attacker, Creature target)
	{
		SkillTrait type = attacker.getBaseStats().getAttackType().getTrait();
		Stats defenceStat = type.getDefence();
		if(defenceStat != null)
		{
			double targetDefenceModifier = (target.calcStat(defenceStat) + 100.) / 100.;
			double result = targetDefenceModifier - 1.0;
			return 1.0 - result;
		}
		return 1.0;
	}


	public static double calcAttackTraitBonus(Creature attacker, Creature target)
	{
		double weaponTraitBonus = calcWeaponTraitBonus(attacker, target);
		if(weaponTraitBonus == 0)
			return 0;

		double weaknessBonus = 1.0;
		for(SkillTrait traitType : SkillTrait.VALUES)
		{
			if(traitType.getType() == SkillTraitType.WEAKNESS)
			{
				weaknessBonus *= calcGeneralTraitBonus(attacker, target, traitType, true);
				if(weaknessBonus == 0)
					return 0;
			}
		}
		return Math.max(0.05, Math.min(2.0, weaponTraitBonus * weaknessBonus));
	}

	public static double getBasicPropertyResistBonus(BasicProperty basicProperty, Creature target)
	{
		if(basicProperty == BasicProperty.NONE || !target.hasBasicPropertyResist())
			return 1.0;

		BasicPropertyResist resist = target.getBasicPropertyResist(basicProperty);
		switch(resist.getResistLevel())
		{
			case 0:
				return 1.0;
			case 1:
				return 0.6;
			case 2:
				return 0.3;
			default:
				return 0;
		}
	}
}