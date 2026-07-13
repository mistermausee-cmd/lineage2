package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

//By Evil_dnk

public class TreasureMapInstance extends NpcInstance
{
	private int[] maps = {46090, 46095, 46091, 46094, 46092, 46093};

	public TreasureMapInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("complete_map"))
		{
			if(player.getInventory().getCountOf(46085) > 0 && player.getInventory().getCountOf(46086) > 0 && player.getInventory().getCountOf(46087) > 0 && player.getInventory().getCountOf(46088) > 0 && player.getInventory().getCountOf(46089) > 0)
			{
				ItemFunctions.deleteItem(player, 46085, 1);
				ItemFunctions.deleteItem(player, 46086, 1);
				ItemFunctions.deleteItem(player, 46087, 1);
				ItemFunctions.deleteItem(player, 46088, 1);
				ItemFunctions.deleteItem(player, 46089, 1);

				if(Rnd.chance(0.5))
					ItemFunctions.addItem(player, 46095, 1, true);
				else if(Rnd.chance(1))
					ItemFunctions.addItem(player, 46094, 1, true);
				else if(Rnd.chance(5))
					ItemFunctions.addItem(player, 46093, 1, true);
				else if(Rnd.chance(5))
					ItemFunctions.addItem(player, 46092, 1, true);
				else if(Rnd.chance(50))
					ItemFunctions.addItem(player, 46091, 1, true);
				else
					ItemFunctions.addItem(player, 46090, 1, true);

				showChatWindow(player, "fisherman/31577-2.htm", false);
				return;
			}
			else
			{
				showChatWindow(player, "fisherman/31577-1.htm", false);
				return;
			}

		}
		else if(command.equalsIgnoreCase("FishingSkillList"))
		{
			showFishingSkillList(player);
		}

		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "fisherman/";
	}
}
