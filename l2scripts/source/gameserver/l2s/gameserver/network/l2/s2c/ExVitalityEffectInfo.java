package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;


public class ExVitalityEffectInfo extends L2GameServerPacket
{
	private final int _vitalityPoints;
	private final int _vitalityItemsAllowed;
	private final int _totalVitalityItemsAllowed;
	private final int _bonusPercent;

	public ExVitalityEffectInfo(Player player)
	{
		_vitalityPoints = player.getVitality();
		_totalVitalityItemsAllowed = player.getVitalityPotionsLimit();
		_vitalityItemsAllowed = _totalVitalityItemsAllowed - player.getUsedVitalityPotions();
		_bonusPercent = (int) (player.getVitalityBonus() * 100);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_vitalityPoints); 
		writeD(_bonusPercent); 
		writeH(0x00); 
		writeH(_vitalityItemsAllowed); 
		writeH(_totalVitalityItemsAllowed); 
	}
}