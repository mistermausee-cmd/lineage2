package npc.model;

import bosses.KelbimManager;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

//By Evil_dnk

public final class KelbimGatekeeperInstance extends NpcInstance
{
	public KelbimGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("enter_kelbim"))
		{
			KelbimManager.enterTheCastle(player);
			return;
		}
		else if(command.equalsIgnoreCase("leave_kelbim"))
		{
			KelbimManager.leaveTheCastle(player);
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}
}