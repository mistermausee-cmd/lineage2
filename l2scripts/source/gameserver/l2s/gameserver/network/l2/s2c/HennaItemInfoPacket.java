package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.HennaTemplate;

public class HennaItemInfoPacket extends L2GameServerPacket
{
	private final int _str, _con, _dex, _int, _wit, _men, _luc, _cha;
	private final long _adena;
	private final HennaTemplate _hennaTemplate;
	private final boolean _available;

	public HennaItemInfoPacket(HennaTemplate hennaTemplate, Player player)
	{
		_hennaTemplate = hennaTemplate;
		_adena = player.getAdena();
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
		_luc = player.getLUC();
		_cha = player.getCHA();
		_available = _hennaTemplate.isForThisClass(player);
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_hennaTemplate.getSymbolId()); 
		writeD(_hennaTemplate.getDyeId()); 
		writeQ(_hennaTemplate.getDrawCount());
		writeQ(_hennaTemplate.getDrawPrice());
		writeD(_available); 
		writeQ(_adena);
		writeD(_int); 
		writeH(_int + _hennaTemplate.getStatINT()); 
		writeD(_str); 
		writeH(_str + _hennaTemplate.getStatSTR()); 
		writeD(_con); 
		writeH(_con + _hennaTemplate.getStatCON()); 
		writeD(_men); 
		writeH(_men + _hennaTemplate.getStatMEN()); 
		writeD(_dex); 
		writeH(_dex + _hennaTemplate.getStatDEX()); 
		writeD(_wit); 
		writeH(_wit + _hennaTemplate.getStatWIT()); 
		writeD(_luc); 
		writeH(_luc + _hennaTemplate.getStatLUC()); 
		writeD(_cha); 
		writeH(_cha + _hennaTemplate.getStatCHA()); 
		writeD(_hennaTemplate.getPeriod());
	}
}