package l2s.gameserver.network.l2.c2s;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.RecipeItemMakeInfoPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2s.gameserver.templates.item.RecipeTemplate;
import l2s.gameserver.templates.item.data.ChancedItemData;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;

public class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
	private int _recipeId;
	private TIntLongMap _items;

	
	@Override
	protected void readImpl()
	{
		_recipeId = readD();
		int count = readD();
		_items = new TIntLongHashMap(count);
		for(int i = 0; i < count; i++)
			_items.put(readD(), readQ());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isProcessingRequest())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if(activeChar.isInTrainingCamp())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(_recipeId);

		if(recipe == null || recipe.getMaterials().length == 0)
		{
			activeChar.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
			return;
		}

	    if(recipe.isAwakedOnly() && !activeChar.getClassId().isAwaked())
	    {
	    	activeChar.sendActionFailed();
	    	return;
	    }
	    
		if(recipe.getLevel() > activeChar.getSkillLevel(!recipe.isCommon() ? 172 : 1320))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getCurrentMp() < recipe.getMpConsume())
		{
			activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeItemMakeInfoPacket(activeChar, recipe, 0));
			return;
		}

		if(!activeChar.findRecipe(_recipeId))
		{
			activeChar.sendPacket(SystemMsg.PLEASE_REGISTER_A_RECIPE, ActionFailPacket.STATIC);
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			ItemData[] materials = recipe.getMaterials();

			for(ItemData material : materials)
			{
				if(material.getCount() == 0)
					continue;

				if(Config.ALT_GAME_UNREGISTER_RECIPE && ItemHolder.getInstance().getTemplate(material.getId()).getItemType() == EtcItemType.RECIPE)
				{
					RecipeTemplate rp = RecipeHolder.getInstance().getRecipeByRecipeItem(material.getId());
					if(activeChar.hasRecipe(rp))
						continue;
					activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfoPacket(activeChar, recipe, 0));
					return;
				}

				ItemInstance item = activeChar.getInventory().getItemByItemId(material.getId());
				if(item == null || item.getCount() < material.getCount())
				{
					activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfoPacket(activeChar, recipe, 0));
					return;
				}
			}

			for(ItemData material : materials)
			{
				if(material.getCount() == 0)
					continue;

				if(Config.ALT_GAME_UNREGISTER_RECIPE && ItemHolder.getInstance().getTemplate(material.getId()).getItemType() == EtcItemType.RECIPE)
					activeChar.unregisterRecipe(RecipeHolder.getInstance().getRecipeByRecipeItem(material.getId()).getId());
				else
				{
					if(!activeChar.getInventory().destroyItemByItemId(material.getId(), material.getCount()))
						continue;
					activeChar.sendPacket(SystemMessagePacket.removeItems(material.getId(), material.getCount()));
				}
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		activeChar.resetWaitSitTime();
		activeChar.reduceCurrentMp(recipe.getMpConsume(), null);
		ChancedItemData product = recipe.getRandomProduct();
		int itemId = product.getId();
		long itemsCount = product.getCount();
		
	    if(Formulas.calcCraftingMastery(activeChar))
	      itemsCount *= 2L;
	    
		double rate = recipe.getSuccessRate();
		
		for(TIntLongIterator iterator = _items.iterator(); iterator.hasNext(); )
		{
			iterator.advance();
			ItemInstance item = activeChar.getInventory().getItemByObjectId(iterator.key());
			if(item == null || item.getReferencePrice() <= 0 || !item.canBeDestroyed(activeChar))
				continue; 
			long adenaSum = item.getReferencePrice() * iterator.value();
			int addRate = (int)((adenaSum >= recipe.getAddRateAdena() ? 1.0D : adenaSum / recipe.getAddRateAdena()) * (100 - recipe.getSuccessRate()));
			if(addRate <= 0)
		        continue; 
			Log.LogItem(activeChar, "Craft AddAdenaRate " + addRate + " %", item, item.getItemId());
			if(ItemFunctions.deleteItem(activeChar, item.getItemId(), iterator.value()))
		        rate += addRate; 
		}
		
		rate += activeChar.getPremiumAccount().getBonus().getCraftChance();
		rate = Math.min(100, rate);

		int success = 0;

		if(product != null)
		{
			boolean lucky = Formulas.tryLuck(activeChar);
			
			if(lucky || Rnd.chance(rate))
			{
				
				ItemFunctions.addItem(activeChar, itemId, itemsCount, true);
				success = 1;
				if(lucky)
			        activeChar.onSuccessLucky();
			}
			else
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_FAILED_TO_MANUFACTURE_S1).addItemName(itemId));
		}



		activeChar.sendPacket(new RecipeItemMakeInfoPacket(activeChar, recipe, success));
	}
}