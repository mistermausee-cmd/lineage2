package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

//By Evil_dnk

public final class HadelInstance extends NpcInstance
{

	public HadelInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("request_teleport"))
		{
			if(player.getLevel() >= 85 && player.getClassId().isAwaked())
			{
			  player.teleToLocation(-114700, 147909, -7720);
			}
			else
				showChatWindow(player, "For players who have 85 level and awaked!", false);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
