package l2s.gameserver.templates.item;

import java.util.ArrayList;
import java.util.List;

import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.data.xml.holder.CrystallizationDataHolder;
import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.handler.items.impl.EquipableItemHandler;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.stats.conditions.ConditionPlayerOlympiad;
import l2s.gameserver.stats.funcs.FuncAdd;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2s.gameserver.templates.item.data.CapsuledItemData;
import l2s.gameserver.templates.item.support.CrystallizationInfo;

public abstract class ItemTemplate extends StatTemplate
{
	public static final int ITEM_ID_PC_BANG_POINTS = -100;
	public static final int ITEM_ID_CLAN_REPUTATION_SCORE = -200;
	public static final int ITEM_ID_FAME = -300;
	public static final int ITEM_ID_RAID_POINTS = -500;
	public static final int ITEM_ID_ADENA = 57;

	public static final int ITEM_ID_OLYMPIAD_TOKEN = 13722;
	public static final int ITEM_ID_ROYAL_BEAUTY = 36308;
	public static final int ITEM_ID_AIR_STONE = 39461; 
	public static final int ITEM_ID_TEMPEST_STONE = 39592; 
	public static final int ITEM_ID_ELCYUM_CRYSTAL = 36514; 

	public static final int ITEM_ID_CHAOS_POMANDER = 37374; 
	public static final int ITEM_ID_CHAOS_POMANDER_DUAL_CLASS = 37375; 

	public static final int ITEM_ID_CHAOS_ESSENCE = 36949; 
	public static final int ITEM_ID_CHAOS_ESSENCE_DUAL_CLASS = 37494; 

	public static final int ITEM_ID_SCROLL_OF_AFTERLIFE = 17600; 
	public static final int ITEM_ID_STONE_OF_AWEKENING = 32227; 

	public static final int ITEM_ID_FORMAL_WEAR = 6408;
	public static final int ITEM_ID_HERO_WING = 6842;
	public static final int ITEM_ID_HERO_CLOAK = 30372;
	public static final int ITEM_ID_FAME_CLOAK = 30373;
	public static final int[] HERO_WEAPON_IDS = { 30392, 30393, 30394, 30395, 30396, 30397, 30398, 30399, 30400, 30401, 30402, 30403, 30404, 30405 };

	
	public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
	public static final int TYPE1_SHIELD_ARMOR = 1;
	public static final int TYPE1_OTHER = 2;
	public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;

	
	public static final int TYPE2_WEAPON = 0;
	public static final int TYPE2_SHIELD_ARMOR = 1;
	public static final int TYPE2_ACCESSORY = 2;
	public static final int TYPE2_QUEST = 3;
	public static final int TYPE2_MONEY = 4;
	public static final int TYPE2_OTHER = 5;

	
	
	public static final int PET_EQUIP_TYPE_WOLF = 1;
	public static final int PET_EQUIP_TYPE_HATCHLING = 2;
	public static final int PET_EQUIP_TYPE_STRIDER = 3;
	public static final int PET_EQUIP_TYPE_GREAT_WOLF = 4;
	public static final int PET_EQUIP_TYPE_PENDANT = 5;
	public static final int PET_EQUIP_TYPE_BABY = 6;

	public static final int SLOT_NONE = 0x00000;
	public static final int SLOT_UNDERWEAR = 0x00001;

	public static final int SLOT_R_EAR = 0x00002;
	public static final int SLOT_L_EAR = 0x00004;

	public static final int SLOT_NECK = 0x00008;

	public static final int SLOT_R_FINGER = 0x00010;
	public static final int SLOT_L_FINGER = 0x00020;

	public static final int SLOT_HEAD = 0x00040;
	public static final int SLOT_R_HAND = 0x00080;
	public static final int SLOT_L_HAND = 0x00100;
	public static final int SLOT_GLOVES = 0x00200;
	public static final int SLOT_CHEST = 0x00400;
	public static final int SLOT_LEGS = 0x00800;
	public static final int SLOT_FEET = 0x01000;
	public static final int SLOT_BACK = 0x02000;
	public static final int SLOT_LR_HAND = 0x04000;
	public static final int SLOT_FULL_ARMOR = 0x08000;
	public static final int SLOT_HAIR = 0x10000;
	public static final int SLOT_FORMAL_WEAR = 0x20000;
	public static final int SLOT_DHAIR = 0x40000;
	public static final int SLOT_HAIRALL = 0x80000;
	public static final int SLOT_R_BRACELET = 0x100000;
	public static final int SLOT_L_BRACELET = 0x200000;
	public static final int SLOT_DECO = 0x400000;
	public static final int SLOT_BELT = 0x10000000;
	public static final int SLOT_BROOCH = 0x20000000;
	public static final int SLOT_JEWEL = 0x40000000;

