package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExResponseBeautyListPacket;


public class RequestShowBeautyList extends L2GameClientPacket
{
	private int _type;

	@Override
	protected void readImpl() throws Exception
	{
		_type = readD(); 
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExResponseBeautyListPacket(activeChar, _type));
	}
}