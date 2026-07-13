package l2s.gameserver.templates.item.support;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.gameserver.templates.item.ItemGrade;


public class AttributeChanceVariation
{
	public static class AttributeChanceInfo
	{
		private final ItemGrade _grade;
		private final double _weaponChance;
		private final double _armorChance;

		public AttributeChanceInfo(ItemGrade grade, double weaponChance, double armorChance)
		{
			_grade = grade;
			_weaponChance = weaponChance;
			_armorChance = armorChance;
		}

		public ItemGrade getGrade()
		{
			return _grade;
		}

		public double getWeaponChance()
		{
			return _weaponChance;
		}

		public double getArmorChance()
		{
			return _armorChance;
		}
	}

	private final int _id;
	private final TIntObjectMap<AttributeChanceInfo> _chanceInfos = new TIntObjectHashMap<AttributeChanceInfo>();

	public AttributeChanceVariation(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public void addChanceInfo(AttributeChanceInfo info)
	{
		_chanceInfos.put(info.getGrade().ordinal(), info);
	}

	public AttributeChanceInfo getChanceInfo(ItemGrade grade)
	{
		return _chanceInfos.get(grade.ordinal());
	}
}