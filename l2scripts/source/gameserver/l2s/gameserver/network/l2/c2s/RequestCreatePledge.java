package l2s.gameserver.network.l2.c2s;

public class RequestCreatePledge extends L2GameClientPacket
{
	
	private String _pledgename;

	@Override
	protected void readImpl()
	{
		_pledgename = readS(64);
	}

	@Override
	protected void runImpl()
	{
		
	}
}