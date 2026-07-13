package npc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class FantasyIslePaddyInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	// Item's
	private static final int SHINY_ELEMENTAL_SHIRT_EXCHANGE_STONE = 37723;	// Камень Обмена Сияющей Футболки Эйнхасад
	private static final int PAAGRIO_SHIRT_1 = 23240;	// Футболка Паагрио
	private static final int SAYHAS_SHIRT_1 = 23301;	// Футболка Сайхи
	private static final int EVAS_SHIRT_1 = 23304;	// Футболка Евы
	private static final int MAPHRS_SHIRT_1 = 23307;	// Футболка Мафр
	private static final int PAAGRIO_SHIRT_2 = 34623;	// Футболка Паагрио
	private static final int SAYHAS_SHIRT_2 = 34624;	// Футболка Сайхи
	private static final int EVAS_SHIRT_2 = 34625;	// Футболка Евы
	private static final int MAPHRS_SHIRT_2 = 34626;	// Футболка Мафр
	private static final int PAAGRIO_SHIRT_3 = 34770;	// Футболка Паагрио
	private static final int SAYHAS_SHIRT_3 = 34771;	// Футболка Сайхи
	private static final int EVAS_SHIRT_3 = 34772;	// Футболка Евы
	private static final int MAPHRS_SHIRT_3 = 34773;	// Футболка Мафр

	public FantasyIslePaddyInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("exchangeshirt"))
		{
			if(!st.hasMoreTokens())
				return;

			int shirtType = Integer.parseInt(st.nextToken());

			int itemId1 = 0;
			int itemId2 = 0;
			int itemId3 = 0;
			switch(shirtType) // TODO: Должны ли обмениваться Ивентовые рубашки?!?
			{
				case 1:
					itemId1 = PAAGRIO_SHIRT_1;
					itemId2 = PAAGRIO_SHIRT_2;
					itemId3 = PAAGRIO_SHIRT_3;
					break;
				case 2:
					itemId1 = SAYHAS_SHIRT_1;
					itemId2 = SAYHAS_SHIRT_2;
					itemId3 = SAYHAS_SHIRT_3;
					break;
				case 3:
					itemId1 = EVAS_SHIRT_1;
					itemId2 = EVAS_SHIRT_2;
					itemId3 = EVAS_SHIRT_3;
					break;
				case 4:
					itemId1 = MAPHRS_SHIRT_1;
					itemId2 = MAPHRS_SHIRT_2;
					itemId3 = MAPHRS_SHIRT_3;
					break;
				default:
					return;
			}

			PcInventory inventory = player.getInventory();

			inventory.writeLock();
			try
			{
				List<ItemInstance> items = new ArrayList<ItemInstance>();
				items.addAll(inventory.getItemsByItemId(itemId1));
				items.addAll(inventory.getItemsByItemId(itemId2));
				items.addAll(inventory.getItemsByItemId(itemId3));

				for(ItemInstance item : items)
				{
					if(item.getEnchantLevel() == 7) // TODO: Должно быть именно +7 или можно и выше?!?
					{
						ItemFunctions.deleteItem(player, item, 1);
						ItemFunctions.addItem(player, SHINY_ELEMENTAL_SHIRT_EXCHANGE_STONE, 1);
						// TODO: Должно ли тут быть сообщение?
						return;
					}
				}
				showChatWindow(player, "Town/FantasyIsle/" + getNpcId() + "-no_shirt.htm", false);
			}
			finally
			{
				inventory.writeUnlock();
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
