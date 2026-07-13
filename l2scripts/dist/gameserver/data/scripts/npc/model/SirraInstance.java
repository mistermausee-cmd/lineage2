package npc.model;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExChangeClientEffectInfo;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class SirraInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int[] questInstances = { 140, 138, 141 };
	private static final int[] warInstances = { 139, 144 };

	public SirraInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(ArrayUtils.contains(warInstances, getReflection().getInstancedZoneId()))
		{
			DoorInstance door = getReflection().getDoor(23140101);
			if(door.isOpen())
				return getNpcId() + "_opened.htm";
			else
				return getNpcId() + "_closed.htm";
		}
		return super.getHtmlFilename(val, player);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("teleport_in"))
		{
			for(NpcInstance n : getReflection().getNpcs())
				if(n.getNpcId() == 29179 || n.getNpcId() == 29180)
					player.sendPacket(new ExChangeClientEffectInfo(2));
			player.teleToLocation(new Location(114712, -113544, -11225));
		}
		else
			super.onBypassFeedback(player, command);
	}
}