package handler.dailymissions;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.listener.actor.player.OnChaosFestivalFinishBattleListener;
import l2s.gameserver.model.Player;

/**
 * @author Bonux
**/
public class ChaosFestivalBattle extends ProgressDailyMissionHandler
{
	private class HandlerListeners implements OnChaosFestivalFinishBattleListener
	{
		@Override
		public void onChaosFestivalFinishBattle(Player player, boolean winner)
		{
			if(!winner) // TODO: Проверить на оффе.
				return;

			progressMission(player, 1, true);
		}
	}

	private final HandlerListeners _handlerListeners = new HandlerListeners();

	@Override
	public CharListener getListener()
	{
		return _handlerListeners;
	}
}
