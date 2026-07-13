package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.templates.npc.NpcTemplate;

public class CrystallGolemInstance extends NpcInstance
{	
	public CrystallGolemInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("request_walk"))
		{
			setFollowTarget(player);
			setBusy(true);
			//TODO setTitleNpcString(NpcString.GIVEN_TO_S1_, player.getName());
			//TODO setNameNpcString(NpcString.TRAITOR_CRYSTALLINE_GOLEM);
		}
	}


}
