package l2s.gameserver.network.l2.s2c;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;


public class CastleSiegeInfoPacket extends L2GameServerPacket
{
	private int _startTime;
	private int _id, _ownerObjectId, _allyId;
	private boolean _isLeader;
	private String _ownerName = "NPC";
	private String _leaderName = StringUtils.EMPTY;
	private String _allyName = StringUtils.EMPTY;

	public CastleSiegeInfoPacket(Residence residence, Player player)
	{
		_id = residence.getId();
		_ownerObjectId = residence.getOwnerId();
		Clan owner = residence.getOwner();
		if(owner != null)
		{
			_isLeader = player.isGM() || owner.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) == player.getObjectId();
			_ownerName = owner.getName();
			_leaderName = owner.getLeaderName(Clan.SUBUNIT_MAIN_CLAN);
			Alliance ally = owner.getAlliance();
			if(ally != null)
			{
				_allyId = ally.getAllyId();
				_allyName = ally.getAllyName();
			}
		}
		_startTime = residence.getSiegeEvent() != null ? (int) (residence.getSiegeDate().getTimeInMillis() / 1000) : 0;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_id);
		writeD(_isLeader ? 0x01 : 0x00);
		writeD(_ownerObjectId);
		writeS(_ownerName); 
		writeS(_leaderName); 
		writeD(_allyId); 
		writeS(_allyName); 
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeD(_startTime);
		
	}
}