package l2s.gameserver.network.l2.s2c;


public class ExEnchantSkillInfoDetailPacket extends L2GameServerPacket
{
	private final int _unk = 0;
	private final int _skillId;
	private final int _skillLvl;
	private final long _sp;
	private final int _chance;
	private final int _bookId, _bookCount, _adenaCount;

	public ExEnchantSkillInfoDetailPacket(int skillId, int skillLvl, long sp, int chance, int bookId, int bookCount, int adenaCount)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
		_sp = sp;
		_chance = chance;
		_bookId = bookId;
		_bookCount = bookCount;
		_adenaCount = adenaCount;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_unk); 
		writeD(_skillId);
		writeD(_skillLvl);
		writeQ(_sp);
		writeD(_chance);

		writeD(2);
		writeD(57); 
		writeD(_adenaCount); 

		writeD(_bookId); 
		writeD(_bookCount); 
	}
}