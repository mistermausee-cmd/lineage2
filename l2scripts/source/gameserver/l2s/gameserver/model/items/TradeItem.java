package l2s.gameserver.model.items;


public final class TradeItem extends ItemInfo
{
	private long _price;
	private long _referencePrice;
	private long _currentValue;
	private int _lastRechargeTime;
	private int _rechargeTime;

	public TradeItem()
	{
		super();
	}

	public TradeItem(ItemInstance item)
	{
		this(item, false);
	}

	public TradeItem(ItemInstance item, boolean isBlocked)
	{
		super(item, isBlocked);
		setReferencePrice(item.getReferencePrice());
	}

	public void setOwnersPrice(long price)
	{
		_price = price;
	}

	public long getOwnersPrice()
	{
		return _price;
	}

	public void setReferencePrice(long price)
	{
		_referencePrice = price;
	}

	public long getReferencePrice()
	{
		return _referencePrice;
	}

	public long getStorePrice()
	{
		return getReferencePrice() / 2;
	}

	public void setCurrentValue(long value)
	{
		_currentValue = value;
	}

	public long getCurrentValue()
	{
		return _currentValue;
	}

	
	public void setRechargeTime(int rechargeTime)
	{
		_rechargeTime = rechargeTime;
	}

	
	public int getRechargeTime()
	{
		return _rechargeTime;
	}

	
	public boolean isCountLimited()
	{
		return getCount() > 0;
	}

	
	public void setLastRechargeTime(int lastRechargeTime)
	{
		_lastRechargeTime = lastRechargeTime;
	}

	
	public int getLastRechargeTime()
	{
		return _lastRechargeTime;
	}

	@Override
	public TradeItem clone()
	{
		TradeItem item = new TradeItem();
		item.setOwnerId(getOwnerId());
		item.setObjectId(getObjectId());
		item.setItemId(getItemId());
		item.setCount(getCount());
		item.setCustomType1(getCustomType1());
		item.setEquipped(isEquipped());
		item.setEnchantLevel(getEnchantLevel());
		item.setCustomType2(getCustomType2());
		item.setVariationStoneId(getVariationStoneId());
		item.setVariation1Id(getVariation1Id());
		item.setVariation2Id(getVariation2Id());
		item.setShadowLifeTime(getShadowLifeTime());
		item.setEquipSlot(getEquipSlot());
		item.setTemporalLifeTime(getTemporalLifeTime());
		item.setEnchantOptions(getEnchantOptions());
		item.setAttributeFire(getAttributeFire());
		item.setAttributeWater(getAttributeWater());
		item.setAttributeWind(getAttributeWind());
		item.setAttributeEarth(getAttributeEarth());
		item.setAttributeHoly(getAttributeHoly());
		item.setAttributeUnholy(getAttributeUnholy());
		item.setIsBlocked(isBlocked());
		item.setVisualId(getVisualId());
		item.setOwnersPrice(getOwnersPrice());
		item.setReferencePrice(getReferencePrice());
		item.setCurrentValue(getCurrentValue());
		item.setLastRechargeTime(getLastRechargeTime());
		item.setRechargeTime(getRechargeTime());
		return item;
	}
}