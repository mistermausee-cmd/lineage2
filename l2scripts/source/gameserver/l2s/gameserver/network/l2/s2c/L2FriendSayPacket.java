package l2s.gameserver.network.l2.s2c;


public class L2FriendSayPacket extends L2GameServerPacket
{
	private String _sender, _receiver, _message;

	public L2FriendSayPacket(String sender, String reciever, String message)
	{
		_sender = sender;
		_receiver = reciever;
		_message = message;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(0);
		writeS(_receiver);
		writeS(_sender);
		writeS(_message);
	}
}