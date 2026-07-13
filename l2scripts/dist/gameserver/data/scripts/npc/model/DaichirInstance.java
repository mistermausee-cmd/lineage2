package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;
import java.util.Calendar;

public class DaichirInstance extends NpcInstance
{	
	public DaichirInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("requestEarthWorm"))
		{
            Reflection r = player.getActiveReflection();
            if (r != null) 
			{
                if(player.canReenterInstance(246))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } 
			else if(player.canEnterInstance(246)) 
			{
                ReflectionUtils.enterReflection(player, 246);
            }
        }
		else
			super.onBypassFeedback(player, command);
	}
}
