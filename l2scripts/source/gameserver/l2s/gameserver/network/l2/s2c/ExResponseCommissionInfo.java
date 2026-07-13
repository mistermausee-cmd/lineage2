package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInstance;

public class ExResponseCommissionInfo extends L2GameServerPacket
{
	private ItemInstance _item;

	public ExResponseCommissionInfo(ItemInstance item)
	{
		_item = item;
	}

	protected void writeImpl()
	{
		writeD(_item.getItemId()); 
		writeD(_item.getObjectId());
		writeQ(_item.getCount()); 
		writeQ(0);
		writeD(0); 
	}
}