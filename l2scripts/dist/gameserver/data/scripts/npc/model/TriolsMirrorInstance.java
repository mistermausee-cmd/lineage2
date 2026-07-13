package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется телепортерами из/в Pagan Temple
 * @author SYS
 */
public class TriolsMirrorInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public TriolsMirrorInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(getNpcId() == 32040)
			player.teleToLocation(-12766, -35840, -10856); //to pagan
		else if(getNpcId() == 32039)
			player.teleToLocation(35079, -49758, -760); //from pagan
	}
}