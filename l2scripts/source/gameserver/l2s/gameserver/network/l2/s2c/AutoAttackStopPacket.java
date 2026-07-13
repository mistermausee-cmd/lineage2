package l2s.gameserver.network.l2.s2c;

public class AutoAttackStopPacket extends L2GameServerPacket
{
	
	private int _targetId;

	
	public AutoAttackStopPacket(int targetId)
	{
		_targetId = targetId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_targetId);
	}
}