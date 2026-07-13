package l2s.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.List;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.utils.SkillUtils;


public class AcquireSkillListPacket extends L2GameServerPacket
{
	private Player _player;
	private Collection<SkillLearn> _skills;

	public AcquireSkillListPacket(Player player)
	{
		_player = player;
		_skills = SkillAcquireHolder.getInstance().getAcquirableSkillListByClass(player);
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_skills.size());
		for(SkillLearn sk : _skills)
		{
			Skill skill = SkillHolder.getInstance().getSkill(sk.getId(), sk.getLevel());
			if(skill == null)
				continue;

			writeD(sk.getId());
			writeH(SkillUtils.getSkillLevelFromMask(sk.getLevel()));
			writeH(0x00);
			writeQ(sk.getCost()); 
			writeC(sk.getMinLevel());

			writeC(sk.getDualClassMinLvl());

			List<ItemData> requiredItems = sk.getRequiredItemsForLearn(AcquireType.NORMAL);
			writeC(requiredItems.size());
			for(ItemData item : requiredItems)
			{
				writeD(item.getId());
				writeQ(item.getCount());
			}
			 
			Skill[] analogSkills = skill.getAnalogSkills(_player);
			writeC(analogSkills.length);
			for(Skill analogSkill : analogSkills)
			{
		        writeD(analogSkill.getId());
		        writeH(analogSkill.getLevel());
		        writeH(0x00);
			}
		}
	}
}