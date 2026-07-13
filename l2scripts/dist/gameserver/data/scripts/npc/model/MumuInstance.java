package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class MumuInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public MumuInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("showmovie"))
			player.startScenePlayer(SceneMovie.SINEMA_ARKAN_ENTER);
		else
			super.onBypassFeedback(player, command);
	}
}
