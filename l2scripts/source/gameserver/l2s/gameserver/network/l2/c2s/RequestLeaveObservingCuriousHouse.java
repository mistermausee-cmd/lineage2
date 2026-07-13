package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestLeaveObservingCuriousHouse extends L2GameClientPacket
{
	protected void readImpl()
	{
		
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		 
	    if(activeChar.getObserverMode() == 3)
	    	activeChar.leaveObserverMode();
	}
}