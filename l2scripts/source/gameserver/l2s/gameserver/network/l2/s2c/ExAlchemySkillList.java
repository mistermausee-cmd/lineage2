package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.skills.SkillEntry;


public class ExAlchemySkillList extends L2GameServerPacket
{
	private final List<SkillEntry> _skills;

	public ExAlchemySkillList(Player player)
	{
		_skills = new ArrayList<SkillEntry>(player.getAllAlchemySkills());
	}

	@Override
	protected void writeImpl()
	{
		writeD(_skills.size());
		for(SkillEntry skillEntry : _skills)
		{
			writeD(skillEntry.getId());
			writeD(skillEntry.getLevel());
			writeD(0x00);
			writeD(0x00);
			writeC(skillEntry.getId() == 17943 ? 0x00 : 0x01);
		}
	}
}