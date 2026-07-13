package npc.model;

import instances.Fortuna;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

public final class FortunaManagerInstance extends NpcInstance
{
	private static final int fortunaId = 179;
	
	public FortunaManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("begin"))
		{
			Reflection localReflection = player.getActiveReflection();
			if(localReflection != null)
			{
				if(player.canReenterInstance(fortunaId))
				{
					player.teleToLocation(localReflection.getTeleportLoc(), localReflection);
				}	
			}	
			else if(player.canEnterInstance(fortunaId))
			{
				ReflectionUtils.enterReflection(player, new Fortuna(), fortunaId);
			}	
		}
		else
			super.onBypassFeedback(player, command);
	}	
}