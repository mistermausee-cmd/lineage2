package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

import bosses.ValakasManager;

/**
 * @author Bonux
**/
public final class ValakasWatchmanInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final Location TELEPORT_POSITION1 = new Location(183813, -115157, -3303);

	public ValakasWatchmanInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
			showChatWindow(player, "default/watcher_valakas_klein001.htm", firstTalk, replace);
		else
			super.showChatWindow(player, val, firstTalk, replace);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -6)
		{
			if(ValakasManager.consumeRequiredItems(player))
				player.teleToLocation(TELEPORT_POSITION1);
			else
				showChatWindow(player, "default/watcher_valakas_klein008.htm", false);
		}
		else if(ask == 618)
		{
			if(reply == 101)
			{
				int insidePlayersCount = ValakasManager.getZone().getInsidePlayers().size(); // TODO: Учитывать количество людей в Зале Пламени.
				if(insidePlayersCount < 50)
					showChatWindow(player, "default/watcher_valakas_klein003.htm", false);
				else if(insidePlayersCount < 100)
					showChatWindow(player, "default/watcher_valakas_klein004.htm", false);
				else if(insidePlayersCount < 150)
					showChatWindow(player, "default/watcher_valakas_klein005.htm", false);
				else if(insidePlayersCount < 200)
					showChatWindow(player, "default/watcher_valakas_klein006.htm", false);
				else
					showChatWindow(player, "default/watcher_valakas_klein007.htm", false);
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}
}