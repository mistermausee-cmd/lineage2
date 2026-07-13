package l2s.gameserver.model.base;


public enum AcquireType
{
	NORMAL, 
    FISHING, 
    CLAN, 
    SUB_UNIT, 
    TRANSFORMATION, 
    CERTIFICATION, 
    DUAL_CERTIFICATION, 
    COLLECTION, 
    TRANSFER_CARDINAL, 
    TRANSFER_EVA_SAINTS, 
    TRANSFER_SHILLIEN_SAINTS, 
    GENERAL, 
    NOBLESSE, 
    HERO, 
    GM, 
    CHAOS, 
    DUAL_CHAOS, 
    ABILITY, 
    HONORABLE_NOBLESSE, 
    ALCHEMY(140), 
    MULTICLASS, 
    CUSTOM;

	public static final AcquireType[] VALUES = AcquireType.values();

	private final int _id;

	private AcquireType(int id)
	{
		_id = id;
	}

	private AcquireType()
	{
		_id = ordinal();
	}

	public int getId()
	{
		return _id;
	}

	public static AcquireType getById(int id)
	{
		for(AcquireType at : VALUES)
		{
			if(at.getId() == id)
				return at;
		}
		return null;
	}
	
	public static AcquireType transferType(int classId)
	{
		switch(classId)
		{
			case 97:
				return TRANSFER_CARDINAL;
			case 105:
				return TRANSFER_EVA_SAINTS;
			case 112:
				return TRANSFER_SHILLIEN_SAINTS;
		}

		return null;
	}

	public int transferClassId()
	{
		switch(this)
		{
			case TRANSFER_CARDINAL:
				return 97;
			case TRANSFER_EVA_SAINTS:
				return 105;
			case TRANSFER_SHILLIEN_SAINTS:
				return 112;
		}

		return 0;
	}
}