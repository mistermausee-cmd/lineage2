package npc.model.events;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class BeerFestivalManagerInstance extends NpcInstance
{
	private static final int RECIPE_ID = 23308; // Рецепт - Эльфийский Эль
	private static final int ELVEN_RADLER = 23309; // Эльфийский Эль
	private static final int BEER_TANKARD = 23318; // Пивной Бочонок
	private static final int ADEN_WHITE_BEER = 23312; // Светлое Пиво из Адена
	private static final int GRAN_KAIN_DELIGHT = 23313; // Восторг Гран Каина

	public BeerFestivalManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("giverecipe"))
		{
			if(ItemFunctions.getItemCount(player, RECIPE_ID) > 0)
			{
				showChatWindow(player, "events/beer_festival/" + getNpcId() + "-have_recipe.htm", false);
				return;
			}
			showChatWindow(player, "events/beer_festival/" + getNpcId() + "-give_recipe.htm", false);
			ItemFunctions.addItem(player, RECIPE_ID, 1, true);
		}
		else if(cmd.startsWith("craftgrandcain"))
		{
			if(ItemFunctions.getItemCount(player, ADEN_WHITE_BEER) <= 0)
			{
				showChatWindow(player, "events/beer_festival/" + getNpcId() + "-no_white_beer.htm", false);
				return;
			}

			if(ItemFunctions.getItemCount(player, BEER_TANKARD) <= 0)
			{
				showChatWindow(player, "events/beer_festival/" + getNpcId() + "-no_ingridients.htm", false);
				return;
			}

			showChatWindow(player, "events/beer_festival/" + getNpcId() + "-give_kain_beer.htm", false);

			player.getInventory().writeLock();
			try
			{
				ItemFunctions.deleteItem(player, ADEN_WHITE_BEER, 1, true);
				ItemFunctions.deleteItem(player, BEER_TANKARD, 1, true);
				ItemFunctions.addItem(player, GRAN_KAIN_DELIGHT, 1, true);
			}
			finally
			{
				player.getInventory().writeUnlock();
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}