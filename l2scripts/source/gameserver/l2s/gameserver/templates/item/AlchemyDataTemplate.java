package l2s.gameserver.templates.item;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.templates.item.data.ItemData;


public final class AlchemyDataTemplate
{
	private final int _skillId;
	private final int _skillLevel;
	private final int _successRate;

	private final List<ItemData> _ingridients = new ArrayList<ItemData>();
	private final List<ItemData> _onSuccessProducts = new ArrayList<ItemData>();
	private final List<ItemData> _onFailProducts = new ArrayList<ItemData>();

	public AlchemyDataTemplate(int skillId, int skillLevel, int successRate)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
		_successRate = successRate;
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getSkillLevel()
	{
		return _skillLevel;
	}

	public int getSuccessRate()
	{
		return _successRate;
	}

	public void addIngridient(ItemData ingridient)
	{
		_ingridients.add(ingridient);
	}

	public ItemData[] getIngridients()
	{
		return _ingridients.toArray(new ItemData[_ingridients.size()]);
	}

	public void addOnSuccessProduct(ItemData product)
	{
		_onSuccessProducts.add(product);
	}

	public ItemData[] getOnSuccessProducts()
	{
		return _onSuccessProducts.toArray(new ItemData[_onSuccessProducts.size()]);
	}

	public void addOnFailProduct(ItemData product)
	{
		_onFailProducts.add(product);
	}

	public ItemData[] getOnFailProducts()
	{
		return _onFailProducts.toArray(new ItemData[_onFailProducts.size()]);
	}
}