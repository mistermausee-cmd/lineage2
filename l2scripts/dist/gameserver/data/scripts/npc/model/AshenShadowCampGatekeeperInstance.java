package npc.model;

import instances.*;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.Ref;
import java.util.ArrayList;

//By Evil_dnk

public class AshenShadowCampGatekeeperInstance extends NpcInstance
{

	private static final int Camp = 260;
	private static final int[] Shadow = {46317, 46318, 46319, 46320, 46321, 46322, 46323, 46324, 46325, 46326};

	public AshenShadowCampGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		Reflection reflection = player.getReflection();

		if(command.startsWith("request_enter"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(Camp))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if(player.canEnterInstance(Camp))
			{
				ReflectionUtils.enterReflection(player, new AshenShadowCamp(), Camp);
			}
		}
		else if(command.startsWith("exchange_shadow"))
		{
			for(ItemInstance item : player.getInventory().getItems())
			{
				if(ArrayUtils.contains(Shadow, item.getItemId()))
				{
					if(item.getEnchantLevel() >= 10 && player.getInventory().getCountOf(46395) >= 10)
					{
						showChatWindow(player, "default/34095-4.htm", false);
						return;
					}
				}
			}
			showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_start"))
		{
			final AshenShadowCamp ashen = (AshenShadowCamp) reflection;
			ashen.Start(player);
		}
		else if(command.startsWith("request_1"))
		{
			if(ItemFunctions.deleteItem(player, 46317, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46327, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_2"))
		{
			if(ItemFunctions.deleteItem(player, 46318, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46328, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_3"))
		{
			if(ItemFunctions.deleteItem(player, 46319, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46329, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_4"))
		{
			if(ItemFunctions.deleteItem(player, 46320, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46330, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_5"))
		{
			if(ItemFunctions.deleteItem(player, 46321, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46331, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_6"))
		{
			if(ItemFunctions.deleteItem(player, 46322, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46332, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_7"))
		{
			if(ItemFunctions.deleteItem(player, 46323, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46333, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_8"))
		{
			if(ItemFunctions.deleteItem(player, 46324, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46334, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_9"))
		{
			if(ItemFunctions.deleteItem(player, 46325, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46335, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_10"))
		{
			if(ItemFunctions.deleteItem(player, 46326, 1, true) && ItemFunctions.deleteItem(player, 46395, 10, true))
			{
				ItemFunctions.addItem(player, 46336, 1, true);
				return;
			}
			else
				showChatWindow(player, "default/34095-3.htm", false);
		}
		else if(command.startsWith("request_leave"))
		{
			player.teleToLocation(-14072, 122984, -3120, ReflectionManager.MAIN);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if (val == 0)
		{
			if(getNpcId() == 34101)
			{
				Reflection reflection = getReflection();

				if (reflection != null)
				{
					if (reflection instanceof AshenShadowCamp)
					{
						AshenShadowCamp ashencamp = (AshenShadowCamp) reflection;
						if (ashencamp.getStage() == 0)
							showChatWindow(player, "default/34101.htm", firstTalk);
						else
							showChatWindow(player, "default/34101-1.htm", firstTalk);
					}
				}
			}
			else if(getNpcId() == 34095)
				showChatWindow(player, "default/34095.htm", firstTalk);
			else
				super.showChatWindow(player, val, firstTalk, arg);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
