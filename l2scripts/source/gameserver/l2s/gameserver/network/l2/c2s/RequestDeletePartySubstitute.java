package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;

public class RequestDeletePartySubstitute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		final Party party = activeChar.getParty();
		if(party == null || party.getPartyLeader() != activeChar)
			return;

		
	}
}
