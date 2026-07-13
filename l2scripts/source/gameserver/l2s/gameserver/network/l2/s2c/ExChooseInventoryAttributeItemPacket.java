package l2s.gameserver.network.l2.s2c;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.support.AttributeStone;


public class ExChooseInventoryAttributeItemPacket extends L2GameServerPacket
{
	private final TIntSet _attributableItems = new TIntHashSet();
	private final int _itemId;
	private final boolean _isFireStone;
	private final boolean _isWaterStone;
	private final boolean _isWindStone;
	private final boolean _isEarthStone;
	private final boolean _isHolyStone;
	private final boolean _isDarkStone;
	private final int _stoneLvl;
	private final long _stonesCount;

	public ExChooseInventoryAttributeItemPacket(Player player, AttributeStone stone, long stonesCount)
	{
		ItemInstance[] items = player.getInventory().getItems();
		for(ItemInstance i : items)
		{
			if(stone.getItemType() != null && stone.getItemType() != i.getTemplate().getQuality())
				continue;

			
			if(i.canBeAttributed())
				_attributableItems.add(i.getObjectId());
		}

		_itemId = stone.getItemId();
		_stoneLvl = stone.getStoneLevel();

		Element stoneElement = stone.getElement(true);
		_isFireStone = stoneElement == Element.FIRE;
		_isWaterStone = stoneElement == Element.WATER;
		_isWindStone = stoneElement == Element.WIND;
		_isEarthStone = stoneElement == Element.EARTH;
		_isHolyStone = stoneElement == Element.HOLY;
		_isDarkStone = stoneElement == Element.UNHOLY;

		_stonesCount = stonesCount;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_itemId);
		writeQ(_stonesCount); 
		writeD(_isFireStone ? 1 : 0); 
		writeD(_isWaterStone ? 1 : 0); 
		writeD(_isWindStone ? 1 : 0); 
		writeD(_isEarthStone ? 1 : 0); 
		writeD(_isHolyStone ? 1 : 0); 
		writeD(_isDarkStone ? 1 : 0); 
		writeD(_stoneLvl); 
		writeD(_attributableItems.size()); 
		for(int itemObjId : _attributableItems.toArray())
		{
			writeD(itemObjId); 
		}
	}
}