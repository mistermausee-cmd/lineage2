package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class AwakeGatekeeperInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public AwakeGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			if(player == null)
				return;

			if(!player.getClassId().isAwaked())
			{
				showChatWindow(player, "teleporter/awake_gatekeeper-no.htm", firstTalk);
				return;
			}
			if(player.getLevel() < 90)
			{
				showChatWindow(player, "teleporter/awake_gatekeeper-85.htm", firstTalk);
				return;
			}
			if(player.getLevel() >= 90)
			{
				showChatWindow(player, "teleporter/awake_gatekeeper-90.htm", firstTalk);
				return;
			}
		}
		super.showChatWindow(player, val, firstTalk, arg);
	}
}