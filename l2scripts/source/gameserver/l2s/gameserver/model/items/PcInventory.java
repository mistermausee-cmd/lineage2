package l2s.gameserver.model.items;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import l2s.gameserver.model.items.listeners.AccessoryListener;
import l2s.gameserver.model.items.listeners.ArmorSetListener;
import l2s.gameserver.model.items.listeners.BowListener;
import l2s.gameserver.model.items.listeners.ItemAugmentationListener;
import l2s.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2s.gameserver.model.items.listeners.ItemSkillsListener;
import l2s.gameserver.model.items.listeners.JewelsListener;
import l2s.gameserver.model.items.listeners.RodListener;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExAdenaInvenCount;
import l2s.gameserver.network.l2.s2c.ExBR_AgathionEnergyInfoPacket;
import l2s.gameserver.network.l2.s2c.ExUserInfoEquipSlot;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.taskmanager.DelayedItemsManager;
import l2s.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;

public class PcInventory extends Inventory
{
	private final Player _owner;

	
	private LockType _lockType = LockType.NONE;
	private int[] _lockItems = ArrayUtils.EMPTY_INT_ARRAY;

	public PcInventory(Player owner)
	{
		super(owner.getObjectId());
		_owner = owner;

		addListener(ItemSkillsListener.getInstance());
		addListener(ItemAugmentationListener.getInstance());
		addListener(ItemEnchantOptionsListener.getInstance());
		addListener(ArmorSetListener.getInstance());
		addListener(BowListener.getInstance());
		addListener(AccessoryListener.getInstance());
		addListener(RodListener.getInstance());
		addListener(JewelsListener.getInstance());
	}

