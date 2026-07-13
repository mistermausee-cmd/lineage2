package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import instances.EmbryoCommandPost;

/**
 * @author Bonux
 */
public class DevianneInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int INSTANT_ZONE_ID = 259;	// Embryo Command Post

	public DevianneInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -9801 && reply == 1)
		{
			Party party = player.getParty();
			if(party == null || !party.isLeader(player))
			{
				showChatWindow(player, "default/devianne_inquiry005.htm", false);
				return;
			}

			if(player.getLevel() < 100)
			{
				showChatWindow(player, "default/devianne_inquiry003.htm", false);
				return;
			}

			for(Player member : party)
			{
				if(member.getDistance(this) > 500)
				{
					showChatWindow(player, "default/devianne_inquiry004.htm", false);
					return;
				}
			}

			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(INSTANT_ZONE_ID))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(INSTANT_ZONE_ID))
				ReflectionUtils.enterReflection(player, new EmbryoCommandPost(), INSTANT_ZONE_ID);
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
			showChatWindow(player, "default/devianne_inquiry001.htm", firstTalk);
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
