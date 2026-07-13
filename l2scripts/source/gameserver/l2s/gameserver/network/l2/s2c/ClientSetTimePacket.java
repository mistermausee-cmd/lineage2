package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.GameTimeController;

public class ClientSetTimePacket extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ClientSetTimePacket();

	@Override
	protected final void writeImpl()
	{
		writeD(GameTimeController.getInstance().getGameTime()); 
		writeD(6); 
	}
}