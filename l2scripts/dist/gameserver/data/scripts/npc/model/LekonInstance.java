package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 10:10/24.06.2011
 */
public class LekonInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int ENERGY_STAR_STONE = 13277;
	private static final int AIRSHIP_SUMMON_LICENSE = 13559;

	public LekonInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equals("get_license"))
		{
			if(player.getClan() == null || !player.isClanLeader() || player.getClan().getLevel() < 5)
			{
				showChatWindow(player, 2, false);
				return;
			}

			if(player.getClan().isHaveAirshipLicense() || ItemFunctions.getItemCount(player, AIRSHIP_SUMMON_LICENSE) > 0)
			{
				showChatWindow(player, 4, false);
				return;
			}

			if(!ItemFunctions.deleteItem(player, ENERGY_STAR_STONE, 10))
			{
				showChatWindow(player, 3, false);
				return;
			}

			ItemFunctions.addItem(player, AIRSHIP_SUMMON_LICENSE, 1);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
