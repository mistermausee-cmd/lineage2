package l2s.gameserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EnchantStoneHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import l2s.gameserver.model.items.attachment.PickableAttachment;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2s.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.item.support.EnchantStone;

public final class ItemFunctions
{
	private ItemFunctions()
	{}

	public static ItemInstance createItem(int itemId)
	{
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		item.setLocation(ItemLocation.VOID);
		item.setCount(1L);

		return item;
	}

	
	public static List<ItemInstance> addItem(Playable playable, int itemId, long count)
	{
		return addItem(playable, itemId, count, 0, true);
	}

	public static List<ItemInstance> addItem(Playable playable, int itemId, long count, boolean notify)
	{
		return addItem(playable, itemId, count, 0, notify);
	}

	
	public static List<ItemInstance> addItem(Playable playable, int itemId, long count, int enchantLevel, boolean notify)
	{
		if(playable == null || count < 1)
			return Collections.emptyList();

		Playable player;
		if(playable.isSummon())
			player = playable.getPlayer();
		else
			player = playable;

		if(itemId > 0)
		{
			List<ItemInstance> items = new ArrayList<ItemInstance>();

			ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
			if(t.isStackable())
			{
				items.add(player.getInventory().addItem(itemId, count));

				if(notify)
					player.sendPacket(SystemMessagePacket.obtainItems(itemId, count, 0));
			}
			else
			{
				for(long i = 0; i < count; i++)
				{
					ItemInstance item = player.getInventory().addItem(itemId, 1, enchantLevel);
					items.add(item);

					if(notify)
						player.sendPacket(SystemMessagePacket.obtainItems(item));
				}
			}

			return items;
		}
		else if(itemId == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
		{
			player.getPlayer().addPcBangPoints((int) count, false, notify);
		}
		else if(itemId == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
		{
			if(player.getPlayer().getClan() != null)
			{
				player.getPlayer().getClan().incReputation((int) count, false, "itemFunction");
				if(notify)
				{
					
				}
			}
		}
		else if(itemId == ItemTemplate.ITEM_ID_FAME)
		{
			player.getPlayer().setFame((int) count + player.getPlayer().getFame(), "itemFunction", notify);
		}
		else if(itemId == ItemTemplate.ITEM_ID_RAID_POINTS)
		{
			player.getPlayer().addRaidPoints((int) count, notify);
		}
		return Collections.emptyList();
	}

	
	public static long getItemCount(Playable playable, int itemId)
	{
		if(playable == null)
			return 0;
		Playable player = playable.getPlayer();
		return player.getInventory().getCountOf(itemId);
	}

	
	public static boolean haveItem(Playable playable, int itemId, long count)
	{
		return getItemCount(playable, itemId) >= count;
	}

	
	public static boolean deleteItem(Playable playable, int itemId, long count)
	{
		return deleteItem(playable, itemId, count, true);
	}

	
	public static boolean deleteItem(Playable playable, int itemId, long count, boolean notify)
	{
		if(playable == null || count < 1)
			return false;

		Player player = playable.getPlayer();

		if(itemId > 0)
		{
			player.getInventory().writeLock();
			try
			{
				ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
				if(t == null)
					return false;
				if(t.isStackable())
				{
					if(!player.getInventory().destroyItemByItemId(itemId, count))
						return false;
				}
				else
				{
					if(player.getInventory().getCountOf(itemId) < count)
						return false;

					for(long i = 0; i < count; i++)
						if(!player.getInventory().destroyItemByItemId(itemId, 1L))
							return false;
				}
			}
			finally
			{
				player.getInventory().writeUnlock();
			}

			if(notify)
				player.sendPacket(SystemMessagePacket.removeItems(itemId, count));
		}
		else if(itemId == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
			player.addPcBangPoints((int) count, false, notify);
		else if(itemId == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
		{
			Clan clan = player.getClan();
			if(clan == null)
				return false;

			if(clan.getReputationScore() < count)
				return false;

			clan.incReputation((int) -count, false, "itemFunction");

			if(notify)
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLANS_REPUTATION).addLong(count));
		}
		else if(itemId == ItemTemplate.ITEM_ID_FAME)
		{
			if(player.getPlayer().getFame() < count)
				return false;

			player.getPlayer().setFame((int) (player.getPlayer().getFame() - count), "itemFunction", notify);
		}
		else if(itemId == ItemTemplate.ITEM_ID_RAID_POINTS)
	    {
		    if(player.getPlayer().getRaidPoints() < count)
		        return false;
		    
		      player.getPlayer().reduceRaidPoints((int)count, notify);
		}

		return true;
	}

	
	public static void deleteItemsEverywhere(Playable playable, int itemId)
	{
		if(playable == null)
			return;

		Player player = playable.getPlayer();

		if(itemId > 0)
		{
			player.getInventory().writeLock();
			try
			{
				ItemInstance item = player.getInventory().getItemByItemId(itemId);
				while(item != null)
				{
					player.getInventory().destroyItem(item);
					item = player.getInventory().getItemByItemId(itemId);
				}
			}
			finally
			{
				player.getInventory().writeUnlock();
			}

			player.getWarehouse().writeLock();
			try
			{
				ItemInstance item = player.getWarehouse().getItemByItemId(itemId);
				while(item != null)
				{
					player.getWarehouse().destroyItem(item);
					item = player.getWarehouse().getItemByItemId(itemId);
				}
			}
			finally
			{
				player.getWarehouse().writeUnlock();
			}

			player.getFreight().writeLock();
			try
			{
				ItemInstance item = player.getFreight().getItemByItemId(itemId);
				while(item != null)
				{
					player.getFreight().destroyItem(item);
					item = player.getFreight().getItemByItemId(itemId);
				}
			}
			finally
			{
				player.getFreight().writeUnlock();
			}

			player.getRefund().writeLock();
			try
			{
				ItemInstance item = player.getRefund().getItemByItemId(itemId);
				while(item != null)
				{
					player.getRefund().destroyItem(item);
					item = player.getRefund().getItemByItemId(itemId);
				}
			}
			finally
			{
				player.getRefund().writeUnlock();
			}
		}
	}

	
	public static boolean deleteItem(Playable playable, ItemInstance item, long count)
	{
		return deleteItem(playable, item, count, true);
	}

	
	public static boolean deleteItem(Playable playable, ItemInstance item, long count, boolean notify)
	{
		if(playable == null || count < 1)
			return false;

		if(item.getCount() < count)
			return false;

		Player player = playable.getPlayer();

		player.getInventory().writeLock();
		try
		{
			if(!player.getInventory().destroyItem(item, count))
				return false;
		}
		finally
		{
			player.getInventory().writeUnlock();
		}

		if(notify)
			player.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), count));

