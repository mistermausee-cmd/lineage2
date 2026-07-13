package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;



public class EquipUpdate extends L2GameServerPacket
{
	private ItemInfo _item;

	public EquipUpdate(ItemInstance item, int change)
	{
		_item = new ItemInfo(item);
		_item.setLastChange(change);
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_item.getLastChange());
		writeD(_item.getObjectId());
		writeD(_item.getEquipSlot());
	}
}