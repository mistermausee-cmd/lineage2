package l2s.gameserver.network.l2.s2c;

public class CameraModePacket extends L2GameServerPacket
{
	int _mode;

	
	public CameraModePacket(int mode)
	{
		_mode = mode;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_mode);
	}
}