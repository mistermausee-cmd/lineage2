package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.CommissionItem;


public class ExResponseCommissionBuyInfo extends L2GameServerPacket
{
	private final CommissionItem _item;

	public ExResponseCommissionBuyInfo(CommissionItem item)
	{
		_item = item;
	}

	protected void writeImpl()
	{
		if(_item != null)
		{
			writeD(0x01); 
			writeQ(_item.getCommissionPrice()); 
			writeQ(_item.getCommissionId()); 
			writeD(_item.getItem().getExType().ordinal()); 
			writeItemInfo(_item);
		}
		else
			writeD(0x00); 
			
	}
}