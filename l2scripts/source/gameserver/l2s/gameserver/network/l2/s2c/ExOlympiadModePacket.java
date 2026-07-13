package l2s.gameserver.network.l2.s2c;

public class ExOlympiadModePacket extends L2GameServerPacket
{
	
	private int _mode;

	
	public ExOlympiadModePacket(int mode)
	{
		_mode = mode;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(_mode);
	}
}