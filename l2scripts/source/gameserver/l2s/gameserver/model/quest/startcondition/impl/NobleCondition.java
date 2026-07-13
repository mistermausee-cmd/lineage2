package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;


public final class NobleCondition implements ICheckStartCondition
{
	private final boolean _allowOnlyNoble;

	public NobleCondition(boolean allowOnlyNoble)
	{
		_allowOnlyNoble = allowOnlyNoble;
	}

	@Override
	public final boolean checkCondition(Player player)
	{
		if(_allowOnlyNoble && player.isNoble())
			return true;
		if(!_allowOnlyNoble && !player.isNoble())
			return true;
		return false;
	}
}