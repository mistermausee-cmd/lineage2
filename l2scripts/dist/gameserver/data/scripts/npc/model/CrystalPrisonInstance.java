package npc.model;

import java.util.Calendar;

import instances.CorallGarden;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import instances.CrystalHall;
import instances.SteamCorridor;

public class CrystalPrisonInstance extends NpcInstance
{	
	public CrystalPrisonInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("request_CrystalHall"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(163))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(163))
				ReflectionUtils.enterReflection(player, new CrystalHall(), 163);
		}
		else if(command.startsWith("request_SteamCorridor"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(164))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if(player.canEnterInstance(164))
			{
				ReflectionUtils.enterReflection(player, new SteamCorridor(), 164);
			}
		}
		else if(command.startsWith("request_CoralGarden"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(165))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if(player.canEnterInstance(165))
			{
				ReflectionUtils.enterReflection(player, new CorallGarden(), 165);
			}
		}
	}
	
	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Emerald Square", "%enter%", "request_CrystalHall");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Steam Corridor", "%enter%", "request_SteamCorridor");
			}
			else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Coral Garden", "%enter%", "request_CoralGarden");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Emerald Square", "%enter%", "request_CrystalHall");
			}
			else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Steam Corridor", "%enter%", "request_SteamCorridor");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Coral Garden", "%enter%", "request_CoralGarden");
			}
			else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Emerald Square", "%enter%", "request_CrystalHall");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Steam Corridor", "%enter%", "request_SteamCorridor");
			}
			else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Coral Garden", "%enter%", "request_CoralGarden");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Emerald Square", "%enter%", "request_CrystalHall");
			}
			else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Steam Corridor", "%enter%", "request_SteamCorridor");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Coral Garden", "%enter%", "request_CoralGarden");
			}
			else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			{
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Emerald Square", "%enter%", "request_CrystalHall");
				else
					showChatWindow(player, "default/33522.htm", firstTalk, "%instance%", "Steam Corridor", "%enter%", "request_SteamCorridor");
			}
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
