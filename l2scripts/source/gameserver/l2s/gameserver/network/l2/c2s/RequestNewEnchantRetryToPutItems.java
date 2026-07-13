package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SynthesisDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantRetryToPutItemFail;
import l2s.gameserver.network.l2.s2c.ExEnchantRetryToPutItemOk;
import l2s.gameserver.templates.item.support.SynthesisData;

public class RequestNewEnchantRetryToPutItems extends L2GameClientPacket
{
	private int _firstItemObjectId;
	private int _secondItemObjectId;
	
	@Override
	protected void readImpl() throws Exception
	{
		_firstItemObjectId = readD();
		_secondItemObjectId = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		else if(activeChar.isInStoreMode())
		{
			activeChar.setSynthesisItem1(null);
			activeChar.setSynthesisItem2(null);
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}
		else if(activeChar.isInTrade() || activeChar.isProcessingRequest())
		{
			activeChar.setSynthesisItem1(null);
			activeChar.setSynthesisItem2(null);
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}

		if(activeChar.isFishing()) 
		{
			activeChar.setSynthesisItem1(null);
			activeChar.setSynthesisItem2(null);
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}

		if(activeChar.isInTrainingCamp())
		{
			activeChar.setSynthesisItem1(null);
			activeChar.setSynthesisItem2(null);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}

		final ItemInstance item1 = activeChar.getInventory().getItemByObjectId(_firstItemObjectId);
		if(item1 == null)
		{
			activeChar.setSynthesisItem1(null);
			activeChar.setSynthesisItem2(null);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}

		final ItemInstance item2 = activeChar.getInventory().getItemByObjectId(_secondItemObjectId);
		if(item1 == item2)
		{
			activeChar.setSynthesisItem1(null);
			activeChar.setSynthesisItem2(null);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}

		SynthesisData data = null;
		for(SynthesisData d : SynthesisDataHolder.getInstance().getDatas())
		{
			if(item2 == null || item2.getItemId() == d.getItem1Id())
			{
				if(item1.getItemId() == d.getItem2Id())
				{
					data = d;
					break;
				}
			}

			if(item2 == null || item2.getItemId() == d.getItem2Id())
			{
				if(item1.getItemId() == d.getItem1Id())
				{
					data = d;
					break;
				}
			}
		}

		if(data == null)
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_VALID_COMBINATION);
			activeChar.sendPacket(ExEnchantRetryToPutItemFail.STATIC);
			return;
		}

		activeChar.setSynthesisItem1(item1);
		activeChar.setSynthesisItem2(item2);
		activeChar.sendPacket(ExEnchantRetryToPutItemOk.STATIC);
	}
}