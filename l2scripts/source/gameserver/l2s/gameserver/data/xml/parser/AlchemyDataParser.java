package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.AlchemyDataHolder;
import l2s.gameserver.templates.item.AlchemyDataTemplate;
import l2s.gameserver.templates.item.data.ItemData;


public final class AlchemyDataParser extends AbstractParser<AlchemyDataHolder>
{
	private static final AlchemyDataParser _instance = new AlchemyDataParser();

	public static AlchemyDataParser getInstance()
	{
		return _instance;
	}

	private AlchemyDataParser()
	{
		super(AlchemyDataHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
	    return new File(Config.DATAPACK_ROOT, "data/alchemy_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
	    return "alchemy_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			final int skill_id = Integer.parseInt(element.attributeValue("skill_id"));
			final int skill_level = Integer.parseInt(element.attributeValue("skill_level"));
			final int success_rate = Integer.parseInt(element.attributeValue("success_rate"));

			final AlchemyDataTemplate data = new AlchemyDataTemplate(skill_id, skill_level, success_rate);

			for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				if("ingridients".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("item".equalsIgnoreCase(e.getName()))
						{
							int itemId = Integer.parseInt(e.attributeValue("id"));
							int itemCount = Integer.parseInt(e.attributeValue("count"));
							data.addIngridient(new ItemData(itemId, itemCount));
						}
					}
				}
				else if("products".equalsIgnoreCase(subElement.getName()))
				{
					for(Iterator<Element> productIterator = subElement.elementIterator(); productIterator.hasNext();)
					{
						Element productElement = productIterator.next();
	
						if("on_success".equalsIgnoreCase(productElement.getName()))
						{
							for(Element e : productElement.elements())
							{
								if("item".equalsIgnoreCase(e.getName()))
								{
									int itemId = Integer.parseInt(e.attributeValue("id"));
									int itemCount = Integer.parseInt(e.attributeValue("count"));
									data.addOnSuccessProduct(new ItemData(itemId, itemCount));
								}
							}
						}
						else if("on_fail".equalsIgnoreCase(productElement.getName()))
						{
							for(Element e : productElement.elements())
							{
								if("item".equalsIgnoreCase(e.getName()))
								{
									int itemId = Integer.parseInt(e.attributeValue("id"));
									int itemCount = Integer.parseInt(e.attributeValue("count"));
									data.addOnFailProduct(new ItemData(itemId, itemCount));
								}
							}
						}
					}
				}
			}
			getHolder().addData(data);
		}
	}
}