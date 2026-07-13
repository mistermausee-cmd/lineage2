package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;


public class ExFishingHpRegenPacket extends L2GameServerPacket
{
	private int _time, _fishHP, _HPmode, _Anim, _GoodUse, _Penalty, _hpBarColor;
	private int char_obj_id;

	public ExFishingHpRegenPacket(Creature character, int time, int fishHP, int HPmode, int GoodUse, int anim, int penalty, int hpBarColor)
	{
		char_obj_id = character.getObjectId();
		_time = time;
		_fishHP = fishHP;
		_HPmode = HPmode;
		_GoodUse = GoodUse;
		_Anim = anim;
		_Penalty = penalty;
		_hpBarColor = hpBarColor;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(char_obj_id);
		writeD(_time);
		writeD(_fishHP);
		writeC(_HPmode); 
		writeC(_GoodUse); 
		writeC(_Anim); 
		writeD(_Penalty); 
		writeC(_hpBarColor); 

	}
}