package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import quests._10288_SecretMission;

/**
 * @author pchayka
 */
public class PriestAquilaniInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public PriestAquilaniInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(player.getQuestState(10288) != null && player.getQuestState(10288).isCompleted())
		{
			player.sendPacket(new HtmlMessage(this, "default/32780-1.htm").setPlayVoice(firstTalk));
			return;
		}
		else
		{
			player.sendPacket(new HtmlMessage(this, "default/32780.htm").setPlayVoice(firstTalk));
			return;
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("teleport"))
		{
			player.teleToLocation(new Location(118833, -80589, -2688));
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}
}