package l2s.gameserver.handler.voicecommands;

import l2s.gameserver.model.Player;


public interface IVoicedCommandHandler
{
	
	public boolean useVoicedCommand(String command, Player activeChar, String target);

	
	public String[] getVoicedCommandList();
}