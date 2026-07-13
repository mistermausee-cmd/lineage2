package handler.dailymissions;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.DailyMission;

/**
 * @author Bonux
**/
public class DualclassLevelUp extends LevelUp
{
	@Override
	public int getProgress(Player player, DailyMission mission)
	{
		return player.getDualClass() != null ? player.getDualClass().getLevel() : 0;
	}
}
