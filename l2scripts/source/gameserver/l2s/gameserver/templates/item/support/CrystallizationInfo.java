package l2s.gameserver.templates.item.support;

import java.util.Collection;
import java.util.TreeMap;

import l2s.gameserver.templates.item.data.ChancedItemData;

public class CrystallizationInfo
{
	private final TreeMap<Integer, ChancedItemData> _items;
	
	public CrystallizationInfo()
	{
		_items = new TreeMap<Integer, ChancedItemData>();
	}

	public void addItem(ChancedItemData item)
	{
		if(item.getCount() > 0)
			_items.put(_items.size() + 1, item);
	}

	public Collection<ChancedItemData> getItems()
	{
		return _items.values();
	}
}