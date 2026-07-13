package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;


public class ExFishingStartCombatPacket extends L2GameServerPacket
{
	int _time, _hp;
	int _lureType, _deceptiveMode, _mode;
	private int char_obj_id;

	public ExFishingStartCombatPacket(Creature character, int time, int hp, int mode, int lureType, int deceptiveMode)
	{
		char_obj_id = character.getObjectId();
		_time = time;
		_hp = hp;
		_mode = mode;
		_lureType = lureType;
		_deceptiveMode = deceptiveMode;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(char_obj_id);
		writeD(_time);
		writeD(_hp);
		writeC(_mode); 
		writeC(_lureType); 
		writeC(_deceptiveMode); 
	}
}