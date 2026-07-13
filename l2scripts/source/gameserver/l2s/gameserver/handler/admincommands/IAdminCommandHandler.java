package l2s.gameserver.handler.admincommands;

import l2s.gameserver.model.Player;

public interface IAdminCommandHandler
{
	
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar);

	
	public Enum<?>[] getAdminCommandEnum();
}