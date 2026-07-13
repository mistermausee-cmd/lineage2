package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;


public class ExAcquireAPSkillList extends L2GameServerPacket
{
	private final boolean _avaiable;
	private final long _abilitiesRefreshPrice;
	private final int _allowAbilitiesPoints;
	private final int _usedPoints;
	private final Collection<Skill> _learnedSkills;

	public ExAcquireAPSkillList(Player player)
	{
		_avaiable = player.isAllowAbilities();
		_abilitiesRefreshPrice = Player.getAbilitiesRefreshPrice();
		_allowAbilitiesPoints = player.getAllowAbilitiesPoints();
		_usedPoints = player.getUsedAbilitiesPoints();
		_learnedSkills = player.getLearnedAbilitiesSkills();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_avaiable ? 0x01 : 0x00);	
		writeQ(_abilitiesRefreshPrice);	
		writeD(_allowAbilitiesPoints);	
		writeD(_usedPoints);	
		writeD(_learnedSkills.size());	
		for(Skill skill : _learnedSkills)
		{
			writeD(skill.getId()); 
			writeD(skill.getLevel()); 
		}
	}
}