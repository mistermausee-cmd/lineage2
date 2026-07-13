package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

import bosses.AnakimManager;
import bosses.LilithManager;
import bosses.SevenSignsRaidManager;

/**
 * @author Bonux
 */
public class ZigguratInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ZigguratInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("dungeon"))
		{
			if(!st.hasMoreTokens())
				return;

			SevenSignsRaidManager raidManager = null;

			String cmd2 = st.nextToken();
			if(cmd2.equals("lilith"))
				raidManager = LilithManager.getInstance();
			else if(cmd2.equals("anakim"))
				raidManager = AnakimManager.getInstance();

			if(raidManager != null)
			{
				if(!st.hasMoreTokens())
					return;

				String cmd3 = st.nextToken();
				if(cmd3.equals("enter"))
				{
					int result = raidManager.tryEnterToDungeon(player);
					if(result == 1)
						showChatWindow(player, "default/" + getNpcId() + "-no_already_killed.htm", false);
					else if(result == 2 || result == 3)
						showChatWindow(player, "default/" + getNpcId() + "-no_level.htm", false);
					else if(result >= 4 && result <= 8)
						showChatWindow(player, "default/" + getNpcId() + "-no_commandchannel.htm", false);
				}
				else if(cmd3.equals("exit"))
					raidManager.tryExitFromDungeon(player);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
