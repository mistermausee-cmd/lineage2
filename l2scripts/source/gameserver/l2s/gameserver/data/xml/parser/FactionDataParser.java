package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import gnu.trove.map.hash.TIntIntHashMap;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.FactionDataHolder;
import l2s.gameserver.model.base.FactionType;

public final class FactionDataParser extends AbstractParser<FactionDataHolder>
{
	private static final FactionDataParser _instance = new FactionDataParser();
  
	public static FactionDataParser getInstance()
	{
	    return _instance;
	}
  
	protected FactionDataParser()
	{
		super(FactionDataHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/faction_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "faction_data.dtd";
	}

	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("faction"); iterator.hasNext();)
		{
			Element factionElement = iterator.next();
      
			FactionType factionType = FactionType.valueOf(factionElement.attributeValue("name").toUpperCase());
			TIntIntHashMap levels = new TIntIntHashMap();
			for(Iterator<Element> pointIterator = factionElement.elementIterator("point"); pointIterator.hasNext();)
			{
				Element pointElement = pointIterator.next();
				int level = Integer.parseInt(pointElement.attributeValue("level"));
				int points = Integer.parseInt(pointElement.attributeValue("value"));
				levels.put(level, points);
			}
			getHolder().addData(factionType, levels);
		}
	}
}