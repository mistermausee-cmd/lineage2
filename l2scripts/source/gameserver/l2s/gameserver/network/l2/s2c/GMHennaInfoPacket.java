package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Henna;
import l2s.gameserver.model.actor.instances.player.HennaList;

public class GMHennaInfoPacket extends L2GameServerPacket
{
	private final Player _player;
	private final HennaList _hennaList;

	public GMHennaInfoPacket(Player player)
	{
		_player = player;
		_hennaList = player.getHennaList();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_hennaList.getINT()); 
		writeD(_hennaList.getSTR()); 
		writeD(_hennaList.getCON()); 
		writeD(_hennaList.getMEN()); 
		writeD(_hennaList.getDEX()); 
		writeD(_hennaList.getWIT()); 
		writeD(_hennaList.getLUC()); 
		writeD(_hennaList.getCHA()); 
		writeD(HennaList.MAX_SIZE); 
		writeD(_hennaList.size());
		for(Henna henna : _hennaList.values(false))
		{
			writeD(henna.getTemplate().getSymbolId());
			writeD(_hennaList.isActive(henna));
		}

		Henna henna = _hennaList.getPremiumHenna();
		if(henna != null)
		{
			writeD(henna.getTemplate().getSymbolId());	
			writeD(_hennaList.isActive(henna));	
			writeD(henna.getLeftTime());	
		}
		else
		{
			writeD(0x00);	
			writeD(0x00);	
			writeD(0x00);	
		}
	}
}