package l2s.gameserver.network.l2.c2s;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.AppearanceStoneHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExPeriodicItemList;
import l2s.gameserver.network.l2.s2c.ExShape_Shifting_Result;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ExItemType;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.support.AppearanceStone;
import l2s.gameserver.templates.item.support.AppearanceStone.ShapeTargetType;
import l2s.gameserver.templates.item.support.AppearanceStone.ShapeType;


public class RequestShapeShiftingItem extends L2GameClientPacket
{
	private int _targetItemObjId;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.isActionsDisabled() || player.isInStoreMode() || player.isInTrade())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		PcInventory inventory = player.getInventory();
		ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		ItemInstance stone = player.getAppearanceStone();
		if(targetItem == null || stone == null)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(stone.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(!targetItem.canBeAppearance())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}
		 
	    if((targetItem.getCustomFlags() & ItemInstance.FLAG_NO_SHAPE_SHIFTING) == ItemInstance.FLAG_NO_SHAPE_SHIFTING)
	    {
	    	player.sendPacket(ExShape_Shifting_Result.FAIL);
	    	player.setAppearanceStone(null);
	    	player.setAppearanceExtractItem(null);
	    	return;
	    }
	    
		if(targetItem.getLocation() != ItemInstance.ItemLocation.INVENTORY && targetItem.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		AppearanceStone appearanceStone = AppearanceStoneHolder.getInstance().getAppearanceStone(stone.getItemId());
		if(appearanceStone == null)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(appearanceStone.getType() != ShapeType.RESTORE && targetItem.getVisualId() > 0 || appearanceStone.getType() == ShapeType.RESTORE && targetItem.getVisualId() == 0)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(!targetItem.getTemplate().isHairAccessory() && targetItem.getGrade() == ItemGrade.NONE)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		ItemGrade[] stoneGrades = appearanceStone.getGrades();
		if(stoneGrades != null && stoneGrades.length > 0)
		{
			if(!ArrayUtils.contains(stoneGrades, targetItem.getGrade()))
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}
		}

		ShapeTargetType[] targetTypes = appearanceStone.getTargetTypes();
		if(targetTypes == null || targetTypes.length == 0)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(!ArrayUtils.contains(targetTypes, ShapeTargetType.ALL))
		{
			if(targetItem.isWeapon())
			{
				if(!ArrayUtils.contains(targetTypes, ShapeTargetType.WEAPON))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}
			else if(targetItem.isArmor())
			{
				if(!ArrayUtils.contains(targetTypes, ShapeTargetType.ARMOR))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}
			else
			{
				if(!ArrayUtils.contains(targetTypes, ShapeTargetType.ACCESSORY))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}
		}

		ExItemType[] itemTypes = appearanceStone.getItemTypes();
		if(itemTypes != null && itemTypes.length > 0)
		{
			if(!ArrayUtils.contains(itemTypes, targetItem.getExType()))
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}
		}

		ItemInstance extracItem = player.getAppearanceExtractItem();
		int extracItemId = 0;
		if(appearanceStone.getType() != ShapeType.RESTORE && appearanceStone.getType() != ShapeType.FIXED)
		{
			if(extracItem == null)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(!extracItem.canBeAppearance())
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(extracItem.getLocation() != ItemInstance.ItemLocation.INVENTORY && extracItem.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			

			if(!extracItem.getTemplate().isHairAccessory() && targetItem.getGrade().ordinal() < extracItem.getGrade().ordinal())
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(extracItem.getVisualId() > 0)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(targetItem.getExType() != extracItem.getExType())
			{
				if(targetItem.getExType() != ExItemType.UPPER_PIECE && extracItem.getExType() != ExItemType.FULL_BODY)
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}

			if(targetItem.isWeapon())
			{
		        if(targetItem.getTemplate().getItemType() != extracItem.getTemplate().getItemType())
		        {
		        	player.sendPacket(ExShape_Shifting_Result.FAIL);
		        	player.setAppearanceStone(null);
		        	player.setAppearanceExtractItem(null);
		        	return;
		        }
			}

			
			if(extracItem.getOwnerId() != player.getObjectId())
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}
			extracItemId = extracItem.getItemId();
		}

	    if(Config.APPEARANCE_STONE_CHECK_ARMOR_TYPE)
	    {
	    	if(targetItem.isArmor() && (targetItem.getBodyPart() == ItemTemplate.SLOT_CHEST || targetItem.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR || targetItem.getBodyPart() == ItemTemplate.SLOT_LEGS))
	    	{
	    		if(extracItem != null)
	    		{
	    			if(extracItem.getTemplate().isArmor() && (extracItem.getTemplate().getBodyPart() == ItemTemplate.SLOT_CHEST || extracItem.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR || extracItem.getTemplate().getBodyPart() == ItemTemplate.SLOT_LEGS))
	    			{
	    				if(targetItem.getTemplate().getItemType() != extracItem.getTemplate().getItemType())
	    				{
	    					player.sendPacket(ExShape_Shifting_Result.FAIL);
	    					player.setAppearanceStone(null);
	    					player.setAppearanceExtractItem(null);
	    					return;
	    				}
	    			}
	    		}
	    		else if(appearanceStone.getType() == AppearanceStone.ShapeType.FIXED && appearanceStone.getExtractItemId() > 0)
	    		{
	    			ItemTemplate extracItemTemplate = ItemHolder.getInstance().getTemplate(appearanceStone.getExtractItemId());
	    			if(extracItemTemplate != null && extracItemTemplate.isArmor() && (extracItemTemplate.getBodyPart() == ItemTemplate.SLOT_CHEST || extracItemTemplate.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR || extracItemTemplate.getBodyPart() == ItemTemplate.SLOT_LEGS))
	    			{
	    				if(targetItem.getTemplate().getItemType() != extracItemTemplate.getItemType())
	    				{
	    					player.sendPacket(ExShape_Shifting_Result.FAIL);
	    					player.setAppearanceStone(null);
	    					player.setAppearanceExtractItem(null);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    }

		
		if(targetItem.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		inventory.writeLock();
		try
		{
			long cost = appearanceStone.getCost();
			if(cost > player.getAdena())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(stone.getCount() < 1L)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(appearanceStone.getType() == ShapeType.NORMAL)
			{
				if(!inventory.destroyItem(extracItem, 1))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}			
			}
			
			inventory.destroyItem(stone, 1L);
			player.reduceAdena(cost);

			boolean equipped = targetItem.isEquipped();
			if(equipped)
			{
		        inventory.isRefresh = true;
		        inventory.unEquipItem(targetItem);
			}

			switch(appearanceStone.getType())
			{
				case RESTORE:
					if(appearanceStone.getPeriod() > 0)
					{
						targetItem.setLifeTime(0);
						targetItem.stopTimer();
						player.sendPacket(new ExPeriodicItemList(1, targetItem.getObjectId(), 0));
					}
					targetItem.setVisualId(0);
					targetItem.setAppearanceStoneId(0);
					break;
				case NORMAL:
				case BLESSED:
				case FIXED:
					targetItem.setVisualId(appearanceStone.getType() == ShapeType.FIXED ? appearanceStone.getExtractItemId() : extracItem.getItemId());
					targetItem.setAppearanceStoneId(appearanceStone.getItemId());
					if(appearanceStone.getPeriod() > 0)
					{
						targetItem.setLifeTime((int) (System.currentTimeMillis() / 1000L) + appearanceStone.getPeriod());
						inventory.startVisualChangeTask(targetItem);
						player.sendPacket(new ExPeriodicItemList(1, targetItem.getObjectId(), appearanceStone.getPeriod()));
					}
					break;
			}

			targetItem.setJdbcState(JdbcEntityState.UPDATED);
			targetItem.update();

			if(equipped)
			{
				inventory.equipItem(targetItem);
				inventory.isRefresh = false;
			}
			

			player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, targetItem));
			player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_SPENT_S1_ON_A_SUCCESSFUL_APPEARANCE_MODIFICATION).addLong(cost));
		}
		finally
		{
			inventory.writeUnlock();
		}

		player.sendPacket(new ExShape_Shifting_Result(ExShape_Shifting_Result.SUCCESS_RESULT, targetItem.getItemId(), extracItemId, appearanceStone.getPeriod()));

		player.setAppearanceStone(null);
		player.setAppearanceExtractItem(null);
	}
}