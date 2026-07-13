package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 10:26/24.06.2011
 */
public class BorderOutpostDoormanInstance extends GuardInstance
{
	private static final long serialVersionUID = 1L;

	public BorderOutpostDoormanInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equals("openDoor"))
		{
			DoorInstance door = ReflectionUtils.getDoor(24170001);
			door.openMe();
		}
		else if(command.equals("closeDoor"))
		{
			DoorInstance door = ReflectionUtils.getDoor(24170001);
			door.closeMe();
		}
		else
			super.onBypassFeedback(player, command);
	}
}
