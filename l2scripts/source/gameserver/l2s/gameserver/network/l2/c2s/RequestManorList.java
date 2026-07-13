package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.network.l2.s2c.ExSendManorListPacket;


public class RequestManorList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new ExSendManorListPacket());
	}
}