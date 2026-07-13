package l2s.gameserver.network.l2.c2s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.BlockListPacket;

public class RequestBlock extends L2GameClientPacket
{
	
	private static final Logger _log = LoggerFactory.getLogger(RequestBlock.class);

	private final static int BLOCK = 0;
	private final static int UNBLOCK = 1;
	private final static int BLOCKLIST = 2;
	private final static int ALLBLOCK = 3;
	private final static int ALLUNBLOCK = 4;

	private Integer _type;
	private String targetName = null;

	@Override
	protected void readImpl()
	{
		_type = readD(); 

		if(_type == BLOCK || _type == UNBLOCK)
			targetName = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		switch(_type)
		{
			case BLOCK:
				activeChar.getBlockList().add(targetName);
				break;
			case UNBLOCK:
				activeChar.getBlockList().remove(targetName);
				break;
			case BLOCKLIST:
				activeChar.sendPacket(new BlockListPacket(activeChar));
				break;
			case ALLBLOCK:
				activeChar.setBlockAll(true);
				activeChar.sendPacket(SystemMsg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
				activeChar.sendEtcStatusUpdate();
				break;
			case ALLUNBLOCK:
				activeChar.setBlockAll(false);
				activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
				activeChar.sendEtcStatusUpdate();
				break;
			default:
				_log.info("Unknown 0x0a block type: " + _type);
		}
	}
}