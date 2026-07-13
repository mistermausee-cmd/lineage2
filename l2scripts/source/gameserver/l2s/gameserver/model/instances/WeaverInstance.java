package l2s.gameserver.model.instances;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

public class WeaverInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public WeaverInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();

		if(actualCommand.equalsIgnoreCase("unseal"))
		{
			int cost = Integer.parseInt(st.nextToken()); 
			int id = Integer.parseInt(st.nextToken()); 

			if(player.getAdena() < cost)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			if(!ItemFunctions.deleteItem(player, id, 1, true))
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}

			player.reduceAdena(cost, true);

			int chance = Rnd.get(RewardList.MAX_CHANCE);
			switch(id)
			{
				case 13898: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13902, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13903, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13904, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13905, 1, true);
					else
						informFail(player, id);
					break;
				case 13899: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13906, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13907, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13908, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13909, 1, true);
					else
						informFail(player, id);
					break;
				case 13900: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13910, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13911, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13912, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13913, 1, true);
					else
						informFail(player, id);
					break;
				case 13901: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13914, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13915, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13916, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13917, 1, true);
					else
						informFail(player, id);
					break;
				case 13918: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13922, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13923, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13924, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13925, 1, true);
					else
						informFail(player, id);
					break;
				case 13919: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13926, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13927, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13928, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13929, 1, true);
					else
						informFail(player, id);
					break;
				case 13920: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13930, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13931, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13932, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13933, 1, true);
					else
						informFail(player, id);
					break;
				case 13921: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 13934, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 13935, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 13936, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 13937, 1, true);
					else
						informFail(player, id);
					break;
				case 14902: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 14906, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 14907, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 14908, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 14909, 1, true);
					else
						informFail(player, id);
					break;
				case 14903: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 14910, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 14911, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 14912, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 14913, 1, true);
					else
						informFail(player, id);
					break;
				case 14904: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 14914, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 14915, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 14916, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 14917, 1, true);
					else
						informFail(player, id);
					break;
				case 14905: 
					if(chance < 350000) 
						ItemFunctions.addItem(player, 14918, 1, true);
					else if(chance < 550000) 
						ItemFunctions.addItem(player, 14919, 1, true);
					else if(chance < 650000) 
						ItemFunctions.addItem(player, 14920, 1, true);
					else if(chance < 730000) 
						ItemFunctions.addItem(player, 14921, 1, true);
					else
						informFail(player, id);
					break;
				default:
					return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void informFail(Player player, int itemId)
	{
		Functions.npcSay(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
	}
}