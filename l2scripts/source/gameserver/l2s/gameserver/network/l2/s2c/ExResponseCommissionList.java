package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.CommissionItem;


public class ExResponseCommissionList extends L2GameServerPacket
{
	public final static int COMMON_EMPTY_LIST = -1;
	public final static int MY_EMPTY_LIST = -2;
	public final static int MY_COMMISSION_LIST = 2;
	public final static int COMMON_COMMISSION_LIST = 3;

	public final static L2GameServerPacket COMMON_EMPTY_LIST_PACKET = new ExResponseCommissionList(COMMON_EMPTY_LIST);
	public final static L2GameServerPacket MY_EMPTY_LIST_PACKET = new ExResponseCommissionList(MY_EMPTY_LIST);

	private final int _currentTimeInSeconds;
	private final int _listType;
	private final int _part;
	private final CommissionItem[] _items;

	private ExResponseCommissionList(int listType)
	{
		_currentTimeInSeconds = 0;
		_listType = listType;
		_part = 0;
		_items = new CommissionItem[0];
	}

	public ExResponseCommissionList(int listType, int part, CommissionItem[] items)
	{
		_currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000L);
		_listType = listType;
		_part = part;
		_items = items;
	}

	protected void writeImpl()
	{
		writeD(_listType);
		if(_listType == COMMON_EMPTY_LIST || _listType == MY_EMPTY_LIST)
			return;

		writeD(_currentTimeInSeconds); 
		writeD(_part); 
		writeD(_items.length); 
		for(CommissionItem item : _items)
		{
			writeQ(item.getCommissionId()); 
			writeQ(item.getCommissionPrice()); 
			writeD(item.getItem().getExType().ordinal());
			writeD(item.getPeriodDays());
			writeD(item.getEndPeriodDate()); 
			writeS(item.getOwnerName()); 
			writeItemInfo(item);
		}
	}
}