	@Override
	public Player getActor()
	{
		return _owner;
	}

	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.INVENTORY;
	}

	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PAPERDOLL;
	}

	
	public ItemInstance addAdena(long amount)
	{
		return addItem(ItemTemplate.ITEM_ID_ADENA, amount);
	}

	public boolean reduceAdena(long adena)
	{
		return destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, adena);
	}

	public int getPaperdollVariation1Id(int slot)
	{
		ItemInstance item = _paperdoll[slot];
		if(item != null && item.isAugmented())
			return item.getVariation1Id();
		return 0;
	}

	public int getPaperdollVariation2Id(int slot)
	{
		ItemInstance item = _paperdoll[slot];
		if(item != null && item.isAugmented())
			return item.getVariation2Id();
		return 0;
	}

	@Override
	public int getPaperdollItemId(int slot)
	{
		Player player = getActor();

		int itemId = super.getPaperdollItemId(slot);

		if(slot == PAPERDOLL_RHAND && itemId == 0 && player.isClanAirShipDriver())
			itemId = 13556; 

		return itemId;
	}

	@Override
	public int getPaperdollVisualId(int slot)
	{
		Player player = getActor();

		int itemId = super.getPaperdollVisualId(slot);

		if(player.isInTrainingCamp())
		{
			if(slot == PAPERDOLL_RHAND || slot == PAPERDOLL_LRHAND)
				itemId = 135;
		}
		return itemId;
	}

	@Override
	protected void onRefreshWeight()
	{
		
		getActor().refreshOverloaded();
	}

	
	public void validateItems()
	{
		for(ItemInstance item : _paperdoll)
			if(item != null && (ItemFunctions.checkIfCanEquip(getActor(), item) != null || !item.getTemplate().testCondition(getActor(), item, false)))
			{
				unEquipItem(item);
				getActor().sendDisarmMessage(item);
			}
	}

	
	public void validateItemsSkills()
	{
		for(ItemInstance item : _paperdoll)
		{
			if(item == null || item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON)
				continue;

			boolean needUnequipSkills = getActor().getWeaponsExpertisePenalty() > 0;

			if(item.getTemplate().getAttachedSkills().length > 0)
			{
				boolean has = getActor().getSkillLevel(item.getTemplate().getAttachedSkills()[0].getId()) > 0;
				if(needUnequipSkills && has)
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				else if(!needUnequipSkills && !has)
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
			}
			else if(item.getTemplate().getEnchant4Skill() != null)
			{
				boolean has = getActor().getSkillLevel(item.getTemplate().getEnchant4Skill().getId()) > 0;
				if(needUnequipSkills && has)
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				else if(!needUnequipSkills && !has)
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
			}
			else if(!item.getTemplate().getTriggerList().isEmpty())
			{
				if(needUnequipSkills)
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				else
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
			}
		}
	}

	
	public boolean isRefresh = false;

	public void refreshEquip()
	{
		isRefresh = true;
		for(ItemInstance item : getItems())
		{
			if(item.isEquipped())
			{
				int slot = item.getEquipSlot();
				_listeners.onUnequip(slot, item);
				_listeners.onEquip(slot, item);
			}
			else if(item.getTemplate().isRune())
			{
				_listeners.onUnequip(-1, item);
				_listeners.onEquip(-1, item);
			}
		}
		isRefresh = false;
	}

	
	public void sort(int[][] order)
	{
		boolean needSort = false;
		for(int[] element : order)
		{
			ItemInstance item = getItemByObjectId(element[0]);
			if(item == null)
				continue;
			if(item.getLocation() != ItemLocation.INVENTORY)
				continue;
			if(item.getLocData() == element[1])
				continue;
			item.setLocData(element[1]);
			item.setJdbcState(JdbcEntityState.UPDATED); 
			needSort = true;
		}
		if(needSort)
			Collections.sort(_items, ItemOrderComparator.getInstance());
	}

	public ItemInstance findArrowForBow(ItemTemplate bow)
	{
		ItemInstance res = null;
		for(ItemInstance temp : getItems())
		{
			if(temp.getItemType() == EtcItemType.ARROW || temp.getItemType() == EtcItemType.ARROW_QUIVER)
			{
				if(bow.getGrade().extOrdinal() == temp.getGrade().extOrdinal())
				{
					if(temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == PAPERDOLL_LHAND)
						return temp;
					else if(res == null || temp.getItemId() < res.getItemId())
						res = temp;
				}
			}
		}
		return res;
	}

	public ItemInstance findArrowForCrossbow(ItemTemplate crossbow)
	{
		ItemInstance res = null;
		for(ItemInstance temp : getItems())
		{
			if(temp.getItemType() == EtcItemType.BOLT || temp.getItemType() == EtcItemType.BOLT_QUIVER)
			{
				if(crossbow.getGrade().extOrdinal() == temp.getGrade().extOrdinal())
				{
					if(temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == PAPERDOLL_LHAND)
						return temp;
					else if(res == null || temp.getItemId() < res.getItemId())
						res = temp;
				}
			}
		}
		return res;
	}

	public void lockItems(LockType lock, int[] items)
	{
		if(_lockType != LockType.NONE)
			return;

		_lockType = lock;
		_lockItems = items;

		getActor().sendItemList(false);
	}

	public void unlock()
	{
		if(_lockType == LockType.NONE)
			return;

		_lockType = LockType.NONE;
		_lockItems = ArrayUtils.EMPTY_INT_ARRAY;

		getActor().sendItemList(false);
	}

	public boolean isLockedItem(ItemInstance item)
	{
		switch(_lockType)
		{
			case INCLUDE:
				return ArrayUtils.contains(_lockItems, item.getItemId());
			case EXCLUDE:
				return !ArrayUtils.contains(_lockItems, item.getItemId());
			default:
				return false;
		}
	}

	public LockType getLockType()
	{
		return _lockType;
	}

	public int[] getLockItems()
	{
		return _lockItems;
	}

	@Override
	protected void onRestoreItem(ItemInstance item)
	{
		super.onRestoreItem(item);

		if(item.getTemplate().isRune())
			_listeners.onEquip(-1, item);

		if(!startVisualChangeTask(item) && (item.isTemporalItem() || item.isFlagLifeTime()))
			item.startTimer(new LifeTimeTask(item));
		
		if(item.isCursed())
		      CursedWeaponsManager.getInstance().checkPlayer(getActor(), item, true);
		
		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
		super.onAddItem(item);

		if(item.getTemplate().isRune())
			_listeners.onEquip(-1, item);

		if(item.getTemplate().isArrow() || item.getTemplate().isBolt() || item.getTemplate().isQuiver())
			getActor().checkAndEquipArrows();

		if(item.isTemporalItem() || item.isFlagLifeTime())
			item.startTimer(new LifeTimeTask(item));

		if(item.isCursed())
			CursedWeaponsManager.getInstance().checkPlayer(getActor(), item, false);

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onModifyItem(ItemInstance item)
	{
		super.onModifyItem(item);

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onRemoveItem(ItemInstance item)
	{
		super.onRemoveItem(item);

		Player owner = getActor();
		owner.removeItemFromShortCut(item.getObjectId());

		if(item.getTemplate().isRune())
			_listeners.onUnequip(-1, item);

		if(item.isTemporalItem() || item.isFlagLifeTime())
			item.stopTimer();

		if(owner.getMountControlItemObjId() == item.getObjectId())
			owner.setMount(null);

		if(owner.getPetControlItem() == item)
		{
			PetInstance pet = owner.getPet();
			if(pet != null)
				pet.unSummon(false);
		}

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onEquip(int slot, ItemInstance item)
	{
		super.onEquip(slot, item);

		if(item.isShadowItem())
			item.startTimer(new ShadowLifeTimeTask(item));
	}

	@Override
	protected void onReequip(int slot, ItemInstance newItem, ItemInstance oldItem)
	{
		super.onReequip(slot, newItem, oldItem);

		if(oldItem.isShadowItem())
			oldItem.stopTimer();

		if(newItem.isShadowItem())
			newItem.startTimer(new ShadowLifeTimeTask(newItem));
	}

	@Override
	protected void onUnequip(int slot, ItemInstance item)
	{
		super.onUnequip(slot, item);

		if(item.isShadowItem())
			item.stopTimer();
	}

	@Override
	public void restore()
	{
		final int ownerId = getOwnerId();

		writeLock();
		try
		{
			Collection<ItemInstance> items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getBaseLocation());

			for(ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
			}
			Collections.sort(_items, ItemOrderComparator.getInstance());

			items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getEquipLocation());

			for(ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
				if(item.getEquipSlot() >= PAPERDOLL_MAX)
				{
					
					item.setLocation(getBaseLocation());
					item.setLocData(0); 
					item.setEquipped(false);
					continue;
				}
				setPaperdollItem(item.getEquipSlot(), item);
			}
		}
		finally
		{
			writeUnlock();
		}

		DelayedItemsManager.getInstance().loadDelayed(getActor(), false);

		refreshWeight();
	}

	@Override
	public void store()
	{
		writeLock();
		try
		{
			_itemsDAO.update(_items);
		}
		finally
		{
			writeUnlock();
		}
	}

	@Override
	public void sendAddItem(ItemInstance item)
	{
		Player actor = getActor();

		actor.sendPacket(new InventoryUpdatePacket().addNewItem(actor, item));

			actor.sendPacket(new ExAdenaInvenCount(actor));
		if(item.getTemplate().getAgathionEnergy() > 0)
			actor.sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item)); 
	}

	@Override
	public void sendModifyItem(ItemInstance... items)
	{
		Player actor = getActor();

		InventoryUpdatePacket iu = new InventoryUpdatePacket();
		for(ItemInstance item : items)
			iu.addModifiedItem(actor, item);

		actor.sendPacket(iu);

		for(ItemInstance item : items)
		{

				actor.sendPacket(new ExAdenaInvenCount(actor));
			if(item.getTemplate().getAgathionEnergy() > 0)
				actor.sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item));
		}
	}

	@Override
	public void sendRemoveItem(ItemInstance item)
	{
		Player actor = getActor();
		actor.sendPacket(new InventoryUpdatePacket().addRemovedItem(actor, item));

			actor.sendPacket(new ExAdenaInvenCount(actor));
	}

	@Override
	public void sendEquipInfo(int slot)
	{
		getActor().broadcastUserInfo(true);
		getActor().sendPacket(new ExUserInfoEquipSlot(getActor(), slot));
	}

	public void startTimers()
	{

	}

	public void stopAllTimers()
	{
		for(ItemInstance item : getItems())
		{
			if(item.isShadowItem() || item.isTemporalItem() || item.getVisualId() > 0 && item.getLifeTime() > 0 || item.isFlagLifeTime())
				item.stopTimer();
		}
	}

	protected class ShadowLifeTimeTask extends RunnableImpl
	{
		private ItemInstance item;

		ShadowLifeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			if(!item.isEquipped())
				return;

			int mana;
			synchronized (item)
			{
				item.setLifeTime(item.getLifeTime() - 1);
				mana = item.getShadowLifeTime();
				if(mana <= 0)
					destroyItem(item);
			}

			SystemMessage sm = null;
			if(mana == 10)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_10);
			else if(mana == 5)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_5);
			else if(mana == 1)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON);
			else if(mana <= 0)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
			else
				player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, item));

			if(sm != null)
			{
				sm.addItemName(item.getItemId());
				player.sendPacket(sm);
			}
		}
	}

	protected class LifeTimeTask extends RunnableImpl
	{
		private ItemInstance item;

		LifeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			int left;
			synchronized (item)
			{
				left = item.getTemporalLifeTime();
				if(left <= 0)
					destroyItem(item);
			}

			if(left <= 0)
				player.sendPacket(new SystemMessage(SystemMessage.THE_LIMITED_TIME_ITEM_HAS_BEEN_DELETED).addItemName(item.getItemId()));
		}
	}
	
	public boolean startVisualChangeTask(ItemInstance item)
	{
		if(item.getVisualId() > 0 && item.getLifeTime() > 0)
		{
			item.startTimer(new VisualChangeTimeTask(item));
			return true;
		}
		return false;
	}

	protected class VisualChangeTimeTask extends RunnableImpl
	{
		private ItemInstance item;

		VisualChangeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			int left;
			synchronized (item)
			{
				left = item.getTemporalLifeTime();
				if(left <= 0)
				{
					item.setVisualId(0);
					item.setAppearanceStoneId(0);
					item.setLifeTime(0);
					item.stopTimer();
					item.setJdbcState(JdbcEntityState.UPDATED);
					item.update();
				}
			}

			if(left <= 0)
			{
				if(item.isEquipped())
					sendEquipInfo(item.getEquipSlot());
				sendModifyItem(item);
			}

			
		}
	}
}