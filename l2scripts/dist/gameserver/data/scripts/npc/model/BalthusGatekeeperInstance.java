package npc.model;

import java.util.Calendar;

import bosses.NewBelethManager;
import instances.BaltusKnight;
import instances.CorallGarden;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.ReflectionUtils;

import instances.CrystalHall;
import instances.SteamCorridor;

//By Evil_dnk

public class BalthusGatekeeperInstance extends NpcInstance
{

	private static final int AntarInstance = 183;

	public BalthusGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("enter"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(AntarInstance))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if(player.canEnterInstance(AntarInstance))
			{
				ReflectionUtils.enterReflection(player, new BaltusKnight(), AntarInstance);
			}
		}
		else if(command.equalsIgnoreCase("request_reward"))
		{
			if(player.getVar("requestrewardant") == null || Long.parseLong(player.getVar("requestrewardant")) > System.currentTimeMillis())
			{
				ItemFunctions.addItem(player, 32272, 1, true);
				ItemFunctions.addItem(player, 35704, 1, true);
				player.setVar("requestrewardant", String.valueOf(System.currentTimeMillis() + 24 * 60 * 60 * 1000L), -1);
				return;
			}
		}
		else if(command.equalsIgnoreCase("request_exit"))
		{
			player.teleToLocation(130969, 114551, -3728, ReflectionManager.MAIN);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if (val == 0)
		{
			if(getNpcId() == 30755)
			{
				if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 || Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15)
				{
					showChatWindow(player, "default/30755-4.htm", firstTalk, false);
				}
				else
					showChatWindow(player, "default/30755.htm", firstTalk, false);
			}
			if(getNpcId() == 19131)
			{
				if(player.getVar("requestrewardant") == null || Long.parseLong(player.getVar("requestrewardant")) < System.currentTimeMillis())
					showChatWindow(player, "default/19131.htm", firstTalk);
				else
					showChatWindow(player, "default/19131-1.htm", firstTalk);
			}
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
