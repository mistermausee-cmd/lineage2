package l2s.gameserver.model.base;

import l2s.gameserver.data.xml.holder.ClassDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.templates.player.ClassData;

public enum ClassId
{
	
	HUMAN_FIGHTER(ClassType.FIGHTER, Race.HUMAN, null, ClassLevel.NONE, null),
	WARRIOR(ClassType.FIGHTER, Race.HUMAN, HUMAN_FIGHTER, ClassLevel.FIRST, null),
	GLADIATOR(ClassType.FIGHTER, Race.HUMAN, WARRIOR, ClassLevel.SECOND, ClassType2.WARRIOR),
	WARLORD(ClassType.FIGHTER, Race.HUMAN, WARRIOR, ClassLevel.SECOND, ClassType2.WARRIOR),
	KNIGHT(ClassType.FIGHTER, Race.HUMAN, HUMAN_FIGHTER, ClassLevel.FIRST, null),
	PALADIN(ClassType.FIGHTER, Race.HUMAN, KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	DARK_AVENGER(ClassType.FIGHTER, Race.HUMAN, KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	ROGUE(ClassType.FIGHTER, Race.HUMAN, HUMAN_FIGHTER, ClassLevel.FIRST, null),
	TREASURE_HUNTER(ClassType.FIGHTER, Race.HUMAN, ROGUE, ClassLevel.SECOND, ClassType2.ROGUE),
	HAWKEYE(ClassType.FIGHTER, Race.HUMAN, ROGUE, ClassLevel.SECOND, ClassType2.ARCHER),

	
	HUMAN_MAGE(ClassType.MYSTIC, Race.HUMAN, null, ClassLevel.NONE, null),
	WIZARD(ClassType.MYSTIC, Race.HUMAN, HUMAN_MAGE, ClassLevel.FIRST, null),
	SORCERER(ClassType.MYSTIC, Race.HUMAN, WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	NECROMANCER(ClassType.MYSTIC, Race.HUMAN, WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	WARLOCK(ClassType.MYSTIC, Race.HUMAN, WIZARD, ClassLevel.SECOND, ClassType2.SUMMONER),
	CLERIC(ClassType.MYSTIC, Race.HUMAN, HUMAN_MAGE, ClassLevel.FIRST, null),
	BISHOP(ClassType.MYSTIC, Race.HUMAN, CLERIC, ClassLevel.SECOND, ClassType2.HEALER),
	PROPHET(ClassType.MYSTIC, Race.HUMAN, CLERIC, ClassLevel.SECOND, ClassType2.ENCHANTER),

	
	ELVEN_FIGHTER(ClassType.FIGHTER, Race.ELF, null, ClassLevel.NONE, null),
	ELVEN_KNIGHT(ClassType.FIGHTER, Race.ELF, ELVEN_FIGHTER, ClassLevel.FIRST, null),
	TEMPLE_KNIGHT(ClassType.FIGHTER, Race.ELF, ELVEN_KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	SWORDSINGER(ClassType.FIGHTER, Race.ELF, ELVEN_KNIGHT, ClassLevel.SECOND, ClassType2.ENCHANTER),
	ELVEN_SCOUT(ClassType.FIGHTER, Race.ELF, ELVEN_FIGHTER, ClassLevel.FIRST, null),
	PLAIN_WALKER(ClassType.FIGHTER, Race.ELF, ELVEN_SCOUT, ClassLevel.SECOND, ClassType2.ROGUE),
	SILVER_RANGER(ClassType.FIGHTER, Race.ELF, ELVEN_SCOUT, ClassLevel.SECOND, ClassType2.ARCHER),

	
	ELVEN_MAGE(ClassType.MYSTIC, Race.ELF, null, ClassLevel.NONE, null),
	ELVEN_WIZARD(ClassType.MYSTIC, Race.ELF, ELVEN_MAGE, ClassLevel.FIRST, null),
	SPELLSINGER(ClassType.MYSTIC, Race.ELF, ELVEN_WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	ELEMENTAL_SUMMONER(ClassType.MYSTIC, Race.ELF, ELVEN_WIZARD, ClassLevel.SECOND, ClassType2.SUMMONER),
	ORACLE(ClassType.MYSTIC, Race.ELF, ELVEN_MAGE, ClassLevel.FIRST, null),
	ELDER(ClassType.MYSTIC, Race.ELF, ORACLE, ClassLevel.SECOND, ClassType2.HEALER),

	
	DARK_FIGHTER(ClassType.FIGHTER, Race.DARKELF, null, ClassLevel.NONE, null),
	PALUS_KNIGHT(ClassType.FIGHTER, Race.DARKELF, DARK_FIGHTER, ClassLevel.FIRST, null),
	SHILLEN_KNIGHT(ClassType.FIGHTER, Race.DARKELF, PALUS_KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	BLADEDANCER(ClassType.FIGHTER, Race.DARKELF, PALUS_KNIGHT, ClassLevel.SECOND, ClassType2.ENCHANTER),
	ASSASIN(ClassType.FIGHTER, Race.DARKELF, DARK_FIGHTER, ClassLevel.FIRST, null),
	ABYSS_WALKER(ClassType.FIGHTER, Race.DARKELF, ASSASIN, ClassLevel.SECOND, ClassType2.ROGUE),
	PHANTOM_RANGER(ClassType.FIGHTER, Race.DARKELF, ASSASIN, ClassLevel.SECOND, ClassType2.ARCHER),

	
	DARK_MAGE(ClassType.MYSTIC, Race.DARKELF, null, ClassLevel.NONE, null),
	DARK_WIZARD(ClassType.MYSTIC, Race.DARKELF, DARK_MAGE, ClassLevel.FIRST, null),
	SPELLHOWLER(ClassType.MYSTIC, Race.DARKELF, DARK_WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	PHANTOM_SUMMONER(ClassType.MYSTIC, Race.DARKELF, DARK_WIZARD, ClassLevel.SECOND, ClassType2.SUMMONER),
	SHILLEN_ORACLE(ClassType.MYSTIC, Race.DARKELF, DARK_MAGE, ClassLevel.FIRST, null),
	SHILLEN_ELDER(ClassType.MYSTIC, Race.DARKELF, SHILLEN_ORACLE, ClassLevel.SECOND, ClassType2.HEALER),

	
	ORC_FIGHTER(ClassType.FIGHTER, Race.ORC, null, ClassLevel.NONE, null),
	ORC_RAIDER(ClassType.FIGHTER, Race.ORC, ORC_FIGHTER, ClassLevel.FIRST, null),
	DESTROYER(ClassType.FIGHTER, Race.ORC, ORC_RAIDER, ClassLevel.SECOND, ClassType2.WARRIOR),
	ORC_MONK(ClassType.FIGHTER, Race.ORC, ORC_FIGHTER, ClassLevel.FIRST, null),
	TYRANT(ClassType.FIGHTER, Race.ORC, ORC_MONK, ClassLevel.SECOND, ClassType2.WARRIOR),

	
	ORC_MAGE(ClassType.MYSTIC, Race.ORC, null, ClassLevel.NONE, null),
	ORC_SHAMAN(ClassType.MYSTIC, Race.ORC, ORC_MAGE, ClassLevel.FIRST, null),
	OVERLORD(ClassType.MYSTIC, Race.ORC, ORC_SHAMAN, ClassLevel.SECOND, ClassType2.ENCHANTER),
	WARCRYER(ClassType.MYSTIC, Race.ORC, ORC_SHAMAN, ClassLevel.SECOND, ClassType2.ENCHANTER),

	
	DWARVEN_FIGHTER(ClassType.FIGHTER, Race.DWARF, null, ClassLevel.NONE, null),
	SCAVENGER(ClassType.FIGHTER, Race.DWARF, DWARVEN_FIGHTER, ClassLevel.FIRST, null),
	BOUNTY_HUNTER(ClassType.FIGHTER, Race.DWARF, SCAVENGER, ClassLevel.SECOND, ClassType2.ROGUE),
	ARTISAN(ClassType.FIGHTER, Race.DWARF, DWARVEN_FIGHTER, ClassLevel.FIRST, null),
	WARSMITH(ClassType.FIGHTER, Race.DWARF, ARTISAN, ClassLevel.SECOND, ClassType2.WARRIOR),

	
	DUMMY_ENTRY_58,
	DUMMY_ENTRY_59,
	DUMMY_ENTRY_60,
	DUMMY_ENTRY_61,
	DUMMY_ENTRY_62,
	DUMMY_ENTRY_63,
	DUMMY_ENTRY_64,
	DUMMY_ENTRY_65,
	DUMMY_ENTRY_66,
	DUMMY_ENTRY_67,
	DUMMY_ENTRY_68,
	DUMMY_ENTRY_69,
	DUMMY_ENTRY_70,
	DUMMY_ENTRY_71,
	DUMMY_ENTRY_72,
	DUMMY_ENTRY_73,
	DUMMY_ENTRY_74,
	DUMMY_ENTRY_75,
	DUMMY_ENTRY_76,
	DUMMY_ENTRY_77,
	DUMMY_ENTRY_78,
	DUMMY_ENTRY_79,
	DUMMY_ENTRY_80,
	DUMMY_ENTRY_81,
	DUMMY_ENTRY_82,
	DUMMY_ENTRY_83,
	DUMMY_ENTRY_84,
	DUMMY_ENTRY_85,
	DUMMY_ENTRY_86,
	DUMMY_ENTRY_87,

	
	DUELIST(ClassType.FIGHTER, Race.HUMAN, GLADIATOR, ClassLevel.THIRD, ClassType2.WARRIOR),
	DREADNOUGHT(ClassType.FIGHTER, Race.HUMAN, WARLORD, ClassLevel.THIRD, ClassType2.WARRIOR),
	PHOENIX_KNIGHT(ClassType.FIGHTER, Race.HUMAN, PALADIN, ClassLevel.THIRD, ClassType2.KNIGHT),
	HELL_KNIGHT(ClassType.FIGHTER, Race.HUMAN, DARK_AVENGER, ClassLevel.THIRD, ClassType2.KNIGHT),
	SAGITTARIUS(ClassType.FIGHTER, Race.HUMAN, HAWKEYE, ClassLevel.THIRD, ClassType2.ARCHER),
	ADVENTURER(ClassType.FIGHTER, Race.HUMAN, TREASURE_HUNTER, ClassLevel.THIRD, ClassType2.ROGUE),

	
	ARCHMAGE(ClassType.MYSTIC, Race.HUMAN, SORCERER, ClassLevel.THIRD, ClassType2.WIZARD),
	SOULTAKER(ClassType.MYSTIC, Race.HUMAN, NECROMANCER, ClassLevel.THIRD, ClassType2.WIZARD),
	ARCANA_LORD(ClassType.MYSTIC, Race.HUMAN, WARLOCK, ClassLevel.THIRD, ClassType2.SUMMONER),
	CARDINAL(ClassType.MYSTIC, Race.HUMAN, BISHOP, ClassLevel.THIRD, ClassType2.HEALER),
	HIEROPHANT(ClassType.MYSTIC, Race.HUMAN, PROPHET, ClassLevel.THIRD, ClassType2.ENCHANTER),

	
	EVAS_TEMPLAR(ClassType.FIGHTER, Race.ELF, TEMPLE_KNIGHT, ClassLevel.THIRD, ClassType2.KNIGHT),
	SWORD_MUSE(ClassType.FIGHTER, Race.ELF, SWORDSINGER, ClassLevel.THIRD, ClassType2.ENCHANTER),
	WIND_RIDER(ClassType.FIGHTER, Race.ELF, PLAIN_WALKER, ClassLevel.THIRD, ClassType2.ROGUE),
	MOONLIGHT_SENTINEL(ClassType.FIGHTER, Race.ELF, SILVER_RANGER, ClassLevel.THIRD, ClassType2.ARCHER),

	
	MYSTIC_MUSE(ClassType.MYSTIC, Race.ELF, SPELLSINGER, ClassLevel.THIRD, ClassType2.WIZARD),
	ELEMENTAL_MASTER(ClassType.MYSTIC, Race.ELF, ELEMENTAL_SUMMONER, ClassLevel.THIRD, ClassType2.SUMMONER),
	EVAS_SAINT(ClassType.MYSTIC, Race.ELF, ELDER, ClassLevel.THIRD, ClassType2.HEALER),

	
	SHILLIEN_TEMPLAR(ClassType.FIGHTER, Race.DARKELF, SHILLEN_KNIGHT, ClassLevel.THIRD, ClassType2.KNIGHT),
	SPECTRAL_DANCER(ClassType.FIGHTER, Race.DARKELF, BLADEDANCER, ClassLevel.THIRD, ClassType2.ENCHANTER),
	GHOST_HUNTER(ClassType.FIGHTER, Race.DARKELF, ABYSS_WALKER, ClassLevel.THIRD, ClassType2.ROGUE),
	GHOST_SENTINEL(ClassType.FIGHTER, Race.DARKELF, PHANTOM_RANGER, ClassLevel.THIRD, ClassType2.ARCHER),

	
	STORM_SCREAMER(ClassType.MYSTIC, Race.DARKELF, SPELLHOWLER, ClassLevel.THIRD, ClassType2.WIZARD),
	SPECTRAL_MASTER(ClassType.MYSTIC, Race.DARKELF, PHANTOM_SUMMONER, ClassLevel.THIRD, ClassType2.SUMMONER),
	SHILLIEN_SAINT(ClassType.MYSTIC, Race.DARKELF, SHILLEN_ELDER, ClassLevel.THIRD, ClassType2.HEALER),

	
	TITAN(ClassType.FIGHTER, Race.ORC, DESTROYER, ClassLevel.THIRD, ClassType2.WARRIOR),
	GRAND_KHAVATARI(ClassType.FIGHTER, Race.ORC, TYRANT, ClassLevel.THIRD, ClassType2.WARRIOR),

	
	DOMINATOR(ClassType.MYSTIC, Race.ORC, OVERLORD, ClassLevel.THIRD, ClassType2.ENCHANTER),
	DOOMCRYER(ClassType.MYSTIC, Race.ORC, WARCRYER, ClassLevel.THIRD, ClassType2.ENCHANTER),

	
	FORTUNE_SEEKER(ClassType.FIGHTER, Race.DWARF, BOUNTY_HUNTER, ClassLevel.THIRD, ClassType2.ROGUE),
	MAESTRO(ClassType.FIGHTER, Race.DWARF, WARSMITH, ClassLevel.THIRD, ClassType2.WARRIOR),

	
	DUMMY_ENTRY_119,
	DUMMY_ENTRY_120,
	DUMMY_ENTRY_121,
	DUMMY_ENTRY_122,

	
	KAMAEL_M_SOLDIER(ClassType.FIGHTER, Race.KAMAEL, null, ClassLevel.NONE, null),
	KAMAEL_F_SOLDIER(ClassType.FIGHTER, Race.KAMAEL, null, ClassLevel.NONE, null),
	TROOPER(ClassType.FIGHTER, Race.KAMAEL, KAMAEL_M_SOLDIER, ClassLevel.FIRST, null),
	WARDER(ClassType.FIGHTER, Race.KAMAEL, KAMAEL_F_SOLDIER, ClassLevel.FIRST, null),
	BERSERKER(ClassType.FIGHTER, Race.KAMAEL, TROOPER, ClassLevel.SECOND, ClassType2.WARRIOR),
	M_SOUL_BREAKER(ClassType.FIGHTER, Race.KAMAEL, TROOPER, ClassLevel.SECOND, ClassType2.WIZARD),
	F_SOUL_BREAKER(ClassType.FIGHTER, Race.KAMAEL, WARDER, ClassLevel.SECOND, ClassType2.WIZARD),
	ARBALESTER(ClassType.FIGHTER, Race.KAMAEL, WARDER, ClassLevel.SECOND, ClassType2.ARCHER),
	DOOMBRINGER(ClassType.FIGHTER, Race.KAMAEL, BERSERKER, ClassLevel.THIRD, ClassType2.WARRIOR),
	M_SOUL_HOUND(ClassType.FIGHTER, Race.KAMAEL, M_SOUL_BREAKER, ClassLevel.THIRD, ClassType2.WIZARD),
	F_SOUL_HOUND(ClassType.FIGHTER, Race.KAMAEL, F_SOUL_BREAKER, ClassLevel.THIRD, ClassType2.WIZARD),
	TRICKSTER(ClassType.FIGHTER, Race.KAMAEL, ARBALESTER, ClassLevel.THIRD, ClassType2.ARCHER),
	INSPECTOR(ClassType.FIGHTER, Race.KAMAEL, TROOPER, WARDER, ClassLevel.SECOND, ClassType2.ENCHANTER),
	JUDICATOR(ClassType.FIGHTER, Race.KAMAEL, INSPECTOR, ClassLevel.THIRD, ClassType2.ENCHANTER),

	
	DUMMY_ENTRY_137,
	DUMMY_ENTRY_138,

	
	SIGEL_KNIGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return PHOENIX_KNIGHT;

			switch(classId)
			{
				case HELL_KNIGHT:
				case EVAS_TEMPLAR:
				case SHILLIEN_TEMPLAR:
					return classId;
				default:
					return PHOENIX_KNIGHT;
			}
		}
	},
	TYR_WARRIOR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return DUELIST;

			switch(classId)
			{
				case DREADNOUGHT:
				case TITAN:
				case GRAND_KHAVATARI:
				case MAESTRO:
				case DOOMBRINGER:
					return classId;
				default:
					return DUELIST;
			}
		}
	},
	OTHELL_ROGUE(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return ADVENTURER;

			switch(classId)
			{
				case WIND_RIDER:
				case GHOST_HUNTER:
				case FORTUNE_SEEKER:
					return classId;
				default:
					return ADVENTURER;
			}
		}
	},
	YR_ARCHER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return SAGITTARIUS;

			switch(classId)
			{
				case MOONLIGHT_SENTINEL:
				case GHOST_SENTINEL:
				case TRICKSTER:
					return classId;
				default:
					return SAGITTARIUS;
			}
		}
	},
	FEOH_WIZARD(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return ARCHMAGE;

			switch(classId)
			{
				case SOULTAKER:
				case MYSTIC_MUSE:
				case STORM_SCREAMER:
				case M_SOUL_HOUND:
				case F_SOUL_HOUND:
					return classId;
				default:
					return ARCHMAGE;
			}
		}
	},
	ISS_ENCHANTER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return HIEROPHANT;

