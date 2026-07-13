package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

//By Evil_dnk

public final class MeloInstance extends NpcInstance
{

	public MeloInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("change_fo_sp"))
		{
			if(player.getInventory().getCountOf(45610) > 0)
			{
				player.addExpAndSp(0, 14000000);
				ItemFunctions.addItem(player, 45941, 1, true);
				ItemFunctions.deleteItem(player, 45610, 1, true);
				return;
			}

			else if(player.getInventory().getCountOf(45611) > 0)
			{
				player.addExpAndSp(0, 14000000);
				ItemFunctions.addItem(player, 45941, 1, true);
				ItemFunctions.deleteItem(player, 45611, 1, true);
				return;
			}
			else if(player.getInventory().getCountOf(45612) > 0)
			{
				player.addExpAndSp(0, 14000000);
				ItemFunctions.addItem(player, 45941, 1, true);
				ItemFunctions.deleteItem(player, 45612, 1, true);
				return;
			}
			else if(player.getInventory().getCountOf(45613) > 0)
			{
				player.addExpAndSp(0, 14000000);
				ItemFunctions.addItem(player, 45941, 1, true);
				ItemFunctions.deleteItem(player, 45613, 1, true);
				return;
			}
			else
			{
				showChatWindow(player, "default/34018-2.htm", false);
				return;
			}

		}
		else
			super.onBypassFeedback(player, command);
	}
}
