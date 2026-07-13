package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class KamalokaGuardInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public KamalokaGuardInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("kamaloka"))
		{
			int val = Integer.parseInt(command.substring(9));
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(val))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(val))
			{
				ReflectionUtils.enterReflection(player, val);
			}
		}
		else if(command.startsWith("escape"))
		{
			if(player.getParty() == null || !player.getParty().isLeader(player))
			{
				showChatWindow(player, "not_party_leader.htm", false);
				return;
			}
			player.getReflection().collapse();
		}
		else if(command.startsWith("return"))
		{
			Reflection r = player.getReflection();
			if(r.getReturnLoc() != null)
				player.teleToLocation(r.getReturnLoc(), ReflectionManager.MAIN);
			else
				player.setReflection(ReflectionManager.MAIN);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "instance/kamaloka/";
	}
}