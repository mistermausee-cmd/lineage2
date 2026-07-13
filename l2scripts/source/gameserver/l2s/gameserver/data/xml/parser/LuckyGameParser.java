package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.LuckyGameHolder;
import l2s.gameserver.templates.luckygame.LuckyGameData;
import l2s.gameserver.templates.luckygame.LuckyGameItem;


public final class LuckyGameParser extends AbstractParser<LuckyGameHolder>
{
	private static final LuckyGameParser _instance = new LuckyGameParser();

	public static LuckyGameParser getInstance()
	{
		return _instance;
	}

	protected LuckyGameParser()
	{
		super(LuckyGameHolder.getInstance());
	}

	@Override
    public File getXMLPath()
	{
        return new File(Config.DATAPACK_ROOT, "data/lucky_game_data.xml");
    }

	@Override
    public String getDTDFileName()
	{
        return "lucky_game_data.dtd";
    }

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		Element configElement = rootElement.element("config");
		if(configElement != null)
		{
			Config.ALLOW_LUCKY_GAME_EVENT = Boolean.parseBoolean(configElement.attributeValue("allow"));
			Config.LUCKY_GAME_UNIQUE_REWARD_GAMES_COUNT = Integer.parseInt(configElement.attributeValue("unique_reward_games_count"));
			Config.LUCKY_GAME_ADDITIONAL_REWARD_GAMES_COUNT = Integer.parseInt(configElement.attributeValue("additional_rewards_games_count"));
		}

		if(!Config.ALLOW_LUCKY_GAME_EVENT)
		{
			return;
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("game"); iterator.hasNext(); )
		{
			Element element = iterator.next();

			int gameId = Integer.parseInt(element.attributeValue("id"));
			int gamesLimit = element.attributeValue("games_limit") == null ? -1 : Integer.parseInt(element.attributeValue("games_limit"));
			String reuse = element.attributeValue("reuse") == null ? null : element.attributeValue("reuse");

			LuckyGameData data = new LuckyGameData(gameId, gamesLimit, reuse);

			for(Iterator<Element> secondIterator = element.elementIterator(); secondIterator.hasNext(); )
			{
				Element secondElement = secondIterator.next();

				if(secondElement.getName().equals("common_rewards"))
				{
					data.addCommonRewards(parseRewards(secondElement));
				}
				else if(secondElement.getName().equals("unique_rewards"))
				{
					data.addUniqueRewards(parseRewards(secondElement));
				}
				else if(secondElement.getName().equals("additional_rewards"))
				{
					data.addAdditionalRewards(parseRewards(secondElement));
				}
				else if(secondElement.getName().equals("fee"))
				{
					List<LuckyGameItem> feeItems = new ArrayList<LuckyGameItem>();
					for(Iterator<Element> itemIterator = secondElement.elementIterator("item"); itemIterator.hasNext(); )
					{
						Element itemElement = itemIterator.next();

						int itemId = Integer.parseInt(itemElement.attributeValue("id"));
						long count = Long.parseLong(itemElement.attributeValue("count"));

						feeItems.add(new LuckyGameItem(itemId, count));
					}
					data.addFeeItems(feeItems);
				}
			}

			getHolder().addData(data);
		}
	}

	private static List<LuckyGameItem> parseRewards(Element element)
	{
		List<LuckyGameItem> rewards = new ArrayList<LuckyGameItem>();
		for(Iterator<Element> iterator = element.elementIterator("item"); iterator.hasNext(); )
		{
			Element itemElement = iterator.next();

			int itemId = Integer.parseInt(itemElement.attributeValue("id"));
			long minCount = Long.parseLong(itemElement.attributeValue("min_count"));
			long maxCount = Long.parseLong(itemElement.attributeValue("max_count"));
			double chance = Double.parseDouble(itemElement.attributeValue("chance"));

			rewards.add(new LuckyGameItem(itemId, minCount, maxCount, chance));
		}
		return rewards;
	}
}