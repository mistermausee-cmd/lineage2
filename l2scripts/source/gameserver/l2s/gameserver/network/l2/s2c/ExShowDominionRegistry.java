package l2s.gameserver.network.l2.s2c;

public class ExShowDominionRegistry extends L2GameServerPacket
{
	public ExShowDominionRegistry()
	{
		
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00);
		writeS("");
		writeS("");
		writeS("");
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x01);
		writeD(0x00); 
	}
}