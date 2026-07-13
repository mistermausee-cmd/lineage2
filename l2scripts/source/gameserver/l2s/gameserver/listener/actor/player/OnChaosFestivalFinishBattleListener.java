package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;

public interface OnChaosFestivalFinishBattleListener extends PlayerListener
{
	public void onChaosFestivalFinishBattle(Player player, boolean winner);
}