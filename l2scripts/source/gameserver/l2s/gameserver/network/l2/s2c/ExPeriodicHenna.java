package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Henna;

public class ExPeriodicHenna extends L2GameServerPacket
{
	private final Henna _henna;
	private final boolean _active;

	public ExPeriodicHenna(Player player)
	{
		_henna = player.getHennaList().getPremiumHenna();
		_active = _henna != null && player.getHennaList().isActive(_henna);
	}

	@Override
	protected void writeImpl()
	{
		if(_henna != null)
		{
			writeD(_henna.getTemplate().getSymbolId());	
			writeD(_henna.getLeftTime());	
			writeD(_active);	
		}
		else
		{
			writeD(0x00);	
			writeD(0x00);	
			writeD(0x00);	
		}
	}
}
