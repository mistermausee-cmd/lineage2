package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */
public class KegorNpcInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public KegorNpcInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(getReflection().isDefault())
			return getNpcId() + "-default.htm";
		return super.getHtmlFilename(val, player);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("request_stone"))
		{
			if(player.getInventory().getCountOf(15469) == 0 && player.getInventory().getCountOf(15470) == 0)
				ItemFunctions.addItem(player, 15469, 1);
			else
				player.sendMessage("You can't take more than 1 Frozen Core.");
		}
		else
			super.onBypassFeedback(player, command);
	}
}