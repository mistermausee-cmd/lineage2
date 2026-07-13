package l2s.gameserver.network.l2.s2c;

public class ExIsCharNameCreatable extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new ExIsCharNameCreatable(-1); 
	public static final L2GameServerPacket UNABLE_TO_CREATE_A_CHARACTER = new ExIsCharNameCreatable(0x00); 
	public static final L2GameServerPacket TOO_MANY_CHARACTERS = new ExIsCharNameCreatable(0x01); 
	public static final L2GameServerPacket NAME_ALREADY_EXISTS = new ExIsCharNameCreatable(0x02); 
	public static final L2GameServerPacket ENTER_CHAR_NAME__MAX_16_CHARS = new ExIsCharNameCreatable(0x03); 
	public static final L2GameServerPacket WRONG_NAME = new ExIsCharNameCreatable(0x04); 
	public static final L2GameServerPacket WRONG_SERVER = new ExIsCharNameCreatable(0x05); 
	public static final L2GameServerPacket DONT_CREATE_CHARS_ON_THIS_SERVER = new ExIsCharNameCreatable(0x06); 
	public static final L2GameServerPacket DONT_USE_ENG_CHARS = new ExIsCharNameCreatable(0x07); 

	public int _errorCode;

	public ExIsCharNameCreatable(int errorCode)
	{
		_errorCode = errorCode;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_errorCode);
	}
}
