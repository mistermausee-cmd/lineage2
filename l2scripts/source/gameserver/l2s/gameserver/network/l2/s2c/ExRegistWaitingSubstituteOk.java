package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;


public class ExRegistWaitingSubstituteOk extends L2GameServerPacket
{
	private final Player _partyLeader;

	public ExRegistWaitingSubstituteOk(final Player player)
	{
		_partyLeader = player;
	}

	@Override
	protected void writeImpl()
	{
		
	}
}
