package l2s.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;

import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.c2s.RequestExDeleteReceivedPost;
import l2s.gameserver.network.l2.c2s.RequestExPostItemList;
import l2s.gameserver.network.l2.c2s.RequestExRequestReceivedPost;
import l2s.gameserver.network.l2.c2s.RequestExRequestReceivedPostList;


public class ExShowReceivedPostList extends L2GameServerPacket
{
	private final Mail[] _mails;

	public ExShowReceivedPostList(Player cha)
	{
		List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(cha.getObjectId());
		Collections.sort(mails);
		_mails = mails.toArray(new Mail[mails.size()]);
	}

	
	@Override
	protected void writeImpl()
	{
		writeD((int) (System.currentTimeMillis() / 1000L));
		writeD(_mails.length); 
		for(Mail mail : _mails)
		{
			writeD(mail.getType().ordinal()); 

			if(mail.getType() == Mail.SenderType.SYSTEM)
				writeD(mail.getSystemTopic());

			writeD(mail.getMessageId()); 
			writeS(mail.getTopic()); 
			writeS(mail.getSenderName()); 
			writeD(mail.isPayOnDelivery() ? 1 : 0); 
			writeD(mail.getExpireTime()); 
			writeD(mail.isUnread() ? 1 : 0); 
			writeD(mail.isReturnable()); 
			writeD(mail.getAttachments().isEmpty() ? 0 : 1); 
			writeD(mail.isReturned() ? 1 : 0);
			writeD(mail.getReceiverId());
		}
		writeD(100);
		writeD(1000);
	}
}