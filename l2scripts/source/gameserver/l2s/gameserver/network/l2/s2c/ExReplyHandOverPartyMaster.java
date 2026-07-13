package l2s.gameserver.network.l2.s2c;


public class ExReplyHandOverPartyMaster extends L2GameServerPacket
{
	public static final L2GameServerPacket TRUE = new ExReplyHandOverPartyMaster(true);
	public static final L2GameServerPacket FALSE = new ExReplyHandOverPartyMaster(false);

	private boolean _isLeader;

	public ExReplyHandOverPartyMaster(boolean leader)
	{
		_isLeader = leader;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_isLeader);
	}
}
