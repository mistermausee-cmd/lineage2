package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.Residence;


public class ExSendManorListPacket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		Collection<Castle> residences = ResidenceHolder.getInstance().getResidenceList(Castle.class);
		writeD(residences.size());
		for(Residence castle : residences)
		{
			writeD(castle.getId());
			writeS(castle.getName().toLowerCase());
		}
	}
}