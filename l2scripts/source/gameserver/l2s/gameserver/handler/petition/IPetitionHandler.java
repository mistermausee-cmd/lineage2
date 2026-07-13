package l2s.gameserver.handler.petition;

import l2s.gameserver.model.Player;


public interface IPetitionHandler
{
	void handle(Player player, int id, String txt);
}