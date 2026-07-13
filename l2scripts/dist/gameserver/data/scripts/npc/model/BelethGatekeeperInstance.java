package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

import bosses.NewBelethManager;

//By Evil_dnk

public final class BelethGatekeeperInstance extends NpcInstance
{

	public BelethGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("enter_beleth"))
		{
			if (player.getParty() == null || player.getParty().getCommandChannel() == null|| player.getParty().getCommandChannel().getMemberCount() < 49)
			{
				showChatWindow(player, "default/19518-1.htm", false);
				return;
			}
			else
			{
				NewBelethManager.enterBeleth(player);
				return;
			}
		}
		else if(command.equalsIgnoreCase("request_realbeleth"))
		{

			if(player.getVar("requestbeleth") == null || Long.parseLong(player.getVar("requestbeleth")) < System.currentTimeMillis())
			{
				player.setVar("requestbeleth", String.valueOf(System.currentTimeMillis() + 24 * 60 * 60 * 1000L), -1);
				NewBelethManager.setCountOfHunt(NewBelethManager.getCountOfHunt()+1);
				showChatWindow(player, "default/33898-1.htm", false);
				return;
			}
		}
		else if(command.equalsIgnoreCase("request_reward"))
		{
			if(player.getVar("requestreward") == null || Long.parseLong(player.getVar("requestreward")) > System.currentTimeMillis())
			{
				ItemFunctions.addItem(player,  37823, 1, true);
				player.setVar("requestreward", String.valueOf(System.currentTimeMillis() + 24 * 60 * 60 * 1000L), -1);
				if(Rnd.chance(1))
				{
					NewBelethManager.luckyman(player);
				}
				return;
			}
		}
		else if(command.equalsIgnoreCase("moveback"))
		{
			NewBelethManager.leaveTheCastle(player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if (getNpcId() == 33899)
		{
			if (NewBelethManager.getBelethStage() > 2)
			{
				if (player.getVar("requestreward") == null || Long.parseLong(player.getVar("requestreward")) < System.currentTimeMillis())
				{
					showChatWindow(player, "default/33899.htm", firstTalk);
				}
				else
					showChatWindow(player, "default/33899-1.htm", firstTalk);
			}
		}
		else if (getNpcId() == 33898)
		{
			if(player.getVar("requestbeleth") != null && Long.parseLong(player.getVar("requestbeleth")) > System.currentTimeMillis())
				showChatWindow(player, "default/33898-1.htm", firstTalk);
			else
				showChatWindow(player, "default/33898.htm", firstTalk);
		}
		else if (getNpcId() == 19518)
		{
			showChatWindow(player, "default/19518.htm", firstTalk);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}