	public static final int SLOT_WOLF = -100;
	public static final int SLOT_HATCHLING = -101;
	public static final int SLOT_STRIDER = -102;
	public static final int SLOT_BABYPET = -103;
	public static final int SLOT_GWOLF = -104;
	public static final int SLOT_PENDANT = -105;

	
	public static final int SLOTS_ARMOR = SLOT_HEAD | SLOT_L_HAND | SLOT_GLOVES | SLOT_CHEST | SLOT_LEGS | SLOT_FEET | SLOT_BACK | SLOT_FULL_ARMOR;
	
	public static final int SLOTS_JEWELRY = SLOT_R_EAR | SLOT_L_EAR | SLOT_NECK | SLOT_R_FINGER | SLOT_L_FINGER;

	public static final int CRYSTAL_NONE = 0;
	public static final int CRYSTAL_D = 1458;
	public static final int CRYSTAL_C = 1459;
	public static final int CRYSTAL_B = 1460;
	public static final int CRYSTAL_A = 1461;
	public static final int CRYSTAL_S = 1462;
	public static final int CRYSTAL_R = 17371;

	private final int _itemId;
	private final String _name;
	private final String _addname;
	private final String _icon;
	private final int _weight;
	private final int _referencePrice;
	private final int _durability;
	private final boolean _temporal;
	private final ItemGrade _grade; 
	private int _flags;
	protected ItemType _type;
	protected int _type1; 
	protected int _type2;
	protected ExItemType _exType = ExItemType.OTHER_ITEMS;

	protected int _petType;

	protected SkillEntry[] _skills;
	private SkillEntry _enchant4Skill = null; 

	private final List<Condition> _conditions = new ArrayList<Condition>();

	private final boolean _stackable;
	private final ItemReuseType _reuseType;
	private final int _reuseDelay;
	private final int _reuseGroup;
	private final List<CapsuledItemData> _capsuledItems = new ArrayList<CapsuledItemData>();

	protected int _bodyPart;
	private final int _crystalCount;
	private final CrystallizationInfo _crystallizeInfo;
	private final int _agathionEnergy;
	private int[] _baseAttributes = new int[6];
	private IntObjectMap<int[]> _enchantOptions = Containers.emptyIntObjectMap();
	private final boolean _isPvP;
	private final ItemQuality _quality;
	private final int _baseEnchantLevel;
	private final long _priceLimit;

	private final int _variationGroupId;

	private int _pAtk = 0;
	private int _mAtk = 0;
	private int _pDef = 0;
	private int _mDef = 0;

	protected ItemTemplate(final StatsSet set)
	{
		_itemId = set.getInteger("item_id");
		_name = set.getString("name");
		_addname = set.getString("add_name", "");
		_icon = set.getString("icon", "");
		_weight = set.getInteger("weight", 0);
		_referencePrice = set.getInteger("price", 0);
		_durability = set.getInteger("durability", -1);
		_temporal = set.getBool("temporal", false);
		_grade = set.getEnum("crystal_type", ItemGrade.class, ItemGrade.NONE); 

		_stackable = set.getBool("stackable", false);
		_bodyPart = set.getInteger("bodypart", 0);
		_reuseType = set.getEnum("reuse_type", ItemReuseType.class, ItemReuseType.NORMAL);
		_reuseDelay = set.getInteger("reuse_delay", 0);
		_reuseGroup = set.getInteger("delay_share_group", -_itemId);
		_agathionEnergy = set.getInteger("agathion_energy", 0);
		_isPvP = set.getBool("is_pvp", false);
		_baseEnchantLevel = set.getInteger("enchanted", 0);

		_crystalCount = set.getInteger("crystal_count", 0);
		_priceLimit = set.getLong("price_limit", 0);
		_crystallizeInfo = new CrystallizationInfo();

		_quality = set.getEnum("item_quality", ItemQuality.class, ItemQuality.NORMAL);

		_variationGroupId = set.getInteger("variation_group_id", 0);

		for(ItemFlags f : ItemFlags.VALUES)
		{
			boolean flag = set.getBool(f.name().toLowerCase(), f.getDefaultValue());
			if(flag)
				activeFlag(f);
		}

		_funcTemplates = FuncTemplate.EMPTY_ARRAY;
		_skills = SkillEntry.EMPTY_ARRAY;

		if(!set.getBool("is_olympiad_can_use", true))
		{
			Condition cond = new ConditionPlayerOlympiad(false);
			cond.setSystemMsg(1508);
			addCondition(cond);
		}
	}

