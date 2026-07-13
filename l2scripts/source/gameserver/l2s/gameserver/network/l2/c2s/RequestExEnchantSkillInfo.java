package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoPacket;
import l2s.gameserver.utils.SkillUtils;

public class RequestExEnchantSkillInfo extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(SkillUtils.isEnchantedSkill(_skillLvl))
		{
			Skill skill = SkillHolder.getInstance().getSkill(_skillId, _skillLvl);
			if(skill == null)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(activeChar.getSkillLevel(_skillId) != skill.getLevel())
			{
				activeChar.sendActionFailed();
				return;
			}
		}
		else if(activeChar.getSkillLevel(_skillId) != _skillLvl)
		{
			activeChar.sendActionFailed();
			return;
		}

		sendPacket(new ExEnchantSkillInfoPacket(_skillId, _skillLvl));
	}
}