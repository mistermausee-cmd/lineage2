package l2s.gameserver.handler.items.impl;


public class SkillsReduceItemHandler extends SkillsItemHandler
{
	@Override
	public boolean reduceAfterUse()
	{
		return true;
	}
}