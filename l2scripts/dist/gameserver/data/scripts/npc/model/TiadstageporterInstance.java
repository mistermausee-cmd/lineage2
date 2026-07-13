package npc.model;

import instances.AltarShilen;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Awakeninger
 */

public final class TiadstageporterInstance extends NpcInstance
{
	public TiadstageporterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("teleportme"))
		   player.teleToLocation(-245843, 220547, -12104, player.getReflection());
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		HtmlMessage htmlMessage = new HtmlMessage(getObjectId()).setPlayVoice(firstTalk);
		if (getNpcId() == 32601)
		{
			htmlMessage.setFile("teleporter/32601.htm");
		}
			player.sendPacket(htmlMessage);
	}
}