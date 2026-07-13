package npc.model;

import instances.Kimerian;
import instances.KimerianHard;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.ReflectionUtils;

public final class KimerianManagerInstance extends NpcInstance
{
	private static final int kimerianIdl = 161;
	private static final int kimerianIdh= 162;

	public KimerianManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("kimerian_l"))
		{
			Reflection localReflection = player.getActiveReflection();
			if(ItemFunctions.deleteItem(player, 17375, 1))
			{
				if (localReflection != null)
				{
					if (player.canReenterInstance(kimerianIdl))
					{
						player.teleToLocation(localReflection.getTeleportLoc(), localReflection);
					}
				}
				else if (player.canEnterInstance(kimerianIdl))
				{
					ReflectionUtils.enterReflection(player, new Kimerian(), kimerianIdl);
				}
			}
			else
				player.sendPacket(new SystemMessagePacket(SystemMsg.C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));

		}
		else if(command.equalsIgnoreCase("kimerian_h"))
		{
			if (ItemFunctions.deleteItem(player, 17376, 1))
			{
				Reflection localReflection = player.getActiveReflection();
				if (localReflection != null)
				{
					if (player.canReenterInstance(kimerianIdh))
					{
						player.teleToLocation(localReflection.getTeleportLoc(), localReflection);
					}
				}
				else if (player.canEnterInstance(kimerianIdh))
				{
					ReflectionUtils.enterReflection(player, new KimerianHard(), kimerianIdh);
				}
			}
			else
				player.sendPacket(new SystemMessagePacket(SystemMsg.C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
		}
		else
			super.onBypassFeedback(player, command);
	}

}