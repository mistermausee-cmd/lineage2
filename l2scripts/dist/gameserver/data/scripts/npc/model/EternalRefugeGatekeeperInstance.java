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

public class EternalRefugeGatekeeperInstance extends NpcInstance
{

	private static final int Eternal = 258;

	public EternalRefugeGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("request_enter"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(Eternal))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(Eternal))
			{
				ReflectionUtils.enterReflection(player, new EternalRefuge(), Eternal);
			}
			else
			{
				showChatWindow(player, "default/30870-1.htm", false);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

}