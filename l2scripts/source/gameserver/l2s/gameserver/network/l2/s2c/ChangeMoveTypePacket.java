package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;


public class ChangeMoveTypePacket extends L2GameServerPacket
{
	public static int WALK = 0;
	public static int RUN = 1;

	private int _chaId;
	private boolean _running;

	public ChangeMoveTypePacket(Creature cha)
	{
		_chaId = cha.getObjectId();
		_running = cha.isRunning();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_chaId);
		writeD(_running ? 1 : 0);
		writeD(0); 
	}
}