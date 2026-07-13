package l2s.gameserver.network.l2.c2s;


class SuperCmdCharacterInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _characterName;

	@Override
	protected void readImpl()
	{
		_characterName = readS();
	}

	@Override
	protected void runImpl()
	{}
}