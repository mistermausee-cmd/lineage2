package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class EtcStatusUpdatePacket extends L2GameServerPacket
{
	private static final int NO_CHAT_FLAG = 1 << 0;
	private static final int DANGER_AREA_FLAG = 1 << 1;
	private static final int CHARM_OF_COURAGE_FLAG = 1 << 2;

	

	private int _increasedForce, _weightPenalty, _weaponPenalty, _armorPenalty, _consumedSouls;
	private int _flags, _deathPenalty;

	public EtcStatusUpdatePacket(Player player)
	{
		_increasedForce = player.getIncreasedForce();
		_weightPenalty = player.getWeightPenalty();
		_weaponPenalty = player.getWeaponsExpertisePenalty();
		_armorPenalty = player.getArmorsExpertisePenalty();
		_deathPenalty = player.getDeathPenalty().getLevel();
		_consumedSouls = player.getConsumedSouls();

		if(player.getMessageRefusal() || player.getNoChannel() != 0 || player.isBlockAll())
			_flags |= NO_CHAT_FLAG; 
		if(player.isInDangerArea())
			_flags |= DANGER_AREA_FLAG; 
		if(player.isCharmOfCourage())
			_flags |= CHARM_OF_COURAGE_FLAG; 
	}

	@Override
	protected final void writeImpl()
	{
		
		writeC(_increasedForce); 
		writeD(_weightPenalty); 
		writeC(_weaponPenalty); 
		writeC(_armorPenalty); 
		writeC(_deathPenalty); 
		writeC(_consumedSouls);
		writeC(_flags);
	}
}