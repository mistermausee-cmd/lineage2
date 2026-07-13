package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.ExReplyPostItemList;
import l2s.gameserver.network.l2.s2c.ExShowReceivedPostList;


public class RequestExPostItemList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
			activeChar.sendActionFailed();

		if(!Config.ALLOW_MAIL)
		{
			activeChar.sendMessage(new CustomMessage("mail.Disabled"));
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new ExReplyPostItemList(activeChar));
	}
}