package l2s.gameserver.network.l2.c2s;

import l2s.commons.math.SafeMath;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.instancemanager.CastleManorManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.manor.SeedProduction;
import l2s.gameserver.utils.NpcUtils;


public class RequestBuySeed extends L2GameClientPacket
{
	private int _count, _manorId;
	private int[] _items;
	private long[] _itemQ;

	@Override
	protected void readImpl()
	{
		_manorId = readD();
		_count = readD();

		if(_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];

		for(int i = 0; i < _count; i++)
		{
			_items[i] = readD();
			_itemQ[i] = readQ();
			if(_itemQ[i] < 1)
			{
				_count = 0;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _count == 0)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(activeChar.isInTrade())
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
	    
		if(!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.isPK() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		NpcInstance manor = NpcUtils.canPassPacket(activeChar, this);
		if(manor == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _manorId);
		if(castle == null)
			return;

		long totalPrice = 0;
		int slots = 0;
		long weight = 0;

		try
		{
			for(int i = 0; i < _count; i++)
			{
				int seedId = _items[i];
				long count = _itemQ[i];
				long price = 0;
				long residual = 0;

				SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
				price = seed.getPrice();
				residual = seed.getCanProduce();

				if(price < 1)
					return;

				if(residual < count)
					return;

				totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(count, price));

				ItemTemplate item = ItemHolder.getInstance().getTemplate(seedId);
				if(item == null)
					return;

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, item.getWeight()));
				if(!item.isStackable() || activeChar.getInventory().getItemByItemId(seedId) == null)
					slots++;
			}

		}
		catch(ArithmeticException ae)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			if(!activeChar.getInventory().validateWeight(weight))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				return;
			}

			if(!activeChar.getInventory().validateCapacity(slots))
			{
				activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				return;
			}

			if(!activeChar.reduceAdena(totalPrice, true))
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			
			castle.addToTreasuryNoTax(totalPrice, false, true);

			
			for(int i = 0; i < _count; i++)
			{
				int seedId = _items[i];
				long count = _itemQ[i];

				
				SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
				seed.setCanProduce(seed.getCanProduce() - count);
				castle.updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);

				
				activeChar.getInventory().addItem(seedId, count);
				activeChar.sendPacket(SystemMessagePacket.obtainItems(seedId, count, 0));
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		activeChar.sendChanges();
	}
}