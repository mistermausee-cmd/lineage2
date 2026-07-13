package l2s.gameserver.network.l2.s2c;


public class ExAlchemyConversion extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExAlchemyConversion(-1);

	private final int _result;
	private final int _successCount;
	private final int _failCount;

	public ExAlchemyConversion(int result)
	{
		_result = result;
		_successCount = 0;
		_failCount = 0;
	}

	public ExAlchemyConversion(int successCount, int failCount)
	{
		_result = 0;
		_successCount = successCount;
		_failCount = failCount;
	}

	@Override
	protected void writeImpl()
	{
		writeC(_result);
		writeD(_successCount);
		writeD(_failCount);
	}
}