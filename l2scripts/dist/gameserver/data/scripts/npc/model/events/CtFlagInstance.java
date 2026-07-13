package npc.model.events;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class CtFlagInstance extends NpcInstance
{
	private final TeamType _team;

	public CtFlagInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		_team = TeamType.valueOf(getParameter("team", "NONE").toUpperCase());
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "events/ctf/";
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			if(_team == TeamType.NONE)
				return;

			if(_team == player.getTeam())
				return;

			super.showChatWindow(player, 0, firstTalk, "<?n1?>", String.valueOf(Rnd.get(100, 999)), "<?n2?>", String.valueOf(Rnd.get(100, 999)));
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
