package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.ChaosFestivalManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestCuriousHouseRecord extends L2GameClientPacket
{
	protected void readImpl()
	{
		
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		
		int points = (int) ChaosFestivalManager.getInstance().getPoints(activeChar.getObjectId());
		activeChar.sendPacket((new SystemMessagePacket(SystemMsg.YOU_HAVE_OBTAINED_S1_BATTLE_MARKS_DURING_THIS_ROUND_OF_THE_CEREMONY_OF_CHAOS)).addLong(points));
	}
}