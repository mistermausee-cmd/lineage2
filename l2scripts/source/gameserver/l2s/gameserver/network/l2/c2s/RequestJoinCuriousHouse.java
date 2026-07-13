package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseEnter;

public class RequestJoinCuriousHouse extends L2GameClientPacket
{
	protected void readImpl()
	{
		
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		
		{
			
			activeChar.sendPacket(new ExCuriousHouseEnter());
		}
	}
}