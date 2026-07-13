package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;


public class RequestExBR_LectureMark extends L2GameClientPacket
{
	public static final int INITIAL_MARK = 1;
	public static final int EVANGELIST_MARK = 2;
	public static final int OFF_MARK = 3;

	private int _mark;

	@Override
	protected void readImpl() throws Exception
	{
		_mark = readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || !Config.EX_LECTURE_MARK)
			return;

		switch(_mark)
		{
			case INITIAL_MARK:
			case EVANGELIST_MARK:
			case OFF_MARK:
				
				player.setLectureMark(_mark);
				player.broadcastUserInfo(true);
				break;
		}
	}
}