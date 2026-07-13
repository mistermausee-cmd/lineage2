package l2s.gameserver.handler.usercommands;

import l2s.gameserver.model.Player;

public interface IUserCommandHandler
{
	
	public boolean useUserCommand(int id, Player activeChar);

	
	public int[] getUserCommandList();
}