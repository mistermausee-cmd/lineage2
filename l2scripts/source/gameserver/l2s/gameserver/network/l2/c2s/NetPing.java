package l2s.gameserver.network.l2.c2s;


public class NetPing extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int unk, unk2, unk3;

	@Override
	protected void runImpl()
	{
		
	}

	@Override
	protected void readImpl()
	{
		unk = readD();
		unk2 = readD();
		unk3 = readD();
	}
}