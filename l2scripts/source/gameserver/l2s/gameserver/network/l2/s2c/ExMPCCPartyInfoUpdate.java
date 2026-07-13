package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;


public class ExMPCCPartyInfoUpdate extends L2GameServerPacket
{
	private Party _party;
	Player _leader;
	private int _mode, _count;

	
	public ExMPCCPartyInfoUpdate(Party party, int mode)
	{
		_party = party;
		_mode = mode;
		_count = _party.getMemberCount();
		_leader = _party.getPartyLeader();
	}

	@Override
	protected void writeImpl()
	{
		writeS(_leader.getName());
		writeD(_leader.getObjectId());
		writeD(_count);
		writeD(_mode); 
	}
}