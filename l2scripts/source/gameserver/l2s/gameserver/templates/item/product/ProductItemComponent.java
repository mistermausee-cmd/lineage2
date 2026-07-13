package l2s.gameserver.templates.item.product;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.data.ItemData;


public class ProductItemComponent extends ItemData
{
	private final int _weight;
	private final boolean _dropable;

	public ProductItemComponent(int itemId, int count)
	{
		super(itemId, count);

		ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
		if(item != null)
		{
			_weight = item.getWeight();
			_dropable = item.isDropable();
		}
		else
		{
			
			_weight = 0;
			_dropable = true;
		}
	}

	public int getWeight()
	{
		return _weight;
	}

	public boolean isDropable()
	{
		return _dropable;
	}
}
