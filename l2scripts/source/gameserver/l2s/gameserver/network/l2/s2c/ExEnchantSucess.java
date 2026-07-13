package l2s.gameserver.network.l2.s2c;


public final class ExEnchantSucess extends L2GameServerPacket
{
	private final int _itemId;

	public ExEnchantSucess(int itemId)
	{
		_itemId = itemId;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_itemId);
	}
}