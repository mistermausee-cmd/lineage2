package l2s.gameserver.network.l2.s2c;


public class ExCallToChangeClass extends L2GameServerPacket
{
	private int _classId;
	private boolean _showMsg;

	public ExCallToChangeClass(int classId, boolean showMsg)
	{
		_classId = classId;
		_showMsg = showMsg;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_classId); 
		writeD(_showMsg); 
	}
}
