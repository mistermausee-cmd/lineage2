package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.c2s.RequestExReceivePost;
import l2s.gameserver.network.l2.c2s.RequestExRejectPost;
import l2s.gameserver.network.l2.c2s.RequestExRequestReceivedPost;


public class ExReplyReceivedPost extends L2GameServerPacket
{
	private final Mail mail;

	public ExReplyReceivedPost(Mail mail)
	{
		this.mail = mail;
	}

	
	@Override
	protected void writeImpl()
	{
		writeD(mail.getType().ordinal());
		if(mail.getType() == Mail.SenderType.SYSTEM)
		{
			writeD(mail.getSystemParams()[0]);
			writeD(mail.getSystemParams()[1]);
			writeD(mail.getSystemParams()[2]);
			writeD(mail.getSystemParams()[3]);
			writeD(mail.getSystemParams()[4]);
			writeD(mail.getSystemParams()[5]);
			writeD(mail.getSystemParams()[6]);
			writeD(mail.getSystemParams()[7]);
			writeD(mail.getSystemTopic());
			writeD(mail.getSystemBody());
		}
		else if(mail.getType() == Mail.SenderType.UNKNOWN)
		{
			writeD(3492);
			writeD(3493);
		}

		writeD(mail.getMessageId()); 

		writeD(mail.isPayOnDelivery() ? 0x01 : 0x00); 
		writeD(mail.isReturned() ? 0x01 : 0x00);

		writeS(mail.getSenderName()); 
		writeS(mail.getTopic()); 
		writeS(mail.getBody()); 

		writeD(mail.getAttachments().size()); 
		for(ItemInstance item : mail.getAttachments())
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
		}

		writeQ(mail.getPrice()); 
		writeD(mail.isReturnable());
		writeD(mail.getReceiverId()); 
	}
}