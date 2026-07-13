package l2s.gameserver.network.l2.c2s;


public class RequestAcceptWaitingSubstitute extends L2GameClientPacket
{
	private int _flag;
	private int _unk1;
	private int _unk2;

	@Override
	protected void readImpl()
	{
		_flag = readD();
		_unk1 = readD();
		_unk2 = readD();
	}

	@Override
	protected void runImpl()
	{
		
	}
}
