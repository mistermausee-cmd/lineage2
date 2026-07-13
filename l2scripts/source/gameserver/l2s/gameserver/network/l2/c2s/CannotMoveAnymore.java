package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.ObservePoint;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

public class CannotMoveAnymore extends L2GameClientPacket
{
	private Location _loc = new Location();

	
	@Override
	protected void readImpl()
	{
		_loc.x = readD();
		_loc.y = readD();
		_loc.z = readD();
		_loc.h = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isInObserverMode())
		{
			ObservePoint observer = activeChar.getObservePoint();
			if(observer != null)
				observer.stopMove();
			return;
		}

		activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, _loc, null);
	}
}