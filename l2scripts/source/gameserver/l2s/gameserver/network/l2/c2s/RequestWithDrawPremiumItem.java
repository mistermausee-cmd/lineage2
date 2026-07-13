package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.PremiumItem;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExGetPremiumItemListPacket;
import l2s.gameserver.network.l2.s2c.ExNotifyPremiumItem;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.NpcUtils;


public final class RequestWithDrawPremiumItem extends L2GameClientPacket
{
	private int _index;
	private int _charId;
	private long _itemCount;

	@Override
	protected void readImpl()
	{
		_index = readD();
		_charId = readD();
		_itemCount = readQ();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_itemCount <= 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getObjectId() != _charId) 
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getPremiumItemList().isEmpty())
		{
			activeChar.sendActionFailed();
			return;
		}

		

	    if(NpcUtils.canPassPacket(activeChar, this) == null && !activeChar.isGM())
	    {
	    	activeChar.sendActionFailed();
	    	return;
	    }
	    
		if(activeChar.getWeightPenalty() >= 3 || activeChar.getInventoryLimit() * 0.8 <= activeChar.getInventory().getSize())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT);
			return;
		}

		if(activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
			return;
		}

		PremiumItem item = activeChar.getPremiumItemList().get(_index);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getPremiumItemList().remove(item, _itemCount))
		{
			activeChar.sendActionFailed();
			return;
		}

		ItemFunctions.addItem(activeChar, item.getItemId(), this._itemCount);

		if(activeChar.getPremiumItemList().isEmpty())
			activeChar.sendPacket(SystemMsg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
		else
		{
			activeChar.sendPacket(new ExGetPremiumItemListPacket(activeChar));
			activeChar.sendPacket(ExNotifyPremiumItem.STATIC);
		}
	}
}