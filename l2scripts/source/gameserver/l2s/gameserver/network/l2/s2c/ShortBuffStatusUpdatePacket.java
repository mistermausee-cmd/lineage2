package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.actor.instances.creature.Abnormal;

public class ShortBuffStatusUpdatePacket extends L2GameServerPacket
{
	

	int _skillId;
	int _skillLevel;
	int _skillDuration;

	public ShortBuffStatusUpdatePacket(Abnormal effect)
	{
		_skillId = effect.getSkill().getDisplayId();
		_skillLevel = effect.getSkill().getDisplayLevel();
		_skillDuration = effect.getTimeLeft();
	}

	
	public ShortBuffStatusUpdatePacket()
	{
		_skillId = 0;
		_skillLevel = 0;
		_skillDuration = 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_skillId); 
		writeD(_skillLevel); 
		writeD(_skillDuration); 
	}
}