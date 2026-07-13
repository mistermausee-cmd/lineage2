package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.utils.SkillUtils;

public class ExEnchantSkillInfoPacket extends L2GameServerPacket
{
	private List<Integer> _routes;

	private int _id, _level, _canAdd, canDecrease;

	public ExEnchantSkillInfoPacket(int id, int level)
	{
		_routes = new ArrayList<Integer>();
		_id = id;
		_level = level;

		
		if(SkillUtils.isEnchantedSkill(_level))
		{
			
			Skill nextSkill = SkillUtils.getNextEnchantSkill(_id, _level);
			
			if(nextSkill != null)
			{
				_canAdd = 1;
				addEnchantSkillDetail(nextSkill.getLevel());
			}

			for(Skill temp : SkillUtils.getSkillsForChangeEnchant(_id, _level))
				addEnchantSkillDetail(temp.getLevel());
		}
		else
		{
			
			for(Skill temp : SkillUtils.getSkillsForFirstEnchant(_id, _level))
			{
				addEnchantSkillDetail(temp.getLevel());
				_canAdd = 1;
			}
		}
	}

	public void addEnchantSkillDetail(int level)
	{
		_routes.add(level);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_id);
		writeD(_level);
		writeD(_canAdd); 
		writeD(canDecrease); 

		writeD(_routes.size());
		for(Integer route : _routes)
			writeD(route);
	}
}