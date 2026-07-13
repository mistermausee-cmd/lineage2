package l2s.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;


public class CastleSiegeAttackerListPacket extends L2GameServerPacket
{
	private int _id, _registrationValid;
	private List<SiegeClanObject> _clans = Collections.emptyList();

	public CastleSiegeAttackerListPacket(Residence residence)
	{
		_id = residence.getId();

		SiegeEvent<?,?> siegeEvent = residence.getSiegeEvent();
		if(siegeEvent != null)
		{
			_registrationValid = !siegeEvent.isRegistrationOver() ? 1 : 0;
			_clans = siegeEvent.getObjects(SiegeEvent.ATTACKERS);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_id);

		writeD(0x00);
		writeD(_registrationValid);
		writeD(0x00);

		writeD(_clans.size());
		writeD(_clans.size());

		for(SiegeClanObject siegeClan : _clans)
		{
			Clan clan = siegeClan.getClan();

			writeD(clan.getClanId());
			writeS(clan.getName());
			writeS(clan.getLeaderName());
			writeD(clan.getCrestId());
			writeD((int) (siegeClan.getDate() / 1000L));

			Alliance alliance = clan.getAlliance();
			writeD(clan.getAllyId());
			if(alliance != null)
			{
				writeS(alliance.getAllyName());
				writeS(alliance.getAllyLeaderName());
				writeD(alliance.getAllyCrestId());
			}
			else
			{
				writeS(StringUtils.EMPTY);
				writeS(StringUtils.EMPTY);
				writeD(0);
			}
		}
	}
}