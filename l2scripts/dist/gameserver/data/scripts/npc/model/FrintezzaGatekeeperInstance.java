package npc.model;

import instances.Frintezza;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class FrintezzaGatekeeperInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int frintezzaIzId = 136;

	public FrintezzaGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("request_frintezza"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(frintezzaIzId))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(frintezzaIzId))
			{
				ReflectionUtils.enterReflection(player, new Frintezza(), frintezzaIzId);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}