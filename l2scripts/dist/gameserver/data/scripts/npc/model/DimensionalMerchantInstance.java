package npc.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class DimensionalMerchantInstance extends FreightSenderInstance
{
	private static final Logger _log = LoggerFactory.getLogger(DimensionalMerchantInstance.class);

	private static final long serialVersionUID = 1L;

	// Items
	private static final int MINION_COUPON = 13273; // Minion Coupon (5-hour)
	private static final int MINION_COUPON_EV = 13383; // Minion Coupon (5-hour) (Event)
	private static final int SUP_MINION_COUPON = 14065; // Superior Minion Coupon - 5-hour
	private static final int SUP_MINION_COUPON_EV = 14074; // Superior Minion Coupon (Event) - 5-hour
	private static final int ENH_MINION_COUPON = 20914; // Enhanced Rose Spirit Coupon (5-hour)
	private static final int ENH_MINION_COUPON_EV = 22240; // Enhanced Rose Spirit Coupon (5-hour) - Event

	// Misc
	private static final Map<String, Integer> MINION_EXCHANGE = new HashMap<String, Integer>();
	{
		// Normal
		MINION_EXCHANGE.put("whiteweasel", 13017); // White Weasel Minion Necklace
		MINION_EXCHANGE.put("fairyprincess", 13018); // Fairy Princess Minion Necklace
		MINION_EXCHANGE.put("wildbeast", 13019); // Wild Beast Fighter Minion Necklace
		MINION_EXCHANGE.put("foxshaman", 13020); // Fox Shaman Minion Necklace
		// Superior
		MINION_EXCHANGE.put("toyknight", 14061); // Toy Knight Summon Whistle
		MINION_EXCHANGE.put("spiritshaman", 14062); // Spirit Shaman Summon Whistle
		MINION_EXCHANGE.put("turtleascetic", 14064); // Turtle Ascetic Summon Necklace
		// Enhanced
		MINION_EXCHANGE.put("desheloph", 20915); // Enhanced Rose Necklace: Desheloph
		MINION_EXCHANGE.put("hyum", 20916); // Enhanced Rose Necklace: Hyum
		MINION_EXCHANGE.put("lekang", 20917); // Enhanced Rose Necklace: Lekang
		MINION_EXCHANGE.put("lilias", 20918); // Enhanced Rose Necklace: Lilias
		MINION_EXCHANGE.put("lapham", 20919); // Enhanced Rose Necklace: Lapham
		MINION_EXCHANGE.put("mafum", 20920); // Enhanced Rose Necklace: Mafum
	}

	public DimensionalMerchantInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	private boolean exchangeCoupon(Player player, String event, int[] coupons)
	{
		if(MINION_EXCHANGE.containsKey(event))
		{
			for(int coupon : coupons)
			{
				if(ItemFunctions.deleteItem(player, coupon, 1))
				{
					List<ItemInstance> items = ItemFunctions.addItem(player, MINION_EXCHANGE.get(event), 1);
					if(player.getPet() == null)
					{
						for(ItemInstance item : items)
						{
							player.setPetControlItem(item);
							player.summonPet();
							break;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("exchange"))
		{
			if(!st.hasMoreTokens())
				return;

			String cmd2 = st.nextToken();
			switch(cmd2)
			{
				case "whiteweasel":
				case "fairyprincess":
				case "wildbeast":
				case "foxshaman":
				{
					if(exchangeCoupon(player, cmd2.toLowerCase(), new int[]{ MINION_COUPON, MINION_COUPON_EV }))
						showChatWindow(player, "default/" + getNpcId() + "-ok.htm", false);
					else
						showChatWindow(player, "default/" + getNpcId() + "-no.htm", false);
					break;
				}
				case "toyknight":
				case "spiritshaman":
				case "turtleascetic":
				{
					if(exchangeCoupon(player, cmd2.toLowerCase(), new int[]{ SUP_MINION_COUPON, SUP_MINION_COUPON_EV }))
						showChatWindow(player, "default/" + getNpcId() + "-ok.htm", false);
					else
						showChatWindow(player, "default/" + getNpcId() + "-no.htm", false);
					break;
				}
				case "desheloph":
				case "hyum":
				case "lekang":
				case "lilias":
				case "lapham":
				case "mafum":
				{
					if(exchangeCoupon(player, cmd2.toLowerCase(), new int[]{ ENH_MINION_COUPON, ENH_MINION_COUPON_EV }))
						showChatWindow(player, "default/" + getNpcId() + "-ok.htm", false);
					else
						showChatWindow(player, "default/" + getNpcId() + "-no_rose.htm", false);
					break;
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
