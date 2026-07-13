package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestExOustFromMPCC extends L2GameClientPacket
{
	private String _name;

	
	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
			return;

		Player target = World.getPlayer(_name);

		
		if(target == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		
		if(activeChar == target)
			return;

		
		if(!target.isInParty() || !target.getParty().isInCommandChannel() || activeChar.getParty().getCommandChannel() != target.getParty().getCommandChannel())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		
		if(activeChar.getParty().getCommandChannel().getChannelLeader() != activeChar)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND);
			return;
		}

		target.getParty().getCommandChannel().getChannelLeader().sendPacket(new SystemMessagePacket(SystemMsg.C1S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL).addName(target));
		target.getParty().getCommandChannel().removeParty(target.getParty());
		target.getParty().broadCast(SystemMsg.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
	}
}