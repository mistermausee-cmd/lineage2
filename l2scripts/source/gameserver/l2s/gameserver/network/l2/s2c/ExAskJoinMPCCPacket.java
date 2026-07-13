package l2s.gameserver.network.l2.s2c;


public class ExAskJoinMPCCPacket extends L2GameServerPacket
{
	private String _requestorName;

	
	public ExAskJoinMPCCPacket(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected void writeImpl()
	{
		writeS(_requestorName); 
		writeD(0x00);
	}
}