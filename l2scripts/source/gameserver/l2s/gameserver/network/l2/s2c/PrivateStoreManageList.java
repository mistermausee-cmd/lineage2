package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.templates.item.ItemTemplate;

public class PrivateStoreManageList extends L2GameServerPacket
{
	private int _sellerId;
	private long _adena;
	private boolean _package;
	private List<TradeItem> _sellList;
	private List<TradeItem> _sellList0;

	
	public PrivateStoreManageList(Player seller, boolean pkg)
	{
		_sellerId = seller.getObjectId();
		_adena = seller.getAdena();
		_package = pkg;
		_sellList0 = seller.getSellList(_package);
		_sellList = new ArrayList<TradeItem>();

		
		for(TradeItem si : _sellList0)
		{
			if(si.getCount() <= 0)
			{
				_sellList0.remove(si);
				continue;
			}

			ItemInstance item = seller.getInventory().getItemByObjectId(si.getObjectId());
			if(item == null)
				
				item = seller.getInventory().getItemByItemId(si.getItemId());

			if(item == null || !item.canBePrivateStore(seller) || item.getItemId() == ItemTemplate.ITEM_ID_ADENA)
			{
				_sellList0.remove(si);
				continue;
			}

			
			si.setCount(Math.min(item.getCount(), si.getCount()));
		}

		ItemInstance[] items = seller.getInventory().getItems();
		
		loop: for(ItemInstance item : items)
			if(item.canBePrivateStore(seller) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA)
			{
				for(TradeItem si : _sellList0)
					if(si.getObjectId() == item.getObjectId())
					{
						if(si.getCount() == item.getCount())
							continue loop;
						
						TradeItem ti = new TradeItem(item, item.getTemplate().isBlocked(seller, item));
						ti.setCount(item.getCount() - si.getCount());
						_sellList.add(ti);
						continue loop;
					}
				_sellList.add(new TradeItem(item, item.getTemplate().isBlocked(seller, item)));
			}
	}

	@Override
	protected final void writeImpl()
	{
		
		writeD(_sellerId);
		writeD(_package ? 1 : 0);
		writeQ(_adena);

		
		writeD(_sellList.size());
		for(TradeItem si : _sellList)
		{
			writeItemInfo(si);
			writeQ(si.getStorePrice());
		}

		
		writeD(_sellList0.size());
		for(TradeItem si : _sellList0)
		{
			writeItemInfo(si);
			writeQ(si.getOwnersPrice());
			writeQ(si.getStorePrice());
		}
	}
}