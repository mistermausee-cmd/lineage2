package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExMentorList;


public class RequestMentorList extends L2GameClientPacket
{
	@Override
	protected void runImpl()
	{
		
	}

	@Override
	protected void readImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		sendPacket(new ExMentorList(activeChar));
	}
}