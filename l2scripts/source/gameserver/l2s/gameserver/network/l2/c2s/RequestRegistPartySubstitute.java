package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExRegistPartySubstitute;

public class RequestRegistPartySubstitute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
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

		final Player target = World.getPlayer(_objectId);
		if(target != null && target.getParty() == party && !target.isPartySubstituteStarted())
		{
			target.startSubstituteTask();
			
			activeChar.sendPacket(new ExRegistPartySubstitute(_objectId), SystemMsg.LOOKING_FOR_A_PLAYER_WHO_WILL_REPLACE_THE_SELECTED_PARTY_MEMBER);
		}
	}
}
