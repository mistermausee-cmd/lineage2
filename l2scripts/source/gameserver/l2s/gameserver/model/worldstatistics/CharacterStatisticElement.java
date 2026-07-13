package l2s.gameserver.model.worldstatistics;


public class CharacterStatisticElement
{
	private CategoryType categoryType;
	private long value;
	private long monthlyValue;

	public CharacterStatisticElement(CategoryType type, long value, long monthlyValue)
	{
		categoryType = type;
		this.value = value;
		this.monthlyValue = monthlyValue;
	}

	public CharacterStatisticElement(CategoryType type, long value)
	{
		this(type, value, 0L);
	}

	public CategoryType getCategoryType()
	{
		return categoryType;
	}

	public long getValue()
	{
		return value;
	}

	public long getMonthlyValue()
	{
		return monthlyValue;
	}
}