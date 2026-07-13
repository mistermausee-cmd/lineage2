package l2s.gameserver.network.l2.s2c;

public class ExRegenMaxPacket extends L2GameServerPacket
{
	private double _max;
	private int _count;
	private int _time;

	public ExRegenMaxPacket(double max, int count, int time)
	{
		_max = max * .66;
		_count = count;
		_time = time;
	}

	public static final int POTION_HEALING_GREATER = 16457;
	public static final int POTION_HEALING_MEDIUM = 16440;
	public static final int POTION_HEALING_LESSER = 16416;

	
	@Override
	protected void writeImpl()
	{
		writeD(1);
		writeD(_count);
		writeD(_time);
		writeF(_max);
	}
}