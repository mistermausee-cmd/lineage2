package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.SkillUtils;

public class GMViewSkillInfoPacket extends L2GameServerPacket
{
	private final String _charName;
	private final Collection<SkillEntry> _skills;
	private final Player _targetChar;

	public GMViewSkillInfoPacket(Player cha)
	{
		_charName = cha.getName();
		_skills = cha.getAllSkills();
		_targetChar = cha;
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_charName);
		writeD(_skills.size());
		for(SkillEntry skillEntry : _skills)
		{
			Skill temp = skillEntry.getTemplate();
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1);
			if(temp.getChainIndex() != -1 && temp.getChainSkillId() != 0 && _targetChar.getSkillChainDetails().containsKey(temp.getChainIndex()))
			{
				writeD(1);
				writeD(14612 + temp.getChainIndex());
			}
			else
			{
				writeD(temp.getDisplayLevel());
				writeD(temp.getDisplayId());
			}
			writeD(temp.getReuseSkillId());
			writeC(_targetChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00);
			writeC(SkillUtils.isSkillEnchantAvailable(_targetChar, temp));
		}
		writeD(0);
	}
}