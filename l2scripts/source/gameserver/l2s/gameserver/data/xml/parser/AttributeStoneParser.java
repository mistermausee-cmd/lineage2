package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.AttributeStoneHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemQuality;
import l2s.gameserver.templates.item.support.AttributeChanceVariation;
import l2s.gameserver.templates.item.support.AttributeChanceVariation.AttributeChanceInfo;
import l2s.gameserver.templates.item.support.AttributeStone;


public class AttributeStoneParser extends AbstractParser<AttributeStoneHolder>
{
	private static AttributeStoneParser _instance = new AttributeStoneParser();

	public static AttributeStoneParser getInstance()
	{
		return _instance;
	}

	private AttributeStoneParser()
	{
		super(AttributeStoneHolder.getInstance());
	}

	@Override
    public File getXMLPath()
    {
        return new File(Config.DATAPACK_ROOT, "data/attribute_stones.xml");
    }

	@Override
    public String getDTDFileName()
    {
        return "attribute_stones.dtd";
    }

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		int defaultVariation = 0;
		int defaultEnchPowerWeapon = 0;
		int defaultEnchPowerArmor = 0;
		int defaultMaxEnchWeapon = 0;
		int defaultMaxEnchArmor = 0;
		int defaultFrstEnchPowerWeapon = 0;
		int defaultFrstEnchPowerArmor = 0;

		Element defaultElement = rootElement.element("default");
		if(defaultElement != null)
		{
			defaultVariation = Integer.parseInt(defaultElement.attributeValue("variation"));
			defaultEnchPowerWeapon = Integer.parseInt(defaultElement.attributeValue("enchant_power_weapon"));
			defaultEnchPowerArmor = Integer.parseInt(defaultElement.attributeValue("enchant_power_armor"));
			defaultMaxEnchWeapon = Integer.parseInt(defaultElement.attributeValue("max_enchant_weapon"));
			defaultMaxEnchArmor = Integer.parseInt(defaultElement.attributeValue("max_enchant_armor"));
			defaultFrstEnchPowerWeapon = defaultElement.attributeValue("first_enchant_power_weapon") == null ? defaultEnchPowerWeapon : Integer.parseInt(defaultElement.attributeValue("first_enchant_power_weapon"));
			defaultFrstEnchPowerArmor = defaultElement.attributeValue("first_enchant_power_armor") == null ? defaultEnchPowerArmor : Integer.parseInt(defaultElement.attributeValue("first_enchant_power_armor"));
		}

		for(Iterator<Element> iterator1 = rootElement.elementIterator("chance_variations"); iterator1.hasNext();)
		{
			Element element1 = iterator1.next();
			for(Iterator<Element> iterator2 = element1.elementIterator("variation"); iterator2.hasNext();)
			{
				Element element2 = iterator2.next();

				AttributeChanceVariation variation = new AttributeChanceVariation(Integer.parseInt(element2.attributeValue("id")));
				for(Iterator<Element> iterator3 = element2.elementIterator("chances"); iterator3.hasNext();)
				{
					Element element3 = iterator3.next();

					final ItemGrade grade = ItemGrade.valueOf(element3.attributeValue("grade"));
					final double weaponChance = Double.parseDouble(element3.attributeValue("weapon_chance"));
					final double armorChance = Double.parseDouble(element3.attributeValue("armor_chance"));

					variation.addChanceInfo(new AttributeChanceInfo(grade, weaponChance, armorChance));
				}
				getHolder().addChanceVariation(variation);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("attribute_stone"); iterator.hasNext();)
		{
			Element attributeStoneElement = iterator.next();
			int itemId = Integer.parseInt(attributeStoneElement.attributeValue("id"));
			l2s.gameserver.model.base.Element attrElement = l2s.gameserver.model.base.Element.valueOf(attributeStoneElement.attributeValue("element").toUpperCase());
			int variation = attributeStoneElement.attributeValue("variation") == null ? defaultVariation : Integer.parseInt(attributeStoneElement.attributeValue("variation"));
			int enchPowerWeapon = attributeStoneElement.attributeValue("enchant_power_weapon") == null ? defaultEnchPowerWeapon : Integer.parseInt(attributeStoneElement.attributeValue("enchant_power_weapon"));
			int enchPowerArmor = attributeStoneElement.attributeValue("enchant_power_armor") == null ? defaultEnchPowerArmor : Integer.parseInt(attributeStoneElement.attributeValue("enchant_power_armor"));
			int maxEnchWeapon = attributeStoneElement.attributeValue("max_enchant_weapon") == null ? defaultMaxEnchWeapon : Integer.parseInt(attributeStoneElement.attributeValue("max_enchant_weapon"));
			int maxEnchArmor = attributeStoneElement.attributeValue("max_enchant_armor") == null ? defaultMaxEnchArmor : Integer.parseInt(attributeStoneElement.attributeValue("max_enchant_armor"));
			int frstEnchPowerWeapon = attributeStoneElement.attributeValue("first_enchant_power_weapon") == null ? defaultFrstEnchPowerWeapon : Integer.parseInt(attributeStoneElement.attributeValue("first_enchant_power_weapon"));
			int frstEnchPowerArmor = attributeStoneElement.attributeValue("first_enchant_power_armor") == null ? defaultFrstEnchPowerArmor : Integer.parseInt(attributeStoneElement.attributeValue("first_enchant_power_armor"));
			ItemQuality itemType = attributeStoneElement.attributeValue("item_type") == null ? null : ItemQuality.valueOf(attributeStoneElement.attributeValue("item_type").toUpperCase());

			AttributeStone item = new AttributeStone(itemId, variation, attrElement, frstEnchPowerWeapon, frstEnchPowerArmor, enchPowerWeapon, enchPowerArmor, maxEnchWeapon, maxEnchArmor, itemType);

			getHolder().addAttributeStone(item);
		}
	}
}