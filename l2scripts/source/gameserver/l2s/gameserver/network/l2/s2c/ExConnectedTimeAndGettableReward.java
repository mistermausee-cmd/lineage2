package l2s.gameserver.network.l2.s2c;


public class ExConnectedTimeAndGettableReward extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExConnectedTimeAndGettableReward();

	@Override
	protected final void writeImpl()
	{
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
		writeD(0x00);       
	}
}