			switch(classId)
			{
				case SWORD_MUSE:
				case SPECTRAL_DANCER:
				case DOMINATOR:
				case DOOMCRYER:
				case JUDICATOR:
					return classId;
				default:
					return HIEROPHANT;
			}
		}
	},
	WYNN_SUMMONER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return ARCANA_LORD;

			switch(classId)
			{
				case ELEMENTAL_MASTER:
				case SPECTRAL_MASTER:
					return classId;
				default:
					return ARCANA_LORD;
			}
		}
	},
	EOLH_HEALER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return CARDINAL;

			switch(classId)
			{
				case EVAS_SAINT:
				case SHILLIEN_SAINT:
					return classId;
				default:
					return CARDINAL;
			}
		}
	},

	
	DUMMY_ENTRY_147,

	
	SIGEL_PHOENIX_KNIGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return PHOENIX_KNIGHT;
		}
	},
	SIGEL_HELL_KNIGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return HELL_KNIGHT;
		}
	},
	SIGEL_EVAS_TEMPLAR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return EVAS_TEMPLAR;
		}
	},
	SIGEL_SHILLIEN_TEMPLAR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SHILLIEN_TEMPLAR;
		}
	},

	
	TYR_DUELIST(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DUELIST;
		}
	},
	TYR_DREADNOUGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DREADNOUGHT;
		}
	},
	TYR_TITAN(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return TITAN;
		}
	},
	TYR_GRAND_KHAVATARI(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return GRAND_KHAVATARI;
		}
	},
	TYR_MAESTRO(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return MAESTRO;
		}
	},
	TYR_DOOMBRINGER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DOOMBRINGER;
		}
	},

	
	OTHELL_ADVENTURER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ADVENTURER;
		}
	},
	OTHELL_WIND_RIDER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return WIND_RIDER;
		}
	},
	OTHELL_GHOST_HUNTER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return GHOST_HUNTER;
		}
	},
	OTHELL_FORTUNE_SEEKER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return FORTUNE_SEEKER;
		}
	},

	
	YR_SAGITTARIUS(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SAGITTARIUS;
		}
	},
	YR_MOONLIGHT_SENTINEL(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return MOONLIGHT_SENTINEL;
		}
	},
	YR_GHOST_SENTINEL(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return GHOST_SENTINEL;
		}
	},
	YR_TRICKSTER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return TRICKSTER;
		}
	},

	
	FEOH_ARCHMAGE(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ARCHMAGE;
		}
	},
	FEOH_SOULTAKER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SOULTAKER;
		}
	},
	FEOH_MYSTIC_MUSE(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return MYSTIC_MUSE;
		}
	},
	FEOH_STORM_SCREAMER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return STORM_SCREAMER;
		}
	},
	FEOH_SOUL_HOUND(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			if(classId == null)
				return M_SOUL_HOUND;

			
			switch(classId)
			{
				case M_SOUL_HOUND:
				case F_SOUL_HOUND:
					return classId;
				default:
					return M_SOUL_HOUND;
			}
		}
	},

	
	ISS_HIEROPHANT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return HIEROPHANT;
		}
	},
	ISS_SWORD_MUSE(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SWORD_MUSE;
		}
	},
	ISS_SPECTRAL_DANCER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SPECTRAL_DANCER;
		}
	},
	ISS_DOMINATOR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DOMINATOR;
		}
	},
	ISS_DOOMCRYER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DOOMCRYER;
		}
	},

	
	WYNN_ARCANA_LORD(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, WYNN_SUMMONER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ARCANA_LORD;
		}
	},
	WYNN_ELEMENTAL_MASTER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, WYNN_SUMMONER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ELEMENTAL_MASTER;
		}
	},
	WYNN_SPECTRAL_MASTER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, WYNN_SUMMONER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SPECTRAL_MASTER;
		}
	},

	
	AEORE_CARDINAL(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, EOLH_HEALER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return CARDINAL;
		}
	},
	AEORE_EVAS_SAINT(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, EOLH_HEALER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return EVAS_SAINT;
		}
	},
	AEORE_SHILLIEN_SAINT(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, EOLH_HEALER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SHILLIEN_SAINT;
		}
	},

	
	ERTHEIA_FIGHTER(ClassType.FIGHTER, Race.ERTHEIA, null, ClassLevel.NONE, null), 
	ERTHEIA_MAGE(ClassType.MYSTIC, Race.ERTHEIA, null, ClassLevel.NONE, null), 
	MARAUDER(ClassType.FIGHTER, Race.ERTHEIA, ERTHEIA_FIGHTER, ClassLevel.FIRST, null), 
	SAIHA_MAGE(ClassType.MYSTIC, Race.ERTHEIA, ERTHEIA_MAGE, ClassLevel.FIRST, null), 
	RANGER(ClassType.FIGHTER, Race.ERTHEIA, MARAUDER, ClassLevel.SECOND, ClassType2.WARRIOR), 
	STORM_SAIHA_MAGE(ClassType.MYSTIC, Race.ERTHEIA, SAIHA_MAGE, ClassLevel.SECOND, ClassType2.WIZARD), 
	RANGER_GRAVITY(ClassType.FIGHTER, Race.ERTHEIA, RANGER, ClassLevel.THIRD, ClassType2.WARRIOR), 
	SAIHA_RULER(ClassType.MYSTIC, Race.ERTHEIA, STORM_SAIHA_MAGE, ClassLevel.THIRD, ClassType2.WIZARD); 

	public static final ClassId[] VALUES = values();
    
	public static ClassId valueOf(int id)
	{
		if(id < 0 || id >= VALUES.length)
			return null;

		ClassId result = VALUES[id];
		if(result != null && !result.isDummy())
			return result;

		return null;
	}

	private final Race _race;
	private final ClassId _parent;
	private final ClassId _parent_f;
	private final ClassId _firstParentM;
	private final ClassId _firstParentF;
	private final ClassLevel _level;
	private final ClassType _type;
	private final ClassType2 _type2;
	private final boolean _isDummy;
	private final ClassId _baseAwakedClassId;
   
	private ClassId()
	{
		this(null, null, null, null, null, null, true, null);
	}

	private ClassId(ClassType classType, ClassLevel level, ClassType2 type2, ClassId baseAwakedClassId)
	{
		this(classType, null, null, null, level, type2, false, baseAwakedClassId);
	}

	private ClassId(ClassType classType, Race race, ClassId parent, ClassLevel level, ClassType2 type2)
	{
		this(classType, race, parent, null, level, type2, false, null);
	}

	private ClassId(ClassType classType, Race race, ClassId parent, ClassId parent2, ClassLevel level, ClassType2 type2)
	{
		this(classType, race, parent, parent2, level, type2, false, null);
	}

	private ClassId(ClassType classType, Race race, ClassId parent, ClassId parent2, ClassLevel level, ClassType2 type2, boolean isDummy, ClassId baseAwakedClassId)
	{
		_type = classType;
		_race = race;
		_parent = parent;
		_parent_f = parent2;
		_level = level;
		_type2 = type2;
		_isDummy = isDummy;
		_baseAwakedClassId = baseAwakedClassId;
		_firstParentM = _parent == null ? this : _parent.getFirstParent(0);
		_firstParentF = _parent_f == null ? _firstParentM : _parent_f.getFirstParent(1);
	}

	public final int getId()
	{
		return ordinal();
	}

	public final Race getRace()
	{
		return _race;
	}

	public final boolean isOfRace(Race race)
	{
		return _race == race;
	}

	public final ClassLevel getClassLevel()
	{
		return _level;
	}

	public final boolean isOfLevel(ClassLevel level)
	{
		return _level == level;
	}

	public final ClassType getType()
	{
		return _type;
	}

	public final boolean isOfType(ClassType type)
	{
		return _type == type;
	}

	public ClassType2 getType2()
	{
		return _type2;
	}

	public final boolean isOfType2(ClassType2 type)
	{
		return _type2 == type;
	}

	public final boolean isMage()
	{
		return _type.isMagician();
	}

	public final boolean isDummy()
	{
		return _isDummy;
	}

	public boolean childOf(ClassId cid)
	{
		if(isOfLevel(ClassLevel.AWAKED))
		{
			if(isOutdated() || cid.isOfLevel(ClassLevel.AWAKED) && cid.isOutdated())
				return cid.getType2() == getType2();

			ClassId parent = getBaseAwakeParent(cid);
			if(parent == cid)
				return true;

			return parent.childOf(cid);
		}

		if(_parent == null)
			return false;

		if(_parent == cid || _parent_f == cid)
			return true;

		return _parent.childOf(cid);

	}

	public final boolean equalsOrChildOf(ClassId cid)
	{
		return this == cid || childOf(cid);
	}

	public final ClassId getParent(int sex)
	{
		return sex == 0 || _parent_f == null ? _parent : _parent_f;
	}

	public final ClassId getFirstParent(int sex)
	{
		return sex == 0 || _firstParentF == null ? _firstParentM : _firstParentF;
	}

	public ClassData getClassData()
	{
		return ClassDataHolder.getInstance().getClassData(getId());
	}

	public double getBaseCp(int level)
	{
		return getClassData().getHpMpCpData(level).getCP();
	}

	public double getBaseHp(int level)
	{
		return getClassData().getHpMpCpData(level).getHP();
	}

	public double getBaseMp(int level)
	{
		return getClassData().getHpMpCpData(level).getMP();
	}

	public ClassId getBaseAwakedClassId()
	{
		return _baseAwakedClassId;
	}

    public int getBaseId()
    {
        if(getBaseAwakedClassId() != null)
            return getBaseAwakedClassId().getId();
        
        return getId();
    }
    
	public ClassId getAwakeParent(ClassId classId)
	{
		if(getBaseAwakedClassId() != null)
			return getBaseAwakedClassId().getAwakeParent(classId);

		return this;
	}

    public ClassId getBaseAwakeParent(final ClassId classId)
    {
        return getAwakeParent(classId);
    }
    
	public final String getName(Player player)
	{
		return new CustomMessage("l2s.gameserver.model.base.ClassId.name." + getId()).toString(player);
	}

	public boolean isOutdated()
	{
		return isOfLevel(ClassLevel.AWAKED) && getBaseAwakedClassId() == null;
	}

	public ClassId getAwakedClass()
	{
		for(ClassId classId : VALUES)
		{
			if(classId.isDummy())
				continue;

			if(classId.isOutdated())
				continue;

			if(!classId.isOfLevel(ClassLevel.AWAKED))
				continue;

			if(classId.getBaseAwakeParent(this) == this)
				return classId;
		}
		return null;
	}

	public int getClassMinLevel(boolean forNextClass)
	{
		ClassLevel classLevel = getClassLevel();
		if(forNextClass)
		{
			if(classLevel == ClassLevel.AWAKED)
				return -1;

			classLevel = ClassLevel.VALUES[classLevel.ordinal() + 1];
		}

		if(getRace() == Race.ERTHEIA)
		{
			switch(classLevel)
			{
				case FIRST:
					return 40;
				case SECOND:
					return 76;
				case THIRD:
					return 85;
				case AWAKED:
					return -1;
			}
		}
		else
		{
			switch(classLevel)
			{
				case FIRST:
					return 20;
				case SECOND:
					return 40;
				case THIRD:
					return 76;
				case AWAKED:
					return 85;
			}
		}
		return 1;
	}

	public boolean isAwaked()
	{
		return isOfLevel(ClassLevel.AWAKED) || isOfRace(Race.ERTHEIA) && isOfLevel(ClassLevel.THIRD);
	}

	public boolean isLast()
	{
		return isAwaked() || this == JUDICATOR;
	}
}