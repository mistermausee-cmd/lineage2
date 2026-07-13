package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.utils.SkillUtils;

public class ExOlympiadSpelledInfoPacket extends L2GameServerPacket
{
	
	private int char_obj_id = 0;
	private List<Abnormal> _effects;

	class Abnormal
	{
		int skillId;
		int dat;
		int duration;

		public Abnormal(int skillId, int dat, int duration)
		{
			this.skillId = skillId;
			this.dat = dat;
			this.duration = duration;
		}
	}

	public ExOlympiadSpelledInfoPacket()
	{
		_effects = new ArrayList<Abnormal>();
	}

	public void addEffect(int skillId, int dat, int duration)
	{
		_effects.add(new Abnormal(skillId, dat, duration));
	}

	public void addSpellRecivedPlayer(Player cha)
	{
		if(cha != null)
			char_obj_id = cha.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(char_obj_id);
		writeD(_effects.size());
		for(Abnormal temp : _effects)
		{
			writeD(temp.skillId);
			writeH(SkillUtils.getSkillLevelFromMask(temp.dat)); 
			writeH(SkillUtils.getSubSkillLevelFromMask(temp.dat)); 
			writeD(0x00);
			writeH(temp.duration);
		}
	}
}