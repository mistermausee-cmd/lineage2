package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExShowCommission;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class CommissionManagerInstance extends NpcInstance
{
	public CommissionManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("ShowCommission"))
			player.sendPacket(new ExShowCommission());
		else
			super.onBypassFeedback(player, command);
	}
}