		return true;
	}

	public final static boolean isClanApellaItem(int itemId)
	{
		return itemId >= 7860 && itemId <= 7879 || itemId >= 9830 && itemId <= 9839;
	}

	public final static IBroadcastPacket checkIfCanEquip(PetInstance pet, ItemInstance item)
	{
		if(!item.isEquipable())
			return SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM;

		int petId = pet.getNpcId();

		if(item.getTemplate().isPetPendant() 
				|| PetDataHolder.isWolf(petId) && item.getTemplate().isForWolf() 
				|| PetDataHolder.isHatchling(petId) && item.getTemplate().isForHatchling() 
				|| PetDataHolder.isStrider(petId) && item.getTemplate().isForStrider() 
				|| PetDataHolder.isGreatWolf(petId) && item.getTemplate().isForGWolf() 
				|| PetDataHolder.isBabyPet(petId) && item.getTemplate().isForPetBaby() 
				|| PetDataHolder.isImprovedBabyPet(petId) && item.getTemplate().isForPetBaby() 
		)
			return null;

		return SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM;
	}

	
	public final static IBroadcastPacket checkIfCanEquip(Player player, ItemInstance item)
	{
		
		int itemId = item.getItemId();
		int targetSlot = item.getTemplate().getBodyPart();
		Clan clan = player.getClan();

		
		if(player.getRace() != Race.KAMAEL && (item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.RAPIER || item.getItemType() == WeaponType.ANCIENTSWORD))
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		
		if(player.getClassId().getRace() == Race.ERTHEIA && player.getClassId().isOfType(ClassType.MYSTIC) && (item.getItemType() == ArmorType.SIGIL || item.getItemType() == WeaponType.NONE))
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		if(itemId >= 7850 && itemId <= 7859 && player.getLvlJoinedAcademy() == 0) 
			return SystemMsg.THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY;

		if(isClanApellaItem(itemId) && player.getPledgeRank().ordinal() < PledgeRank.WISEMAN.ordinal())
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		if(item.getItemType() == WeaponType.DUALDAGGER && player.getSkillLevel(923) < 1 && player.getSkillLevel(10502) < 1)
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		
		if(itemId == 6841 && (clan == null || !player.isClanLeader() || clan.getCastle() == 0))
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		
		if(targetSlot == ItemTemplate.SLOT_LR_HAND || targetSlot == ItemTemplate.SLOT_L_HAND || targetSlot == ItemTemplate.SLOT_R_HAND)
		{
			if(itemId != player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) && CursedWeaponsManager.getInstance().isCursed(player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND)))
				return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
			if(player.isCursedWeaponEquipped() && itemId != player.getCursedWeaponEquippedId())
				return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
		}

		if(targetSlot == ItemTemplate.SLOT_DECO)
		{
			int count = player.getTalismanCount();
			if(count <= 0)
				return new SystemMessagePacket(SystemMsg.YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET).addItemName(itemId);

			ItemInstance deco;
			for(int slot = Inventory.PAPERDOLL_DECO1; slot <= Inventory.PAPERDOLL_DECO6; slot++)
			{
				deco = player.getInventory().getPaperdollItem(slot);
				if(deco != null)
				{
					if(deco == item)
						return null; 
					
					if(--count <= 0 || deco.getItemId() == itemId)
						return new SystemMessagePacket(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId);
				}
			}
		}

		if(targetSlot == ItemTemplate.SLOT_JEWEL)
		{
			int count = player.getJewelsLimit();
			if(count <= 0)
				return new SystemMessagePacket(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId); 

			ItemInstance jewel;
			for(int slot = Inventory.PAPERDOLL_JEWEL1; slot <= Inventory.PAPERDOLL_JEWEL6; slot++)
			{
				jewel = player.getInventory().getPaperdollItem(slot);
				if(jewel != null)
				{
					if(jewel == item)
						return null; 
					
					if(--count <= 0 || jewel.getItemId() == itemId)
						return new SystemMessagePacket(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId); 
				}
			}
		}
		return null;
	}

	public static boolean checkIfCanPickup(Playable playable, ItemInstance item)
	{
		Player player = playable.getPlayer();
		return item.getDropTimeOwner() <= System.currentTimeMillis() || item.getDropPlayers().contains(player.getObjectId());
	}

	public static boolean canAddItem(Player player, ItemInstance item)
	{
		if(!player.getInventory().validateWeight(item))
		{
			player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return false;
		}

		if(!player.getInventory().validateCapacity(item))
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return false;
		}

		IItemHandler handler = item.getTemplate().getHandler();
		if(handler != null && !handler.pickupItem(player, item))
			return false;

		PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;
		if(attachment != null && !attachment.canPickUp(player))
			return false;

		return true;
	}

	
	public final static boolean checkIfCanDiscard(Player player, ItemInstance item)
	{
		if(item.isHeroItem())
			return false;

		if(player.getMountControlItemObjId() == item.getObjectId())
			return false;

		if(player.getPetControlItem() == item)
			return false;

		if(player.getEnchantScroll() == item)
			return false;

		if(item.isCursed())
			return false;

		if(item.getTemplate().isQuest())
			return false;

		return true;
	}

	
	public static final EnchantStone getEnchantStone(ItemInstance item, ItemInstance catalyst)
	{
		if(item == null || catalyst == null)
			return null;

		EnchantStone enchantStone = EnchantStoneHolder.getInstance().getEnchantStone(catalyst.getItemId());
		if(enchantStone == null)
			return null;

		int current = item.getEnchantLevel();
		if(current < (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? enchantStone.getMinFullbodyEnchantLevel() : enchantStone.getMinEnchantLevel()))
			return null;

		if(current > enchantStone.getMaxEnchantLevel())
			return null;

		if(!enchantStone.containsGrade(item.getGrade()))
			return null;

		final int itemType = item.getTemplate().getType2();
		switch(enchantStone.getType())
		{
			case ARMOR:
				if(itemType == ItemTemplate.TYPE2_WEAPON || item.getTemplate().isHairAccessory())
					return null;
				break;
			case WEAPON:
				if(itemType == ItemTemplate.TYPE2_SHIELD_ARMOR || itemType == ItemTemplate.TYPE2_ACCESSORY || item.getTemplate().isHairAccessory())
					return null;
				break;
			case HAIR_ACCESSORY:
				if(!item.getTemplate().isHairAccessory())
					return null;
				break;
		}

		return enchantStone;
	}

	public static int getCrystallizeCrystalAdd(ItemInstance item)
	{
		int result = 0;
		int crystalsAdd = 0;
		if(item.isWeapon())
		{
			switch(item.getGrade())
			{
				case D:
					crystalsAdd = 90;
					break;
				case C:
					crystalsAdd = 45;
					break;
				case B:
					crystalsAdd = 67;
					break;
				case A:
					crystalsAdd = 145;
					break;
				case S:
				case S80:
				case S84:
					crystalsAdd = 250;
					break;
				case R:
				case R95:
				case R99:
					crystalsAdd = 500;
					break;
			}
		}
		else
		{
			switch(item.getGrade())
			{
				case D:
					crystalsAdd = 11;
					break;
				case C:
					crystalsAdd = 6;
					break;
				case B:
					crystalsAdd = 11;
					break;
				case A:
					crystalsAdd = 20;
					break;
				case S:
				case S80:
				case S84:
					crystalsAdd = 25;
					break;
				case R:
				case R95:
				case R99:
					crystalsAdd = 30;
					break;
			}
		}

		if(item.getEnchantLevel() > 3)
		{
			result = crystalsAdd * 3;
			if(item.isWeapon())
				crystalsAdd *= 2;
			else
				crystalsAdd *= 3;

			result += crystalsAdd * (item.getEnchantLevel() - 3);
		}
		else
			result = crystalsAdd * item.getEnchantLevel();

		return result;
	}

	public static boolean checkIsEquipped(Player player, int slot, int itemId, int enchant)
	{
		Inventory inv = player.getInventory();
		if(slot >= 0)
		{
			ItemInstance item = inv.getPaperdollItem(slot);
			if(item == null)
				return itemId == 0;

			return item.getItemId() == itemId && item.getFixedEnchantLevel(player) >= enchant;
		}
		else
		{
			for(int s : Inventory.PAPERDOLL_ORDER)
			{
				ItemInstance item = inv.getPaperdollItem(s);
				if(item == null)
					continue;

				if(item.getItemId() == itemId && item.getFixedEnchantLevel(player) >= enchant)
					return true;
			}
		}
		return false;
	}

	public static boolean checkForceUseItem(Player player, ItemInstance item, boolean sendMsg)
	{
		if(player.isOutOfControl())
		{
			if(sendMsg)
				player.sendActionFailed();
			return false;
		}

		if(player.isStunned() || player.isDecontrolled() || player.isSleeping() || player.isAfraid() || player.isAlikeDead())
		{
			if(sendMsg)
				player.sendActionFailed();
			return false;
		}

		if(item.getTemplate().isQuest())
		{
			if(sendMsg)
				player.sendPacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS);
			return false;
		}
		return true;
	}

	public static boolean checkUseItem(Player player, ItemInstance item, boolean sendMsg)
	{
		if(player.isInTrainingCamp())
			return false;

		if(player.isInStoreMode())
		{
			if(sendMsg)
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP);
			return false;
		}

		int itemId = item.getItemId();
		if(player.isFishing() && item.getTemplate().getItemType() != EtcItemType.FISHSHOT)
		{
			if(sendMsg)
				player.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return false;
		}

		if(player.isSharedGroupDisabled(item.getTemplate().getReuseGroup()))
		{
			if(sendMsg)
				player.sendReuseMessage(item);
			return false;
		}

		if(!item.isEquipped() && !item.getTemplate().testCondition(player, item, sendMsg))
			return false;

		if(player.getInventory().isLockedItem(item))
			return false;

		for(Event e : player.getEvents())
		{
			IBroadcastPacket result = e.canUseItem(player, item);
			if(result != null)
			{
				if(sendMsg)
					player.sendPacket(result);
				return false;
			}
		}

		if(item.getTemplate().isForPet())
		{
			if(sendMsg)
				player.sendPacket(SystemMsg.YOU_MAY_NOT_EQUIP_A_PET_ITEM);
			return false;
		}
		
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && player.isMageClass() && item.getItemId() == 10311)
	    {
	      if(sendMsg)
	        player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
	      return false;
	    }
		
	    if(Config.ALT_IMPROVED_PETS_LIMITED_USE && !player.isMageClass() && item.getItemId() == 10313)
	    {
	      if(sendMsg)
	        player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
	      return false;
	    }
	    
		if(player.isUseItemDisabled())
		{
			if(sendMsg)
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}

		if(player.isOutOfControl())
		{
			if(sendMsg)
				player.sendActionFailed();
			return false;
		}
		return true;
	}

	public static boolean useItem(Player player, ItemInstance item, boolean ctrl, boolean sendMsg)
	{
		if(!checkForceUseItem(player, item, sendMsg))
			return false;

		if(player.useItem(item, ctrl, true))
			return true;

		if(!checkUseItem(player, item, sendMsg))
			return false;

		return player.useItem(item, ctrl, false);
	}
}