package l2s.gameserver.model.quest.startcondition.impl;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;


public class ClassLevelCondition implements ICheckStartCondition
{
	private final boolean _ertheia;
	private final ClassLevel[] _classLevels;

	public ClassLevelCondition(boolean ertheia, ClassLevel... classLevels)
	{
		_ertheia = ertheia;
		_classLevels = classLevels;
	}

	@Override
	public boolean checkCondition(Player player)
	{
		if(_ertheia && player.getClassId().isOfRace(Race.ERTHEIA) || !_ertheia && !player.getClassId().isOfRace(Race.ERTHEIA))
			return ArrayUtils.contains(_classLevels, player.getClassLevel());
		return true;
	}
}