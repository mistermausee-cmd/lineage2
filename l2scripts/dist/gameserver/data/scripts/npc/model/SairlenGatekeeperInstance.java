package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import bosses.SailrenManager;

/**
 * @author pchayka
 */

public final class SairlenGatekeeperInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int GAZKH = 8784;

	public SairlenGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("request_entrance"))
		{
			if(player.getLevel() < 75)
				showChatWindow(player, "default/32109-3.htm", false);
			else if(ItemFunctions.getItemCount(player, GAZKH) > 0)
			{
				int check = SailrenManager.canIntoSailrenLair(player);
				if(check == 1 || check == 2)
					showChatWindow(player, "default/32109-5.htm", false);
				else if(check == 3)
					showChatWindow(player, "default/32109-4.htm", false);
				else if(check == 4)
					showChatWindow(player, "default/32109-1.htm", false);
				else if(check == 0)
				{
					ItemFunctions.deleteItem(player, GAZKH, 1, true);
					SailrenManager.setSailrenSpawnTask();
					SailrenManager.entryToSailrenLair(player);
				}
			}
			else
				showChatWindow(player, "default/32109-2.htm", false);
		}
		else
			super.onBypassFeedback(player, command);
	}
}