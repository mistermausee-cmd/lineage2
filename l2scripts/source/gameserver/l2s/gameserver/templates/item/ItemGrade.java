package l2s.gameserver.templates.item;


public enum ItemGrade
{
	NONE(ItemTemplate.CRYSTAL_NONE, 0),
	D(ItemTemplate.CRYSTAL_D, 1),
	C(ItemTemplate.CRYSTAL_C, 2),
	B(ItemTemplate.CRYSTAL_B, 3),
	A(ItemTemplate.CRYSTAL_A, 4),
	S(ItemTemplate.CRYSTAL_S, 5),
	S80(ItemTemplate.CRYSTAL_S, S, 5),
	S84(ItemTemplate.CRYSTAL_S, S, 5),
	R(ItemTemplate.CRYSTAL_R, 6),
	R95(ItemTemplate.CRYSTAL_R, R, 6),
	R99(ItemTemplate.CRYSTAL_R, R, 6);

	public static final ItemGrade[] VALUES = values();

	private final int _crystalId;
	private final int _extOrdinal;
	private final ItemGrade _extGrade;

	ItemGrade(int crystalId, ItemGrade extGrade, int extOrdinal)
	{
		_crystalId = crystalId;
		_extGrade = extGrade;
		_extOrdinal = extOrdinal;
	}

	ItemGrade(int crystalId, int extOrdinal)
	{
		_crystalId = crystalId;
		_extGrade = this;
		_extOrdinal = extOrdinal;
	}

	public int getCrystalId()
	{
		return _crystalId;
	}

	public ItemGrade extGrade()
	{
		return _extGrade;
	}

	public int extOrdinal()
	{
		return _extOrdinal;
	}
}