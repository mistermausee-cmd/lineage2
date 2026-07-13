package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.templates.npc.BuyListTemplate;


public final class BuyListSeedPacket extends L2GameServerPacket
{
	private int _manorId;
	private List<TradeItem> _list = new ArrayList<TradeItem>();
	private long _money;

	public BuyListSeedPacket(BuyListTemplate list, int manorId, long currentMoney)
	{
		_money = currentMoney;
		_manorId = manorId;
		_list = list.getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeQ(_money); 
		writeD(0x00); 
		writeD(_manorId); 

		writeH(_list.size()); 

		for(TradeItem item : _list)
		{
			writeItemInfo(item);
			writeQ(item.getOwnersPrice());
		}
	}
}