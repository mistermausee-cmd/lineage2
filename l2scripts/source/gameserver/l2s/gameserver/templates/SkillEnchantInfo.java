package l2s.gameserver.templates;


public class SkillEnchantInfo
{
	private final int _enchantLevel;
	private final long _adena;
	private final long _sp;
	private final boolean _safe;
	private final int _succesRate;
	private final int _normalEnchantItemId;
	private final int _blessedEnchantItemId;
	private final int _changeEnchantItemId;
	private final int _safeEnchantItemId;

	public SkillEnchantInfo(int enchantLevel, long adena, long sp, boolean safe, int succesRate, int normalEnchantItemId, int blessedEnchantItemId, int changeEnchantItemId, int safeEnchantItemId)
	{
		_enchantLevel = enchantLevel;
		_adena = adena;
		_sp = sp;
		_safe = safe;
		_succesRate = succesRate;
		_normalEnchantItemId = normalEnchantItemId;
		_blessedEnchantItemId = blessedEnchantItemId;
		_changeEnchantItemId = changeEnchantItemId;
		_safeEnchantItemId = safeEnchantItemId;
	}

	public int getEnchantLevel()
	{
		return _enchantLevel;
	}

	public long getAdena()
	{
		return _adena;
	}

	public long getSp()
	{
		return _sp;
	}

	public boolean isSafe()
	{
	    return _safe;
	}
	  
	public int getSuccesRate()
	{
		return _succesRate;
	}

	public int getNormalEnchantItemId()
	{
		return _normalEnchantItemId;
	}

	public int getBlessedEnchantItemId()
	{
		return _blessedEnchantItemId;
	}

	public int getChangeEnchantItemId()
	{
		return _changeEnchantItemId;
	}

	public int getSafeEnchantItemId()
	{
		return _safeEnchantItemId;
	}
}