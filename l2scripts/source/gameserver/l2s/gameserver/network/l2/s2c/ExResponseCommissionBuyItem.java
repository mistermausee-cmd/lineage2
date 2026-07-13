package l2s.gameserver.network.l2.s2c;


public class ExResponseCommissionBuyItem extends L2GameServerPacket
{
	public static final ExResponseCommissionBuyItem FAILED = new ExResponseCommissionBuyItem();

	private int _code;
	private int _itemId;
	private long _count;

	public ExResponseCommissionBuyItem()
	{
		_code = 0;
	}

	public ExResponseCommissionBuyItem(int itemId, long count)
	{
		_code = 1;
		_itemId = itemId;
		_count = count;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_code);
		if(_code == 0)
			return;

		writeD(0); 
		writeD(_itemId);
		writeQ(_count);
	}
}