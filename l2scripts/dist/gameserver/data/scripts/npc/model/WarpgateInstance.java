package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class WarpgateInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public WarpgateInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("enter"))
			player.teleToLocation(-28575, 255984, -2200);
		else if(command.startsWith("exit"))
			player.teleToLocation(111382, 219202, -3536);
		else
			super.onBypassFeedback(player, command);
	}
}