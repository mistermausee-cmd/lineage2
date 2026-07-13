package l2s.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;

import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.c2s.RequestExDeleteSentPost;
import l2s.gameserver.network.l2.c2s.RequestExRequestSentPost;
import l2s.gameserver.network.l2.c2s.RequestExRequestSentPostList;


public class ExShowSentPostList extends L2GameServerPacket
{
	private final List<Mail> mails;

	public ExShowSentPostList(Player cha)
	{
		mails = MailDAO.getInstance().getSentMailByOwnerId(cha.getObjectId());
		Collections.sort(mails);
	}

	
	@Override
	protected void writeImpl()
	{
		writeD((int) (System.currentTimeMillis() / 1000L));
		writeD(mails.size()); 
		for(Mail mail : mails)
		{
			writeD(mail.getMessageId()); 
			writeS(mail.getTopic()); 
			writeS(mail.getReceiverName()); 
			writeD(mail.isPayOnDelivery() ? 1 : 0); 
			writeD(mail.getExpireTime()); 
			writeD(mail.isUnread() ? 1 : 0); 
			writeD(mail.isReturnable()); 
			writeD(mail.getAttachments().isEmpty() ? 0 : 1); 
			writeD(0x00); 
		}
	}
}