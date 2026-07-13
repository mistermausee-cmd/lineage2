package l2s.gameserver.network.l2.s2c;

public class EnchantResultPacket extends L2GameServerPacket
{
	private final int _resultId, _crystalId;
	private final long _count;
	private final int _enchantLevel;

	
	
	public static final EnchantResultPacket CANCEL = new EnchantResultPacket(2, 0, 0, 0); 
	public static final EnchantResultPacket BLESSED_FAILED = new EnchantResultPacket(3, 0, 0, 0); 
	public static final EnchantResultPacket FAILED_NO_CRYSTALS = new EnchantResultPacket(4, 0, 0, 0); 
	public static final EnchantResultPacket ANCIENT_FAILED = new EnchantResultPacket(5, 0, 0, 0); 

	public EnchantResultPacket(int resultId, int crystalId, long count, int enchantLevel)
	{
		_resultId = resultId;
		_crystalId = crystalId;
		_count = count;
		_enchantLevel = enchantLevel;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_resultId);
		writeD(_crystalId); 
		writeQ(_count); 
		writeD(_enchantLevel); 
		writeH(0x00); 
		writeH(0x00); 
		writeH(0x00); 
	}
}