	protected void initEnchantFuncs()
	{
		if(isWeapon())
		{
			attachFunc(new FuncTemplate(null, "Enchant", Stats.POWER_ATTACK, 0x0C, 0));
			attachFunc(new FuncTemplate(null, "Enchant", Stats.MAGIC_ATTACK, 0x0C, 0));
			attachFunc(new FuncTemplate(null, "Enchant", Stats.SOULSHOT_POWER, 0x80, 0));
			attachFunc(new FuncTemplate(null, "Enchant", Stats.SPIRITSHOT_POWER, 0x80, 0));
		}
		else if(isArmor())
		{
			if(_grade.extOrdinal() >= ItemGrade.R.extOrdinal())
			{
				if(_exType == ExItemType.HELMET)
				{
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.P_EVASION_RATE, 0x80, 0));
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.M_EVASION_RATE, 0x80, 0));
				}
				else if(_exType == ExItemType.UPPER_PIECE || _exType == ExItemType.FULL_BODY)
				{
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.POWER_ATTACK, 0x80, 0));
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.MAGIC_ATTACK, 0x80, 0));
				}
				else if(_exType == ExItemType.LOWER_PIECE)
				{
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.BASE_P_CRITICAL_RATE, 0x80, 0));
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.BASE_M_CRITICAL_RATE, 0x80, 0));
				}
				else if(_exType == ExItemType.GLOVES)
				{
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.P_ACCURACY_COMBAT, 0x80, 0));
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.M_ACCURACY_COMBAT, 0x80, 0));
				}
				else if(_exType == ExItemType.FEET)
					attachFunc(new FuncTemplate(null, "EnchantRGrade", Stats.RUN_SPEED, 0x80, 0));
			}

			if(_exType == ExItemType.SHIELD)
				attachFunc(new FuncTemplate(null, "Enchant", Stats.SHIELD_DEFENCE, 0x0C, 0));
			else
				attachFunc(new FuncTemplate(null, "Enchant", Stats.POWER_DEFENCE, 0x0C, 0));
			attachFunc(new FuncTemplate(null, "Enchant", Stats.MAX_HP, 0x80, 0));
		}
		else if(isAccessory())
		{
			attachFunc(new FuncTemplate(null, "Enchant", Stats.MAGIC_DEFENCE, 0x0C, 0));
		}
	}

	public final int getItemId()
	{
		return _itemId;
	}

	public final String getName()
	{
		return _name;
	}

	public final String getName(Player player)
	{
		String name = ItemNameHolder.getInstance().getItemName(player, getItemId());
		return name == null ? _name : name;
	}

	public final String getAdditionalName()
	{
		return _addname;
	}

	public final String getIcon()
	{
		return _icon;
	}

	public final int getWeight()
	{
		return _weight;
	}

	public final int getReferencePrice()
	{
		return _referencePrice;
	}

	public final int getDurability()
	{
		return _durability;
	}

	public final boolean isTemporal()
	{
		return _temporal;
	}

	public ItemType getItemType()
	{
		return _type;
	}

	public final int getType1()
	{
		return _type1;
	}

	public final int getType2()
	{
		return _type2;
	}

	public final ItemGrade getGrade()
	{
		return _grade;
	}

	public abstract long getItemMask();

	public int getBaseAttributeValue(Element element)
	{
		if(element == Element.NONE)
			return 0;
		return _baseAttributes[element.getId()];
	}

	public final void setBaseAtributeElements(int[] val)
	{
		_baseAttributes = val;
	}

	public int getBaseEnchantLevel()
	{
		return _baseEnchantLevel;
	}

	public boolean isCrystallizable()
	{
		if(Config.DISABLE_CRYSTALIZATION_ITEMS)
			return false;
		return isDestroyable() && getGrade() != ItemGrade.NONE && getCrystalCount() > 0;
	}

	public int getCrystalCount()
	{
		return _crystalCount;
	}

	public CrystallizationInfo getCrystallizationInfo()
	{
		if(_crystalCount > 0)
			return CrystallizationDataHolder.getInstance().getCrystallizationInfo(_grade, _crystalCount);
		return null;
	}

	public final int getBodyPart()
	{
		return _bodyPart;
	}

	public boolean isStackable()
	{
		return _stackable;
	}

	public boolean isForHatchling()
	{
		return _petType == PET_EQUIP_TYPE_HATCHLING;
	}

	public boolean isForStrider()
	{
		return _petType == PET_EQUIP_TYPE_STRIDER;
	}

	public boolean isForWolf()
	{
		return _petType == PET_EQUIP_TYPE_WOLF;
	}

	public boolean isForPetBaby()
	{
		return _petType == PET_EQUIP_TYPE_BABY;
	}

	public boolean isForGWolf()
	{
		return _petType == PET_EQUIP_TYPE_GREAT_WOLF;
	}

	public boolean isPetPendant()
	{
		return _petType == PET_EQUIP_TYPE_PENDANT;
	}

	public boolean isForPet()
	{
		return getExType() == ExItemType.PET_EQUIPMENT;
	}

	public void attachSkill(SkillEntry skill)
	{
		_skills = ArrayUtils.add(_skills, skill);
	}

	public SkillEntry[] getAttachedSkills()
	{
		return _skills;
	}

	public SkillEntry getFirstSkill()
	{
		if(_skills.length > 0)
			return _skills[0];
		return null;
	}

	public SkillEntry getEnchant4Skill()
	{
		return _enchant4Skill;
	}

	@Override
	public String toString()
	{
		return _itemId + " " + _name;
	}

	public boolean isShadowItem()
	{
		return _durability > 0 && !isTemporal();
	}

	public final boolean isCommonItem()
	{
		return _quality == ItemQuality.COMMON;
	}

	public final boolean isAltSeed()
	{
		return _type == EtcItemType.ALT_SEED;
	}

	public final boolean isAdena()
	{
		return _itemId == ITEM_ID_ADENA;
	}

	public final boolean isEquipment()
	{
		return _type1 != TYPE1_ITEM_QUESTITEM_ADENA;
	}

	public final boolean isKeyMatherial()
	{
		return _type == EtcItemType.MATERIAL;
	}

	public final boolean isRecipe()
	{
		return _type == EtcItemType.RECIPE;
	}

	public final boolean isRune()
	{
		return _type == EtcItemType.RUNE || _type == EtcItemType.RUNE_SELECT;
	}

	public final boolean isTerritoryAccessory()
	{
		return _itemId >= 13740 && _itemId <= 13748 || _itemId >= 14592 && _itemId <= 14600 || _itemId >= 14664 && _itemId <= 14672 || _itemId >= 14801 && _itemId <= 14809 || _itemId >= 15282 && _itemId <= 15299;
	}

	public final boolean isArrow()
	{
		return _type == EtcItemType.ARROW;
	}

	public final boolean isBolt()
	{
		return _type == EtcItemType.BOLT;
	}

	public final boolean isQuiver()
	{
		return _type == EtcItemType.ARROW_QUIVER || _type == EtcItemType.BOLT_QUIVER;
	}

	public final boolean isBelt()
	{
		return _bodyPart == SLOT_BELT;
	}

	public final boolean isBracelet()
	{
		return _bodyPart == SLOT_R_BRACELET || _bodyPart == SLOT_L_BRACELET;
	}

	public final boolean isUnderwear()
	{
		return _bodyPart == SLOT_UNDERWEAR;
	}

	public final boolean isCloak()
	{
		return _bodyPart == SLOT_BACK;
	}

	public final boolean isTalisman()
	{
		return _bodyPart == SLOT_DECO;
	}

	public final boolean isBrooch()
	{
		return _bodyPart == SLOT_BROOCH;
	}

	public final boolean isJewel()
	{
		return _bodyPart == SLOT_JEWEL;
	}

	public final boolean isHerb()
	{
		return _type == EtcItemType.HERB;
	}

	public final boolean isAttributeCrystal()
	{
		return _type == EtcItemType.ATTRIBUTE_STONE;
	}

	public final boolean isLifeStone()
	{
		return _type == EtcItemType.LIFE_STONE;
	}

	public final boolean isAccessoryLifeStone()
	{
		return _type == EtcItemType.ACC_LIFE_STONE;
	}

	public final boolean isHeroWeapon()
	{
		return org.apache.commons.lang3.ArrayUtils.contains(HERO_WEAPON_IDS, _itemId);
	}

	public boolean isHeroItem()
	{
		return isHeroWeapon() || _itemId == ITEM_ID_HERO_WING || _itemId == ITEM_ID_HERO_CLOAK;
	}

	public boolean isOlympiadItem()
	{
		return isHeroItem() || _itemId == ITEM_ID_FAME_CLOAK;
	}

	public boolean isCursed()
	{
		return CursedWeaponsManager.getInstance().isCursed(_itemId);
	}

	public boolean isTerritoryFlag()
	{
		return _itemId == 13560 || _itemId == 13561 || _itemId == 13562 || _itemId == 13563 || _itemId == 13564 || _itemId == 13565 || _itemId == 13566 || _itemId == 13567 || _itemId == 13568;
	}
	
	public boolean isCrystall()
	{
		return _itemId == 1458 || _itemId == 1459 || _itemId == 1460 || _itemId == 1461 || _itemId == 1462 || _itemId == 17371;
	}

	public boolean isWeapon()
	{
		return getType2() == TYPE2_WEAPON;
	}

	public boolean isArmor()
	{
		return getType2() == TYPE2_SHIELD_ARMOR;
	}

	public boolean isAccessory()
	{
		return getType2() == TYPE2_ACCESSORY;
	}

	public boolean isOther()
	{
		return getType2() == TYPE2_OTHER;
	}

	public boolean isQuest()
	{
		return getType2() == TYPE2_QUEST;
	}

	public boolean isJewelry()
	{
		return getExType() == ExItemType.RING || getExType() == ExItemType.EARRING || getExType() == ExItemType.NECKLACE;
	}

	public boolean isHairAccessory()
	{
		return getExType() == ExItemType.HAIR_ACCESSORY;
	}

	public boolean canBeEnchanted()
	{
		if(isCursed())
			return false;

		return isEnchantable();
	}

	public boolean canBeAppearance()
	{
		if(isCursed())
			return false;

		if(isArmor())
		{
			if(_exType != ExItemType.UPPER_PIECE && 
			   _exType != ExItemType.LOWER_PIECE && 
			   _exType != ExItemType.FULL_BODY && 
			   _exType != ExItemType.GLOVES && 
			   _exType != ExItemType.FEET && 
			   _exType != ExItemType.SHIELD && 
			   _exType != ExItemType.SIGIL)
				return false;
		}
		else if(isAccessory())
		{
			if(_exType != ExItemType.HAIR_ACCESSORY)
				return false;
		}
		else if(isWeapon())
		{
			if(_exType == ExItemType.OTHER_WEAPON)
				return false;
		}

		return isAppearanceable();
	}

	public boolean canBeEnsoul(int ensoulId)
	{
		return false;
	}

	public boolean isEquipable()
	{
		return getBodyPart() > 0 && (getHandler() instanceof EquipableItemHandler);
	}

	public void setEnchant4Skill(SkillEntry enchant4Skill)
	{
		_enchant4Skill = enchant4Skill;
	}

	public boolean testCondition(Playable playable, ItemInstance instance, boolean sendMsg)
	{
		if(_conditions.isEmpty())
			return true;

		SystemMsg msg = getHandler().checkCondition(playable, instance);
		if(msg != null)
		{
			if(sendMsg && playable.isPlayer())
			{
				if(msg.size() > 0)
					playable.sendPacket(new SystemMessagePacket(msg).addItemName(getItemId()));
				else
					playable.sendPacket(msg);
			}
			return false;
		}

		Env env = new Env();
		env.character = playable;
		env.item = instance;

		for(Condition condition : _conditions)
		{
			if(!condition.test(env))
			{
				if(sendMsg && playable.isPlayer() && condition.getSystemMsg() != null)
				{
					if(condition.getSystemMsg().size() > 0)
						playable.sendPacket(new SystemMessagePacket(condition.getSystemMsg()).addItemName(getItemId()));
					else
						playable.sendPacket(condition.getSystemMsg());
				}
				return false;
			}
		}

		return true;
	}

	public boolean isBlocked(Playable playable, ItemInstance instance)
	{
		if(_conditions.isEmpty())
			return false;

		Env env = new Env();
		env.character = playable;
		env.item = instance;

		for(Condition condition : _conditions)
		{
			if(!condition.test(env) && condition.getSystemMsg() == null)
				return true;
		}
		return false;
	}

	public void addCondition(Condition condition)
	{
		_conditions.add(condition);
	}

	public boolean isEnchantable()
	{
		return hasFlag(ItemFlags.ENCHANTABLE);
	}

	public boolean isAugmentable()
	{
		return getVariationGroupId() > 0;
	}

	public boolean isAttributable()
	{
		return hasFlag(ItemFlags.ATTRIBUTABLE);
	}

	public final boolean isTradeable()
	{
		return hasFlag(ItemFlags.TRADEABLE);
	}

	public final boolean isPrivatestoreable()
	{
		return hasFlag(ItemFlags.PRIVATESTOREABLE);
	}

	public final boolean isCommissionable()
	{
		return hasFlag(ItemFlags.COMMISSIONABLE);
	}

	public final boolean isDestroyable()
	{
		return hasFlag(ItemFlags.DESTROYABLE);
	}

	public final boolean isAppearanceable()
	{
		return hasFlag(ItemFlags.APPEARANCEABLE);
	}

	public final boolean isDropable()
	{
		return hasFlag(ItemFlags.DROPABLE);
	}

	public final boolean isSellable()
	{
		return hasFlag(ItemFlags.SELLABLE);
	}

	public final boolean isStoreable()
	{
		return hasFlag(ItemFlags.STOREABLE);
	}

	public final boolean isFreightable()
	{
		return hasFlag(ItemFlags.FREIGHTABLE);
	}

	public final boolean isEnsoulable()
	{
		return hasFlag(ItemFlags.ENSOULABLE);
	}

	public boolean hasFlag(ItemFlags f)
	{
		return (_flags & f.mask()) == f.mask();
	}

	private void activeFlag(ItemFlags f)
	{
		_flags |= f.mask();
	}

	public IItemHandler getHandler()
	{
		return null;
	}

	public int getReuseDelay()
	{
		return _reuseDelay;
	}

	public int getReuseGroup()
	{
		return _reuseGroup;
	}

	public int getDisplayReuseGroup()
	{
		return _reuseGroup < 0 ? -1 : _reuseGroup;
	}

	public int getAgathionEnergy()
	{
		return _agathionEnergy;
	}

	public void addEnchantOptions(int level, int[] options)
	{
		if(_enchantOptions.isEmpty())
			_enchantOptions = new HashIntObjectMap<int[]>();

		_enchantOptions.put(level, options);
	}

	public IntObjectMap<int[]> getEnchantOptions()
	{
		return _enchantOptions;
	}

	public ItemReuseType getReuseType()
	{
		return _reuseType;
	}

	public boolean isMagicWeapon()
	{
		return false;
	}

	public final List<CapsuledItemData> getCapsuledItems()
	{
		return _capsuledItems;
	}

	public void addCapsuledItem(CapsuledItemData ci)
	{
		_capsuledItems.add(ci);
	}

	public ItemQuality getQuality()
	{
		return _quality;
	}

	public boolean isPvP()
	{
		return _isPvP;
	}

	public ExItemType getExType()
	{
		return _exType;
	}
	
	public long getPriceLimitForItem()
	{
		return _priceLimit;
	}

	public int getVariationGroupId()
	{
		return _variationGroupId;
	}

	public WeaponFightType getWeaponFightType()
	{
		return WeaponFightType.WARRIOR;
	}

	@Override
	public void attachFunc(FuncTemplate f)
	{
		if(isForPet())
		{
			super.attachFunc(f);
			return;
		}

		
		if(isWeapon())
		{
			if(f._stat == Stats.POWER_ATTACK && f._func == FuncAdd.class && f._order == 0x10)
			{
				_pAtk = (int) f._value;
				return;
			}
			if(f._stat == Stats.MAGIC_ATTACK && f._func == FuncAdd.class && f._order == 0x10)
			{
				_mAtk = (int) f._value;
				return;
			}
		}
		else if(isArmor())
		{
			switch(_exType)
			{
				case HELMET:
				case UPPER_PIECE:
				case LOWER_PIECE:
				case FULL_BODY:
				case GLOVES:
				case FEET:
				case UNDERWEAR:
				case CLOAK:
					if(f._stat == Stats.POWER_DEFENCE && f._func == FuncAdd.class && f._order == 0x10)
					{
						_pDef = (int) f._value;
						return;
					}
					break;
			}
		}
		else if(isAccessory())
		{
			switch(_exType)
			{
				case RING:
				case EARRING:
				case NECKLACE:
					if(f._stat == Stats.MAGIC_DEFENCE && f._func == FuncAdd.class && f._order == 0x10)
					{
						_mDef = (int) f._value;
						return;
					}
					break;
			}
		}
		super.attachFunc(f);
	}

	public final int getPAtk()
	{
		return _pAtk;
	}

	public final int getMAtk()
	{
		return _mAtk;
	}

	public final int getPDef()
	{
		return _pDef;
	}

	public final int getMDef()
	{
		return _mDef;
	}
}