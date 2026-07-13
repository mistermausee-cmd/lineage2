package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.network.l2.c2s.RequestExRequestReceivedPostList;


public class ExNoticePostArrived extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC_TRUE = new ExNoticePostArrived(1);
	public static final L2GameServerPacket STATIC_FALSE = new ExNoticePostArrived(0);

	private int _anim;

	public ExNoticePostArrived(int useAnim)
	{
		_anim = useAnim;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_anim); 
	}
}