package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.model.items.Warehouse.ItemClassComparator;
import l2s.gameserver.templates.item.ItemTemplate;

public class PrivateStoreBuyManageList extends L2GameServerPacket
{
	private int _buyerId;
	private long _adena;
	private List<TradeItem> _buyList0;
	private List<TradeItem> _buyList;

	
	public PrivateStoreBuyManageList(Player buyer)
	{
		_buyerId = buyer.getObjectId();
		_adena = buyer.getAdena();
		_buyList0 = buyer.getBuyList();
		_buyList = new ArrayList<TradeItem>();

		ItemInstance[] items = buyer.getInventory().getItems();
		Arrays.sort(items, ItemClassComparator.getInstance());
		TradeItem bi;
		for(ItemInstance item : items)
			if(item.canBePrivateStore(buyer) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA)
			{
				_buyList.add(bi = new TradeItem(item, item.getTemplate().isBlocked(buyer, item)));
				bi.setObjectId(0);
			}
	}

	@Override
	protected final void writeImpl()
	{
		
		writeD(_buyerId);
		writeQ(_adena);

		
		writeD(_buyList.size());
		for(TradeItem bi : _buyList)
		{
			writeItemInfo(bi);
			writeQ(bi.getStorePrice());
		}

		
		writeD(_buyList0.size());
		for(TradeItem bi : _buyList0)
		{
			writeItemInfo(bi);
			writeQ(bi.getOwnersPrice());
			writeQ(bi.getStorePrice());
			writeQ(bi.getCount());
		}
	}
}