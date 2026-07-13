package l2s.gameserver.network.l2.s2c;

public class GameGuardQuery extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00); 
	}
}