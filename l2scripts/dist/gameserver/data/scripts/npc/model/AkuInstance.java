package npc.model;

import instances.Tauti;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.instancemanager.SoHManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.templates.InstantZoneEntryType;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author KilRoy & Bonux
 * FIXME[K] - Deprecated method getMembers()
 */
public class AkuInstance extends NpcInstance
{
	private static final long serialVersionUID = -5672768757660962094L;

	private static final int TAUTI_EXTREME_INSTANCE_ID = 219;
	private static final Location TAUTI_ROOM_TELEPORT = new Location(-147262, 211318, -10040);

	public AkuInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("request_tauti_extreme_battle"))
		{
			if(SoHManager.getCurrentStage() != 2 && !Config.ENABLE_TAUTI_FREE_ENTRANCE)
			{
				showChatWindow(player, "tauti_keeper/sofa_aku002h.htm", false);
				return;
			}

			InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(TAUTI_EXTREME_INSTANCE_ID);
			if(iz == null)
			{
				showChatWindow(player, "Error! Write to administator.", false);
				return;
			}

			InstantZoneEntryType entryType = iz.getEntryType(player);
			if(entryType == InstantZoneEntryType.COMMAND_CHANNEL)
			{
				if(player.getParty() == null)
				{
					player.sendPacket(new SystemMessage(SystemMessage.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER));
					return;
				}
				if(player.getParty().getCommandChannel() == null)
				{
					showChatWindow(player, "tauti_keeper/sofa_aku002e.htm", false);
					return;
				}
				if(!player.getParty().getCommandChannel().isLeaderCommandChannel(player))
				{
					showChatWindow(player, "tauti_keeper/sofa_aku002d.htm", false);
					return;
				}

				int channelMemberCount = player.getParty().getCommandChannel().getMemberCount();
				if(channelMemberCount > iz.getMaxParty())
				{
					showChatWindow(player, "tauti_keeper/sofa_aku002c.htm", false);
					return;
				}
				if(channelMemberCount < iz.getMinParty())
				{
					showChatWindow(player, "tauti_keeper/sofa_aku002k.htm", false);
					return;
				}

				for(Player commandChannel : player.getParty().getCommandChannel().getMembers())
				{
					if(commandChannel.getLevel() > iz.getMaxLevel() || commandChannel.getLevel() < iz.getMinLevel())
					{
						showChatWindow(player, "tauti_keeper/sofa_aku002b.htm", false);
						return;
					}
				}
			}

			Reflection reflection = player.getActiveReflection();
			if(reflection != null)
			{
				if(player.canReenterInstance(TAUTI_EXTREME_INSTANCE_ID))
					showChatWindow(player, "tauti_keeper/sofa_aku002g.htm", false);
			}
			else if(player.canEnterInstance(TAUTI_EXTREME_INSTANCE_ID))
			{
				ReflectionUtils.enterReflection(player, new Tauti(), TAUTI_EXTREME_INSTANCE_ID);
				showChatWindow(player, "tauti_keeper/sofa_aku002a.htm", false);
			}
		}
		if(command.startsWith("reenter_tauti_extreme_battle"))
		{
			Reflection reflection = player.getActiveReflection();
			if(reflection != null)
			{
				if(player.canReenterInstance(TAUTI_EXTREME_INSTANCE_ID))
				{
					Tauti instance = (Tauti) reflection;
					if(instance.getInstanceStage() == 2)
						player.teleToLocation(TAUTI_ROOM_TELEPORT, reflection);
					else
						player.teleToLocation(reflection.getTeleportLoc(), reflection);
					showChatWindow(player, "tauti_keeper/sofa_aku002f.htm", false);
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		showChatWindow(player, "tauti_keeper/sofa_aku001.htm", firstTalk);
	}
}