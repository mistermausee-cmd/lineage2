package l2s.gameserver.handler.usercommands.impl;

import l2s.gameserver.handler.usercommands.IUserCommandHandler;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMultiPartyCommandChannelInfoPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;


public class CommandChannel implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS = { 92, 93, 96, 97 };

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if(id != COMMAND_IDS[0] && id != COMMAND_IDS[1] && id != COMMAND_IDS[2] && id != COMMAND_IDS[3])
			return false;

		switch(id)
		{
			case 92: 
				
				activeChar.sendMessage(new CustomMessage("usercommandhandlers.CommandChannel"));
				break;
			case 93: 
				if(!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
					return true;
				if(activeChar.getParty().getCommandChannel().getChannelLeader() == activeChar)
				{
					l2s.gameserver.model.CommandChannel channel = activeChar.getParty().getCommandChannel();
					channel.disbandChannel();
				}
				else
					activeChar.sendPacket(SystemMsg.ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_USE_THE_CHANNEL_DISMISS_COMMAND);
				break;
			case 96: 
				
				if(!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
					return true;
				if(!activeChar.getParty().isLeader(activeChar))
				{
					activeChar.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_LEAVE_A_COMMAND_CHANNEL);
					return true;
				}
				l2s.gameserver.model.CommandChannel channel = activeChar.getParty().getCommandChannel();

				
				
				if(channel.getChannelLeader() == activeChar)
				{
					if(channel.getParties().size() > 1)
						return false;

					
					channel.disbandChannel();
					return true;
				}

				Party party = activeChar.getParty();
				channel.removeParty(party);
				party.broadCast(SystemMsg.YOU_HAVE_QUIT_THE_COMMAND_CHANNEL);
				channel.broadCast(new SystemMessage(SystemMessage.S1_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL).addString(activeChar.getName()));
				break;
			case 97: 
				if(!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
					return false;
				activeChar.sendPacket(new ExMultiPartyCommandChannelInfoPacket(activeChar.getParty().getCommandChannel()));
				break;
		}
		return true;
	}

	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}