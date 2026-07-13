package npc.model.events;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Util;

/**
 * @author Bonux
**/
public class March8ManagerInstance extends NpcInstance
{
	private static final int RECIPE_PRICE = 50000; // 50.000 adena at x1 servers
	private static final int RECIPE_ID = 20191;

	public March8ManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("buyrecipe"))
		{
			long need_adena = (long) (RECIPE_PRICE * Config.EVENT_MARCH8_PRICE_RATE);
			if(player.getAdena() < need_adena)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.reduceAdena(need_adena, true);
			ItemFunctions.addItem(player, RECIPE_ID, 1);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		super.showChatWindow(player, val, firstTalk, "<?recipe_price?>", Util.formatAdena((long) (RECIPE_PRICE * Config.EVENT_MARCH8_PRICE_RATE)));
	}
}